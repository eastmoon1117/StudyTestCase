package com.jared.shellapk

import android.app.Application
import android.app.Instrumentation
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import android.util.ArrayMap
import android.util.Log
import dalvik.system.DexClassLoader
import java.io.*
import java.lang.Exception
import java.lang.ref.WeakReference
import java.util.ArrayList
import java.util.zip.ZipInputStream

open class ShellApplication : Application() {

    private var odexPath: String? = null
    private var libPath: String? = null
    private var apkFileName: String? = null

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        try {
            // 创建两个文件夹payload_odex、payload_lib，私有的，可写的文件目录
            val odex = this.getDir("payload_odex", Context.MODE_PRIVATE)
            val libs = this.getDir("payload_lib", Context.MODE_PRIVATE)
            odexPath = odex.absolutePath
            libPath = libs.absolutePath
            apkFileName = odex.absolutePath + "/payload.apk"
            val dexFile = File(apkFileName)
            Log.i("ShellApplication", "Apk size:" + dexFile.length())
            if (!dexFile.exists()) {
                dexFile.createNewFile()  //在payload_odex文件夹内，创建payload.apk
                // 读取程序classes.dex文件
                val dexdata = this.readDexFileFromApk()
                Log.d("ShellApplication", "apk size: " + dexdata.size)
                // 分离出解壳后的apk文件已用于动态加载
                this.splitPayLoadFromDex(dexdata)
            }

            // 配置动态加载环境
            val currentActivityThread = RefInvoke.invokeStaticMethod(
                "android.app.ActivityThread", "currentActivityThread",
                arrayOf(), arrayOf()
            )//获取主线程对象
            val packageName = this.packageName//当前apk的包名
            val mPackages = RefInvoke.getFieldOjbect(
                "android.app.ActivityThread", currentActivityThread,
                "mPackages"
            ) as ArrayMap<*, *>
            val wr = mPackages[packageName] as WeakReference<*>?
            // 创建被加壳apk的DexClassLoader对象  加载apk内的类和本地代码（c/c++代码）
            val dLoader = DexClassLoader(
                apkFileName, odexPath,
                libPath, RefInvoke.getFieldOjbect(
                    "android.app.LoadedApk", wr!!.get(), "mClassLoader"
                ) as ClassLoader
            )
            //把当前进程的mClassLoader设置成了被加壳apk的DexClassLoader
            RefInvoke.setFieldOjbect(
                "android.app.LoadedApk", "mClassLoader",
                wr.get(), dLoader
            )

            Log.i("ShellApplication", "classloader:$dLoader")

            try {
                val actObj = dLoader.loadClass("com.jared.apktest.MainActivity")
                Log.i("ShellApplication", "actObj:$actObj")
            } catch (e: Exception) {
                Log.i("ShellApplication", "activity:" + Log.getStackTraceString(e))
            }
        } catch (e: Exception) {
            Log.i("ShellApplication", "error:" + Log.getStackTraceString(e))
            e.printStackTrace()
        }
    }

    override fun onCreate() {
        Log.i("ShellApplication", "onCreate")

        apkFileName?.let {
            loadResources(it)
        }

        // 如果源应用配置有Appliction对象，则替换为源应用Applicaiton，以便不影响源程序逻辑。
        var appClassName: String? = null
        try {
            val ai = this.packageManager
                .getApplicationInfo(
                    this.packageName,
                    PackageManager.GET_META_DATA
                )
            val bundle = ai.metaData
            if (bundle != null && bundle.containsKey("APPLICATION_CLASS_NAME")) {
                appClassName = bundle.getString("APPLICATION_CLASS_NAME") // className 是配置在xml文件中的。
            } else {
                Log.i("ShellApplication", "have no application class name")
                return
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.i("ShellApplication", "error:" + Log.getStackTraceString(e))
            e.printStackTrace()
        }

        //有值的话调用该Applicaiton
        val currentActivityThread = RefInvoke.invokeStaticMethod(
            "android.app.ActivityThread", "currentActivityThread",
            arrayOf(), arrayOf()
        )
        val mBoundApplication = RefInvoke.getFieldOjbect(
            "android.app.ActivityThread", currentActivityThread,
            "mBoundApplication"
        )
        val loadedApkInfo = RefInvoke.getFieldOjbect(
            "android.app.ActivityThread\$AppBindData",
            mBoundApplication, "info"
        )
        //把当前进程的mApplication 设置成了null
        RefInvoke.setFieldOjbect(
            "android.app.LoadedApk", "mApplication",
            loadedApkInfo, null
        )
        val oldApplication = RefInvoke.getFieldOjbect(
            "android.app.ActivityThread", currentActivityThread,
            "mInitialApplication"
        )
        val mAllApplications = RefInvoke
            .getFieldOjbect(
                "android.app.ActivityThread",
                currentActivityThread, "mAllApplications"
            ) as ArrayList<*>
        mAllApplications.remove(oldApplication) // 删除oldApplication

        val appInfoInLoadedApk = RefInvoke
            .getFieldOjbect(
                "android.app.LoadedApk", loadedApkInfo,
                "mApplicationInfo"
            ) as ApplicationInfo
        val appInfoInAppBindData = RefInvoke
            .getFieldOjbect(
                "android.app.ActivityThread\$AppBindData",
                mBoundApplication, "appInfo"
            ) as ApplicationInfo
        appInfoInLoadedApk.className = appClassName
        appInfoInAppBindData.className = appClassName
        val app = RefInvoke.invokeMethod(
            "android.app.LoadedApk", "makeApplication", loadedApkInfo,
            arrayOf<Class<*>>(Boolean::class.javaPrimitiveType!!, Instrumentation::class.java),
            arrayOf<Any?>(false, null)
        ) as Application // 执行 makeApplication（false,null）

        RefInvoke.setFieldOjbect(
            "android.app.ActivityThread",
            "mInitialApplication", currentActivityThread, app
        )

        val mProviderMap = RefInvoke.getFieldOjbect(
            "android.app.ActivityThread", currentActivityThread,
            "mProviderMap"
        ) as ArrayMap<*, *>
        val it = mProviderMap.values.iterator()
        while (it.hasNext()) {
            val providerClientRecord = it.next()
            val localProvider = RefInvoke.getFieldOjbect(
                "android.app.ActivityThread\$ProviderClientRecord",
                providerClientRecord, "mLocalProvider"
            )
            if (localProvider != null) {
                RefInvoke.setFieldOjbect(
                    "android.content.ContentProvider",
                    "mContext", localProvider, app
                )
            }
        }

        Log.i("ShellApplication", "app:$app")
        app.onCreate()
    }

    /**
     * 从apk包里获取dex文件内容
     */
    private fun readDexFileFromApk(): ByteArray {
        val dexByteArrayOutputStream = ByteArrayOutputStream()
        val localZipInputStream = ZipInputStream(
            BufferedInputStream(
                FileInputStream(
                    this.applicationInfo.sourceDir
                )
            )
        )
        while (true) {
            val localZipEntry = localZipInputStream.nextEntry
            if (localZipEntry == null) {
                localZipInputStream.close()
                break
            }
            if (localZipEntry.name == "classes.dex") {
                val arrayOfByte = ByteArray(1024)
                while (true) {
                    val i = localZipInputStream.read(arrayOfByte)
                    if (i == -1)
                        break
                    dexByteArrayOutputStream.write(arrayOfByte, 0, i)
                }
            }
            localZipInputStream.closeEntry()
        }
        localZipInputStream.close()
        return dexByteArrayOutputStream.toByteArray()
    }

    private fun splitPayLoadFromDex(apkdata: ByteArray) {
        val ablen = apkdata.size
        //取被加壳apk的长度   这里的长度取值，对应加壳时长度的赋值都可以做些简化
        val dexlen = ByteArray(4)
        System.arraycopy(apkdata, ablen - 4, dexlen, 0, 4)
        val bais = ByteArrayInputStream(dexlen)
        val inS = DataInputStream(bais)
        val readInt = inS.readInt()
        println(Integer.toHexString(readInt))
        var newdex = ByteArray(readInt)
        //把被加壳的源程序apk内容拷贝到newdex中
        System.arraycopy(apkdata, ablen - 4 - readInt, newdex, 0, readInt)
        //这里应该加上对于apk的解密操作，若加壳是加密处理的话

        // 对源程序Apk进行解密
        newdex = decrypt(newdex)

        // 写入apk文件
        val file = File(apkFileName)
        try {
            val localFileOutputStream = FileOutputStream(file)
            localFileOutputStream.write(newdex)
            localFileOutputStream.close()
        } catch (localIOException: IOException) {
            throw RuntimeException(localIOException)
        }

        // 分析被加壳的apk文件
        val localZipInputStream = ZipInputStream(
            BufferedInputStream(FileInputStream(file))
        )
        while (true) {
            val localZipEntry = localZipInputStream.nextEntry // 这个也遍历子目录
            if (localZipEntry == null) {
                localZipInputStream.close()
                break
            }
            // 取出被加壳apk用到的so文件，放到libPath中（data/data/包名/payload_lib)
            val name = localZipEntry.name
            if (name.startsWith("lib/") && name.endsWith(".so")) {
                val storeFile = File(
                    libPath + "/"
                            + name.substring(name.lastIndexOf('/'))
                )
                storeFile.createNewFile()
                val fos = FileOutputStream(storeFile)
                val arrayOfByte = ByteArray(1024)
                while (true) {
                    val i = localZipInputStream.read(arrayOfByte)
                    if (i == -1)
                        break
                    fos.write(arrayOfByte, 0, i)
                }
                fos.flush()
                fos.close()
            }
            localZipInputStream.closeEntry()
        }
        localZipInputStream.close()
    }

    private fun decrypt(srcdata: ByteArray): ByteArray {
        for (i in srcdata.indices) {
            srcdata[i] = 0xFF.xor(srcdata[i].toInt()).toByte()
        }
        return srcdata
    }

    //以下是加载资源
    private var mAssetManager: AssetManager? = null//资源管理器
    private var mResources: Resources? = null//资源
    private var mTheme: Resources.Theme? = null//主题

//    private fun loadResources(dexPath: String) {
//        try {
//            val assetManager = AssetManager::class.java.newInstance()
//            val addAssetPath = assetManager.javaClass.getMethod("addAssetPath", String::class.java)
//            addAssetPath.invoke(assetManager, dexPath)
//            mAssetManager = assetManager
//        } catch (e: Exception) {
//            Log.i("ShellApplication", "loadResource error:" + Log.getStackTraceString(e))
//            e.printStackTrace()
//        }
//
//        val superRes = super.getResources()
//        superRes.displayMetrics
//        superRes.configuration
//        mResources = Resources(mAssetManager, superRes.displayMetrics, superRes.configuration)
//        mTheme = mResources!!.newTheme()
//        mTheme!!.setTo(super.getTheme())
//    }

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

    override fun getTheme(): Resources.Theme {
        return mTheme ?: super.getTheme()
    }
}