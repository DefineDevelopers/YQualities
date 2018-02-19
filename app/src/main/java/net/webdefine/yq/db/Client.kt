package net.webdefine.yq.db

import android.util.Log
import com.beust.klaxon.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import net.webdefine.yq.R
import net.webdefine.yq.model.Quality
import net.webdefine.yq.model.User
import net.webdefine.yq.tools.App
import net.webdefine.yq.tools.Constants.EMPTY
import net.webdefine.yq.tools.Constants.HTTP_ACCENT_COLOR
import net.webdefine.yq.tools.Constants.HTTP_BACKEND
import net.webdefine.yq.tools.Constants.HTTP_CLIENT_ID
import net.webdefine.yq.tools.Constants.HTTP_CLIENT_SECRET
import net.webdefine.yq.tools.Constants.HTTP_CONVERT_TOKEN
import net.webdefine.yq.tools.Constants.JSON_COUNT
import net.webdefine.yq.tools.Constants.HTTP_GRANT_TYPE
import net.webdefine.yq.tools.Constants.HTTP_HEADER_AUTHORIZATION
import net.webdefine.yq.tools.Constants.HTTP_HEADER_BEARER
import net.webdefine.yq.tools.Constants.HTTP_ICON_NAME
import net.webdefine.yq.tools.Constants.HTTP_NAME
import net.webdefine.yq.tools.Constants.HTTP_PRIMARY_COLOR
import net.webdefine.yq.tools.Constants.HTTP_REFRESH_TOKEN
import net.webdefine.yq.tools.Constants.HTTP_REQUEST_TIMEOUT
import net.webdefine.yq.tools.Constants.HTTP_TOKEN
import net.webdefine.yq.tools.Constants.HTTP_VK_OAUTH_2
import net.webdefine.yq.tools.Constants.JSON_ACCENT_COLOR
import net.webdefine.yq.tools.Constants.JSON_BALANCE
import net.webdefine.yq.tools.Constants.JSON_ICON_NAME
import net.webdefine.yq.tools.Constants.JSON_NAME
import net.webdefine.yq.tools.Constants.JSON_PRIMARY_COLOR
import net.webdefine.yq.tools.Constants.JSON_PROFILE
import net.webdefine.yq.tools.Constants.JSON_RESULTS
import net.webdefine.yq.tools.Constants.JSON_UUID
import net.webdefine.yq.tools.Constants.JSON_VALUE
import net.webdefine.yq.tools.Constants.LOG_CLIENT
import net.webdefine.yq.tools.Constants.LOG_CREATE_QUALITY
import net.webdefine.yq.tools.Constants.LOG_GET_USER_INFO
import net.webdefine.yq.tools.Constants.LOG_GET_USER_QUALITIES
import net.webdefine.yq.tools.Constants.LOG_INVALIDATE_SESSION
import net.webdefine.yq.tools.Constants.LOG_REGENERATE_REFRESH_TOKEN
import net.webdefine.yq.tools.Constants.LOG_REGISTER_VIA_VK
import net.webdefine.yq.tools.Constants.LOG_REVOKE_TOKEN
import net.webdefine.yq.tools.Constants.REFRESH_TOKEN
import net.webdefine.yq.tools.Constants.URL_CONVERT_TOKEN
import net.webdefine.yq.tools.Constants.URL_GET_PROFILE
import net.webdefine.yq.tools.Constants.URL_GET_PROPERTIES
import net.webdefine.yq.tools.Constants.URL_INVALIDATE_SESSION
import net.webdefine.yq.tools.Constants.URL_PROPERTIES
import net.webdefine.yq.tools.Constants.URL_REGENERATE_REFRESH_TOKEN
import net.webdefine.yq.tools.Constants.URL_REVOKE_TOKEN
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.ProtocolException
import java.util.concurrent.TimeUnit

object Client {
    private var client = OkHttpClient.Builder()
            .connectTimeout(HTTP_REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(HTTP_REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(HTTP_REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .build()

    private val parser = Parser()
    private var accessToken: String = EMPTY
    var user: User = User(-1, "", "", "", 0,"", "", 0, ArrayList())

    fun registerClientWithAccessToken(accessToken: String) {
        this.accessToken = accessToken
    }

    fun regenerateRefreshToken(refreshToken: String): Deferred<JsonObject> {
        return async(CommonPool) {
            val requestBody = FormBody.Builder()
                    .add(HTTP_CLIENT_ID, App.instance.getString(R.string.client_id))
                    .add(HTTP_CLIENT_SECRET, App.instance.getString(R.string.client_secret))
                    .add(HTTP_GRANT_TYPE, REFRESH_TOKEN)
                    .add(HTTP_REFRESH_TOKEN, refreshToken)
                    .build()

            val request = Request.Builder()
                    .url(URL_REGENERATE_REFRESH_TOKEN)
                    .post(requestBody)
                    .build()

            val response = client.newCall(request).execute()
            val responseData = response.body()?.string()

            parser.parse(StringBuilder(responseData!!)).apply {
                Log.d(LOG_CLIENT + LOG_REGENERATE_REFRESH_TOKEN, (this as JsonObject).toJsonString(true))
            } as JsonObject
        }
    }

    fun revokeToken(token: String) : Deferred<Unit> {
        return async(CommonPool) {
            val requestBody = FormBody.Builder()
                    .add(HTTP_CLIENT_ID, App.instance.getString(R.string.client_id))
                    .add(HTTP_CLIENT_SECRET, App.instance.getString(R.string.client_secret))
                    .add(HTTP_TOKEN, token)
                    .build()

            val request = Request.Builder()
                    .url(URL_REVOKE_TOKEN)
                    .post(requestBody)
                    .build()

            Log.d(LOG_CLIENT + LOG_REVOKE_TOKEN, request.toString())

            try {
                client.newCall(request).execute()
            } catch (e: ProtocolException) {
                Log.e(LOG_CLIENT + LOG_REVOKE_TOKEN, e.message)
            }
            Unit
        }
    }

    fun invalidateSession() : Deferred<Unit> {
        return async(CommonPool) {
            val requestBody = FormBody.Builder()
                    .add(HTTP_CLIENT_ID, App.instance.getString(R.string.client_id))
                    .build()

            val request = Request.Builder()
                    .url(URL_INVALIDATE_SESSION)
                    .header(HTTP_HEADER_AUTHORIZATION, HTTP_HEADER_BEARER + accessToken)
                    .post(requestBody)
                    .build()

            Log.d(LOG_CLIENT + LOG_INVALIDATE_SESSION, request.toString())

            try {
                client.newCall(request).execute()
            } catch (e: ProtocolException) {
                Log.e(LOG_CLIENT + LOG_INVALIDATE_SESSION, e.message)
            }
            Unit
        }
    }

    fun registerUserWithVk(accessToken: String): Deferred<JsonObject> {
        return async(CommonPool) {
            val requestBody = FormBody.Builder()
                    .add(HTTP_CLIENT_ID, App.instance.getString(R.string.client_id))
                    .add(HTTP_CLIENT_SECRET, App.instance.getString(R.string.client_secret))
                    .add(HTTP_GRANT_TYPE, HTTP_CONVERT_TOKEN)
                    .add(HTTP_BACKEND, HTTP_VK_OAUTH_2)
                    .add(HTTP_TOKEN, accessToken)
                    .build()

            val request = Request.Builder()
                    .url(URL_CONVERT_TOKEN)
                    .post(requestBody)
                    .build()

            val response = client.newCall(request).execute()
            val responseData = response.body()?.string()

            parser.parse(StringBuilder(responseData!!)).apply {
                Log.d(LOG_CLIENT + LOG_REGISTER_VIA_VK, (this as JsonObject).toJsonString(true))
            } as JsonObject
        }
    }

    fun createQualityForUser(name: String, iconName: String, primaryColor: String, accentColor: String): Deferred<Quality> {
        return async(CommonPool) {
            val requestBody = FormBody.Builder()
                    .add(HTTP_NAME, name)
                    .add(HTTP_ICON_NAME, iconName)
                    .add(HTTP_PRIMARY_COLOR, primaryColor)
                    .add(HTTP_ACCENT_COLOR, accentColor)
                    .build()

            val request = Request.Builder()
                    .url(URL_PROPERTIES)
                    .header(HTTP_HEADER_AUTHORIZATION, HTTP_HEADER_BEARER + accessToken)
                    .post(requestBody)
                    .build()

            val response = client.newCall(request).execute()
            val responseData = response.body()?.string()

            val parsed = parser.parse(StringBuilder(String(responseData!!.toByteArray()))).apply {
                Log.d(LOG_CLIENT + LOG_CREATE_QUALITY, (this as JsonObject).toJsonString(true))
            } as JsonObject

            val uuid = parsed.string(JSON_UUID) as String

            Quality(uuid, user.id, name, iconName, primaryColor, accentColor, 0)
        }
    }

    fun getUserInfo(id: Int,
                    firstName: String,
                    lastName: String,
                    city: String,
                    age: Int,
                    photoS: String,
                    photoB: String): Deferred<User> {
        return async(CommonPool) {
            val request = Request.Builder()
                    .url(URL_GET_PROFILE + id.toString())
                    .header(HTTP_HEADER_AUTHORIZATION, HTTP_HEADER_BEARER + accessToken)
                    .get()
                    .build()

            val response = client.newCall(request).execute()
            val responseData = response.body()?.string()

            val parsed = parser.parse(StringBuilder(String(responseData!!.toByteArray()))).apply {
                Log.d(LOG_CLIENT + LOG_GET_USER_INFO, (this as JsonObject).toJsonString(true))
            } as JsonObject

            val balance = parsed.int(JSON_BALANCE) as Int


            User(id, firstName, lastName, city, age, photoS, photoB, balance, ArrayList())
        }
    }

    fun getUserQualities(id: Int): Deferred<MutableList<Quality>> {
        return async(CommonPool) {
            val requestBuilder  = Request.Builder()
                    .url(URL_GET_PROPERTIES + id)
                    .get()

            val response = client.newCall(requestBuilder.build()).execute()
            val responseData = response.body()?.string()

            val parsed = parser.parse(StringBuilder(String(responseData!!.toByteArray()))).apply {
                //Log.d(LOG_CLIENT + LOG_GET_USER_QUALITIES, (this as JsonObject).toJsonString(true))
            } as JsonObject

            val count = parsed.int(JSON_COUNT) as Int

            val qualities: MutableList<Quality> = ArrayList()
            for (i in IntRange(0, count - 1)) {
                val currentElementInResultsArray = parsed.array<JsonObject>(JSON_RESULTS)?.get(i)

                val uuid = currentElementInResultsArray?.string(JSON_UUID) as String
                val name = currentElementInResultsArray.string(JSON_NAME) as String
                val iconName = currentElementInResultsArray.string(JSON_ICON_NAME) as String
                val value = currentElementInResultsArray.int(JSON_VALUE) as Int
                val profile = currentElementInResultsArray.int(JSON_PROFILE) as Int
                val colorPrimary = currentElementInResultsArray.string(JSON_PRIMARY_COLOR) as String
                val colorAccent = currentElementInResultsArray.string(JSON_ACCENT_COLOR) as String

                qualities.add(Quality(uuid, profile, name, iconName, colorPrimary, colorAccent, value))
            }

            qualities
        }
    }
}