package com.mobile.praaktishockey

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.jakewharton.threetenabp.AndroidThreeTen
import com.mobile.praaktishockey.domain.common.TypefaceUtil
import com.mobile.praaktishockey.domain.common.pref.SettingsStorage
import io.alterac.blurkit.BlurKit
import io.fabric.sdk.android.Fabric

class PraaktisApp : Application() {

    override fun onCreate() {
        super.onCreate()
        app = this
        Fabric.with(this, Crashlytics())
        AndroidThreeTen.init(this)
        initPreferences()
        TypefaceUtil.overrideFont(this, "SERIF", "fonts/abel_regular.ttf")
        BlurKit.init(this)
    }

    private fun initPreferences() {
        SettingsStorage.initWith(this)
    }

    companion object {
        @JvmField
        var app: Application? = null
        @JvmStatic
        fun getApplication() : Application = app!!
    }
}