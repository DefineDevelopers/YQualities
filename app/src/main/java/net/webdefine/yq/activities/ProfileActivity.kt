package net.webdefine.yq.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.view.LayoutInflaterCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import co.zsmb.materialdrawerkt.builders.accountHeader
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.builders.footer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import co.zsmb.materialdrawerkt.draweritems.divider
import co.zsmb.materialdrawerkt.draweritems.profile.profile
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.context.IconicsLayoutInflater2
import net.webdefine.yq.R
import kotlinx.coroutines.experimental.launch
import net.webdefine.yq.db.Client
import net.webdefine.yq.tools.Android
import net.webdefine.yq.tools.App
import net.webdefine.yq.tools.Constants.LOG_PROFILE
import kotlinx.android.synthetic.main.activity_profile.*
import com.mikepenz.iconics.context.IconicsContextWrapper
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        LayoutInflaterCompat.setFactory2(layoutInflater, IconicsLayoutInflater2(delegate))

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        val mainToolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(mainToolbar)

        toolbar_points_amount.text = "${Client.user.balance}Q"
        profile_user_name.text = "${Client.user.firstName} ${Client.user.lastName}"
        profile_user_age.text = "Возраст: ${Client.user.age}"
        profile_user_city.text = "{gmd_location_city}${Client.user.city}"
        Picasso.with(this)
                .load(Client.user.photoBig)
                .into(profile_photo)

        // in strings.xml:
        // <string name="welcome_messages">Hello, %1$s! You have %2$d new messages.</string>
        // here then:
        // Resources res = getResources();
        // String text = String.format(res.getString(R.string.welcome_messages), username, mailCount);

        toolbar_buy_points_icon.setImageDrawable(
                IconicsDrawable(this)
                        .icon(GoogleMaterial.Icon.gmd_control_point)
                        .color(Color.WHITE)
                        .sizeDp(22)
        )

        val navigation = drawer {
            toolbar = mainToolbar
            accountHeader {
                background = R.color.colorPrimary
                profile(Client.user.firstName + " " + Client.user.lastName) {
                    iconUrl = Client.user.photoSmall
                }
            }

            primaryItem {
                nameRes = R.string.drawer_item_friends
                identifier = 1
                iicon = GoogleMaterial.Icon.gmd_group
                iconColorRes = R.color.colorPrimary
                onClick { _ ->
                    Log.d(LOG_PROFILE, "Clicked on $identifier")
                    false
                }
            }

            primaryItem {
                nameRes = R.string.drawer_item_rating
                identifier = 2
                iicon = GoogleMaterial.Icon.gmd_equalizer
                iconColorRes = R.color.colorPrimary
                onClick { _ ->
                    Log.d(LOG_PROFILE, "Clicked on $identifier")
                    false
                }
            }

            primaryItem {
                nameRes = R.string.drawer_item_progress
                identifier = 3
                iicon = GoogleMaterial.Icon.gmd_trending_up
                iconColorRes = R.color.colorPrimary
                onClick { _ ->
                    Log.d(LOG_PROFILE, "Clicked on $identifier")
                    false
                }
            }

            divider { }

            primaryItem {
                nameRes = R.string.drawer_item_rules
                identifier = 4
                iicon = GoogleMaterial.Icon.gmd_description
                iconColorRes = R.color.colorPrimary
                onClick { _ ->
                    Log.d(LOG_PROFILE, "Clicked on $identifier")
                    false
                }
            }

            footer {
                primaryItem(R.string.drawer_item_sign_out) {
                    identifier = 5
                    iicon = GoogleMaterial.Icon.gmd_close
                    iconColorRes = R.color.colorPrimary
                    onClick { _ ->
                        launch(Android) {
                            App.clearTokens()

                            startActivity(Intent(App.instance, LoginActivity::class.java))
                            overridePendingTransition(R.anim.right_in, R.anim.left_out)
                            finish()
                        }
                        false
                    }
                }
            }

            selectedItem = -1
        }
    }
}