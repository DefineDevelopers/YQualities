package net.webdefine.yq.tools

object Constants {
    /** General Purpose **/
    const val EMPTY                         = ""
    const val HTTP_REQUEST_TIMEOUT          = 20L

    /** Tokens **/
    const val ACCESS_TOKEN                  = "access_token"
    const val REFRESH_TOKEN                 = "refresh_token"
    const val VK_ACCESS_TOKEN               = "vk_access_token"

    /** SharedPreferences keys **/
    const val DB_USER_JSON                  = "user"

    /** VK API **/
    const val VK_SCOPE_EMAIL                = "email"
    const val VK_FIELD_PHOTO_SMALL          = "photo_100"
    const val VK_FIELD_PHOTO_BIG            = "photo_400_orig"

    /** Logging. Root part of tag  **/
    const val LOG_SPLASH_SCREEN             = "SplashScreen"
    const val LOG_LOGIN                     = "Login"
    const val LOG_PROFILE                   = "Profile"
    const val LOG_CLIENT                    = "Client"
    // Logging. The rest
    const val LOG_RESPONSE                  = "/Response"
    const val LOG_REGISTER                  = "/Register"
    const val LOG_VK_API                    = "/VKApi"
    const val LOG_GET_USER                  = "/GetUser"
    const val LOG_GET_USER_INFO             = "/GetUserInfo"
    const val LOG_GET_USER_QUALITIES        = "/GetUserQs"
    const val LOG_REGENERATE_REFRESH_TOKEN  = "/RegenRefrToken"
    const val LOG_REVOKE_TOKEN              = "/RevokeToken"
    const val LOG_INVALIDATE_SESSION        = "/InvalidateSes"
    const val LOG_REGISTER_VIA_VK           = "/RegisterViaVK"
    const val LOG_CREATE_QUALITY            = "/CreateQuality"
    const val LOG_DB_ACCESS                 = "/Prefs"

    /** HTTP queries parameters **/
    const val HTTP_HEADER_AUTHORIZATION     = "Authorization"
    const val HTTP_HEADER_BEARER            = "Bearer "
    const val HTTP_CLIENT_ID                = "client_id"
    const val HTTP_CLIENT_SECRET            = "client_secret"
    const val HTTP_GRANT_TYPE               = "grant_type"
    const val HTTP_TOKEN                    = "token"
    const val HTTP_REFRESH_TOKEN            = "refresh_token"
    const val HTTP_CONVERT_TOKEN            = "convert_token"
    const val HTTP_BACKEND                  = "backend"
    const val HTTP_VK_OAUTH_2               = "vk-oauth2"
    const val HTTP_NAME                     = "name"
    const val HTTP_ICON_NAME                = "icon_name"
    const val HTTP_PRIMARY_COLOR            = "primary_color"
    const val HTTP_ACCENT_COLOR             = "accent_color"

    /** General Purpose **/
    const val JSON_RESULTS                  = "results"
    const val JSON_COUNT                    = "count"
    const val JSON_UUID                     = "uuid"
    const val JSON_NAME                     = "name"
    const val JSON_ICON_NAME                = "icon_name"
    const val JSON_VALUE                    = "value"
    const val JSON_PROFILE                  = "profile"
    const val JSON_PRIMARY_COLOR            = "primary_color"
    const val JSON_ACCENT_COLOR             = "accent_color"
    const val JSON_BALANCE                  = "balance"

    /** URLs for YQualities API methods **/
    const val URL_REGENERATE_REFRESH_TOKEN  = "http://api.yq.webdefine.net/auth/token/"
    const val URL_REVOKE_TOKEN              = "http://api.yq.webdefine.net/auth/revoke-token/"
    const val URL_CONVERT_TOKEN             = "http://api.yq.webdefine.net/auth/convert-token/"
    const val URL_INVALIDATE_SESSION        = "http://api.yq.webdefine.net/auth/invalidate-sessions/"
    const val URL_PROPERTIES                = "http://api.yq.webdefine.net/properties/"
    const val URL_GET_PROPERTIES            = "http://api.yq.webdefine.net/properties/?profile="
    const val URL_GET_PROFILE               = "http://api.yq.webdefine.net/profiles/"

    /** Activities index **/
    const val LOGIN_ACTIVITY                = 0
    const val PROFILE_ACTIVITY              = 1
}