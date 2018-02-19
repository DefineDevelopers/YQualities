package net.webdefine.yq.tools

import android.support.multidex.MultiDexApplication
import android.support.v7.app.AppCompatDelegate
import com.afollestad.materialdialogs.MaterialDialog
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKAccessTokenTracker
import com.vk.sdk.VKSdk
import kotlinx.coroutines.experimental.launch
import net.webdefine.yq.db.Client
import net.webdefine.yq.db.LocalPreferences
import net.webdefine.yq.delegates.DelegatesExt
import net.webdefine.yq.tools.Constants.EMPTY
import co.zsmb.materialdrawerkt.imageloader.drawerImageLoader
import com.mikepenz.materialdrawer.util.DrawerUIUtils
import com.squareup.picasso.Picasso


class App : MultiDexApplication() {
    companion object {
        var instance: App by DelegatesExt.notNullSingleValue()

        var vkAccessToken: String = EMPTY
        var accessToken: String = EMPTY
        var refreshToken: String = EMPTY

        internal var vkAccessTokenTracker: VKAccessTokenTracker = object : VKAccessTokenTracker() {
            override fun onVKAccessTokenChanged(oldToken: VKAccessToken?, newToken: VKAccessToken?) {
                if (newToken == null) {
                    MaterialDialog.Builder(instance)
                            .title("Ошибка авторизации VK")
                            .content("Произошла внутрення ошибка при авторизации в VK")
                            .show()
                }
            }
        }

        fun clearTokens() {
            launch(Android) {
                LocalPreferences.clear() // locally
                Client.invalidateSession().await() // from server
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        vkAccessTokenTracker.startTracking()
        VKSdk.initialize(this)

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        drawerImageLoader {
            placeholder { ctx, tag ->
                DrawerUIUtils.getPlaceHolder(ctx)
            }
            set { imageView, uri, placeholder, tag ->
                Picasso.with(imageView.context)
                        .load(uri)
                        .placeholder(placeholder)
                        .into(imageView)
            }
            cancel { imageView ->
                Picasso.with(imageView.context)
                        .cancelRequest(imageView)
            }
        }
    }
}