package com.mobile.gympraaktis

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.mobile.gympraaktis.domain.getCurrentRoutine
import timber.log.Timber

class PraaktisApp : Application() {

    override fun onCreate() {
        super.onCreate()
        app = this
        AndroidThreeTen.init(this)
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

    companion object {
        @JvmField
        var app: Application? = null

        @JvmStatic
        fun getApplication(): Application = app!!

        val routine by lazy {
            getApplication().getCurrentRoutine()
        }
    }


}
