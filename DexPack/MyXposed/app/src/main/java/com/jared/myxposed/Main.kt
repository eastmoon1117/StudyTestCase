package com.jared.myxposed

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import android.widget.TextView
import android.widget.Toast

class Main : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        XposedBridge.log("Main: " + lpparam?.packageName)
        XposedBridge.log("Hook已经成功了")

        if (lpparam?.packageName != "com.jared.shellapk") {
            return
        }

        hookTest(lpparam)
    }

    private fun hookTest(lpparam: XC_LoadPackage.LoadPackageParam?) {
        XposedHelpers.findAndHookMethod(
            Application::class.java,
            "attach",
            Context::class.java,
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam?) {
                    val cl = (param!!.args[0] as Context).classLoader
                    var hookclass: Class<*>? = null
                    try {
                        hookclass = cl.loadClass("com.jared.apktest.MainActivity")
                    } catch (e: Exception) {
                        Log.e("MyXposed", "寻找com.jared.apktest.MainActivity报错", e)
                        return
                    }

                    Log.i("MyXposed", "寻找com.jared.apktest.MainActivity成功")
                    XposedHelpers.findAndHookMethod(hookclass, "gotoSecondActivity",
                        object : XC_MethodHook() {

                            override fun afterHookedMethod(param: MethodHookParam?) {
                                super.afterHookedMethod(param)
                                param?.thisObject?.let {
                                    Toast.makeText(it as Context, "你被劫持了哦！！！！", Toast.LENGTH_SHORT).show()
                                }
                            }
                        })

                    XposedHelpers.findAndHookMethod(hookclass, "showStr",
                        object : XC_MethodHook() {
                            override fun afterHookedMethod(param: MethodHookParam?) {
                                super.afterHookedMethod(param)
                                val result = "你被劫持了哦！！！！"
                                param?.result = result
                            }
                        })
                }
            })
    }
}
