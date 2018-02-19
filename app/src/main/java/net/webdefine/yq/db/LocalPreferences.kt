package net.webdefine.yq.db

import android.content.SharedPreferences
import net.webdefine.yq.tools.App
import net.webdefine.yq.tools.Constants.ACCESS_TOKEN
import net.webdefine.yq.tools.Constants.DB_USER_JSON
import net.webdefine.yq.tools.Constants.EMPTY
import net.webdefine.yq.tools.Constants.REFRESH_TOKEN
import net.webdefine.yq.tools.Constants.VK_ACCESS_TOKEN

object LocalPreferences {
    private var prefs: SharedPreferences

    private const val PREFERENCES_FILENAME = "net.webdefine.yq.prefs"

    init {
        this.prefs = App.instance.getSharedPreferences(PREFERENCES_FILENAME, 0)
    }

    fun addString(key: String, value: String?) {
        this.prefs.edit().putString(key, value).apply()
    }

    fun getString(key: String): String {
        return this.prefs.getString(key, EMPTY)
    }

    fun clear() {
        this.prefs.edit().remove(ACCESS_TOKEN).remove(REFRESH_TOKEN).remove(VK_ACCESS_TOKEN).remove(DB_USER_JSON).apply()
    }
}