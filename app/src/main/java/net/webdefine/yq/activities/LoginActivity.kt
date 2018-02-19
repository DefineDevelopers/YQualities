package net.webdefine.yq.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.afollestad.materialdialogs.MaterialDialog
import com.beust.klaxon.string
import com.google.gson.Gson
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import com.vk.sdk.VKSdk
import com.vk.sdk.api.*
import com.vk.sdk.api.model.VKApiUserFull
import com.vk.sdk.api.model.VKList
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.experimental.launch
import net.webdefine.yq.R
import net.webdefine.yq.db.Client
import net.webdefine.yq.db.LocalPreferences
import net.webdefine.yq.tools.Android
import net.webdefine.yq.tools.App
import net.webdefine.yq.tools.Constants.ACCESS_TOKEN
import net.webdefine.yq.tools.Constants.DB_USER_JSON
import net.webdefine.yq.tools.Constants.LOG_DB_ACCESS
import net.webdefine.yq.tools.Constants.LOG_LOGIN
import net.webdefine.yq.tools.Constants.LOG_VK_API
import net.webdefine.yq.tools.Constants.REFRESH_TOKEN
import net.webdefine.yq.tools.Constants.VK_ACCESS_TOKEN
import net.webdefine.yq.tools.Constants.VK_SCOPE_EMAIL
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.util.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        vk_si.onClick { VKSdk.login(this@LoginActivity, VK_SCOPE_EMAIL) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, object : VKCallback<VKAccessToken> {
                    override fun onResult(res: VKAccessToken) {
                        val loginDialog = MaterialDialog.Builder(this@LoginActivity)
                                .content(R.string.progress_dialog_auth)
                                .progress(true, 0)
                                .show()

                        launch(Android) {
                            val registerUserWithVkResponse = Client.registerUserWithVk(res.accessToken).await()

                            // setting up and saving tokens
                            val accessToken = registerUserWithVkResponse.string(ACCESS_TOKEN) ?: ""
                            val refreshToken = registerUserWithVkResponse.string(REFRESH_TOKEN) ?: ""
                            LocalPreferences.addString(VK_ACCESS_TOKEN, res.accessToken)
                            LocalPreferences.addString(ACCESS_TOKEN, accessToken)
                            LocalPreferences.addString(REFRESH_TOKEN, refreshToken)
                            Client.registerClientWithAccessToken(accessToken)

                            VKApi.users().get(VKParameters.from(VKApiConst.FIELDS,
                                        "city, bdate, photo_100, photo_400_orig"))
                                    .executeWithListener(object : VKRequest.VKRequestListener() {
                                override fun onComplete(response: VKResponse?) {
                                    val vkApiUser = (response!!.parsedModel as VKList<VKApiUserFull>)[0]

                                    launch(Android) {
                                        val getUserInfoResponse = Client.getUserInfo(
                                                vkApiUser.id,
                                                vkApiUser.first_name,
                                                vkApiUser.last_name,
                                                if (vkApiUser.city == null) "Не указан" else vkApiUser.city.title,
                                                calculateAge(vkApiUser.bdate),
                                                vkApiUser.photo_100,
                                                vkApiUser.photo_400_orig
                                        ).await()

                                        val getUserQualitiesResponse = Client.getUserQualities(vkApiUser.id).await()

                                        // TODO убрать это говно
                                        if (getUserInfoResponse.qualities.size < 3) {
                                            var createQualityResult = Client.createQualityForUser(
                                                    "Привлекательность",
                                                    "gmd_favorite",
                                                    "#F44336",
                                                    "#C62828"
                                            )
                                            Client.user.qualities.add(createQualityResult.await())

                                            createQualityResult = Client.createQualityForUser(
                                                    "Общительность",
                                                    "gmd_record_voice_over",
                                                    "#3F51B5",
                                                    "#283593"
                                            )
                                            Client.user.qualities.add(createQualityResult.await())

                                            createQualityResult = Client.createQualityForUser(
                                                    "Эрудиция",
                                                    "gmd_wb_incandescent",
                                                    "#4CAF50",
                                                    "#1B5E20"
                                            )
                                            Client.user.qualities.add(createQualityResult.await())
                                        } else {
                                            getUserInfoResponse.qualities = getUserQualitiesResponse
                                        }

                                        Client.user = getUserInfoResponse
                                        LocalPreferences.addString(DB_USER_JSON, Gson().toJson(Client.user))
                                        Log.d(LOG_LOGIN + LOG_DB_ACCESS, Gson().toJson(Client.user))

                                        loginDialog.dismiss()
                                        LocalPreferences.addString(REFRESH_TOKEN, refreshToken)
                                        changeActivity(ProfileActivity())
                                    }
                                }

                                override fun onError(error: VKError?) {
                                    Log.d(LOG_LOGIN + LOG_VK_API, "onError")
                                }
                            })
                        }
                    }

                    override fun onError(error: VKError?) {
                        MaterialDialog.Builder(this@LoginActivity)
                                .title("Ошибка авторизации VK")
                                .content("Произошла неизвестная ошибка при попытке авторизации через VK")
                                .show()
                    }
                })) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun calculateAge(date: String): Int {
        var birthday = date
        if (birthday[1] == '.') {
            birthday = "0" + birthday
        }
        if (birthday[4] == '.') {
            birthday = birthday.substring(0,3) + "0" + birthday.substring(3)
        }

        val day = birthday.substring(0, 2).toInt()
        val month = birthday.substring(3, 5).toInt()
        val year = birthday.substring(6).toInt()

        return getAge(year, month, day)
    }

    fun getAge(year: Int, month: Int, day: Int): Int {
        val birthDay = Calendar.getInstance()
        birthDay.set(year, month, day)
        val current = Calendar.getInstance()

        if (current.timeInMillis < birthDay.timeInMillis) {
            throw IllegalArgumentException("age < 0")
        }

        var age = current.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR)

        if (
                (birthDay.get(Calendar.MONTH) > current.get(Calendar.MONTH))   ||
                (birthDay.get(Calendar.MONTH) == current.get(Calendar.MONTH)) &&
                (birthDay.get(Calendar.DATE) > current.get(Calendar.DATE))
        ) {
            age--
        }

        return age
    }

    private fun changeActivity(activity: AppCompatActivity) {
        startActivity(Intent(App.instance, activity::class.java))
        overridePendingTransition(R.anim.right_in, R.anim.left_out)
        finish()
    }
}