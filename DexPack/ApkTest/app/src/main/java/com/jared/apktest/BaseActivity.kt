package com.jared.apktest

import android.app.Activity
import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.os.Bundle
import java.io.File
import java.lang.Exception

abstract class BaseActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val odex = this.getDir("payload_odex", Context.MODE_PRIVATE)
        val apkFileName = odex.absolutePath + "/payload.apk"
        val dexFile = File(apkFileName)
        if (dexFile.exists())
            loadResources(apkFileName)
        super.onCreate(savedInstanceState)
    }

    //以下是加载资源
    private var mAssetManager: AssetManager? = null//资源管理器
    private var mResources: Resources? = null//资源
    private var mTheme: Resources.Theme? = null//主题

    protected fun loadResources(mDexPath: String) {
        try {
            val assetManager = AssetManager::class.java.newInstance()
            val addAssetPath = assetManager.javaClass.getMethod("addAssetPath", String::class.java)
            addAssetPath.invoke(assetManager, mDexPath)
            mAssetManager = assetManager
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val superRes = super.getResources()
        mResources = Resources(
            mAssetManager, superRes.displayMetrics,
            superRes.configuration
        )
        mTheme = mResources?.newTheme()
        mTheme?.setTo(super.getTheme())
    }

    override fun getAssets(): AssetManager {
        return mAssetManager ?: super.getAssets()
    }

    override fun getResources(): Resources {
        return mResources ?: super.getResources()
    }

//    override fun getTheme(): Resources.Theme {
//        return mTheme ?: super.getTheme()
//    }
}