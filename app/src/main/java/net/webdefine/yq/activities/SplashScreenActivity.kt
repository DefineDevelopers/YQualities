package net.webdefine.yq.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.beust.klaxon.string
import com.google.gson.Gson
import kotlinx.coroutines.experimental.launch
import net.webdefine.yq.R
import net.webdefine.yq.db.Client
import net.webdefine.yq.db.LocalPreferences
import net.webdefine.yq.model.User
import net.webdefine.yq.tools.Android
import net.webdefine.yq.tools.App
import net.webdefine.yq.tools.Constants.ACCESS_TOKEN
import net.webdefine.yq.tools.Constants.DB_USER_JSON
import net.webdefine.yq.tools.Constants.EMPTY
import net.webdefine.yq.tools.Constants.LOG_DB_ACCESS
import net.webdefine.yq.tools.Constants.LOG_SPLASH_SCREEN
import net.webdefine.yq.tools.Constants.REFRESH_TOKEN
import net.webdefine.yq.tools.Constants.VK_ACCESS_TOKEN

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        if (LocalPreferences.getString(REFRESH_TOKEN) != EMPTY) {
            App.vkAccessToken = LocalPreferences.getString(VK_ACCESS_TOKEN)
            App.accessToken   = LocalPreferences.getString(ACCESS_TOKEN)
            App.refreshToken  = LocalPreferences.getString(REFRESH_TOKEN)

            launch(Android) {
                val regenerateRefreshTokenResponse = Client.regenerateRefreshToken(App.refreshToken).await()
                if (regenerateRefreshTokenResponse.containsKey("error")) {
                    App.clearTokens()
                    changeActivity(LoginActivity())
                }
                else {
                    App.refreshToken = regenerateRefreshTokenResponse.string(REFRESH_TOKEN) ?: EMPTY
                    LocalPreferences.addString(REFRESH_TOKEN, App.refreshToken)

                    Client.registerClientWithAccessToken(App.accessToken)
                    if (LocalPreferences.getString(DB_USER_JSON) != EMPTY) {
                        Client.user = Gson().fromJson(LocalPreferences.getString(DB_USER_JSON), User::class.java)
                        Log.d(LOG_SPLASH_SCREEN + LOG_DB_ACCESS, LocalPreferences.getString(DB_USER_JSON))
                    }
                    else {
                        Log.d(LOG_SPLASH_SCREEN + LOG_DB_ACCESS, "No such key. Clearing tokens")
                        App.clearTokens()
                        changeActivity(LoginActivity())
                    }

                    changeActivity(ProfileActivity())
                }
            }
        }
        else {
            changeActivity(LoginActivity())
        }
    }

    private fun changeActivity(activity: AppCompatActivity) {
        startActivity(Intent(App.instance, activity::class.java))
        overridePendingTransition(R.anim.right_in, R.anim.left_out)
        finish()
    }
}
