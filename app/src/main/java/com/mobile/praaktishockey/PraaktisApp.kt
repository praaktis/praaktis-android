package com.mobile.praaktishockey

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.jakewharton.threetenabp.AndroidThreeTen
import com.mobile.praaktishockey.domain.common.pref.SettingsStorage
import io.fabric.sdk.android.Fabric
import timber.log.Timber

class PraaktisApp : Application() {

    override fun onCreate() {
        super.onCreate()
        app = this
        Fabric.with(this, Crashlytics())
        AndroidThreeTen.init(this)
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        initPreferences()
    }

    private fun initPreferences() {
        SettingsStorage.initWith(this)
    }

    companion object {
        @JvmField
        var app: Application? = null

        @JvmStatic
        fun getApplication(): Application = app!!
    }
}