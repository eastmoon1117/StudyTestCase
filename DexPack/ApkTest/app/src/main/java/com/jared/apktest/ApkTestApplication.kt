package com.jared.apktest

import android.app.Application
import android.util.Log

class ApkTestApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.i("ApkTestApplication", "Apk Test Application onCreate:$this")
    }
}
