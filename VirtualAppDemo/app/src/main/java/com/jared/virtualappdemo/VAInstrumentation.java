package com.jared.virtualappdemo;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by jared on 2018/2/23.
 */

public class VAInstrumentation extends Instrumentation {
    public static final String TAG = "VAInstrumentation";

    private Instrumentation mBase;

    public VAInstrumentation(Instrumentation base) {
        this.mBase = base;
    }

    public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options) {

        /**
         * 启动Activity的时候会调用到这里
         */
        Log.d(TAG, "====== This is hook startActivity by eastmoon! =======");

        ActivityResult result = realExecStartActivity(who, contextThread, token, target,
                intent, requestCode, options);

        return result;
    }

    private ActivityResult realExecStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options) {
        ActivityResult result = null;
        try {
            Class[] parameterTypes = {Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class,
                    int.class, Bundle.class};
            result = (ActivityResult)ReflectUtil.invoke(Instrumentation.class, mBase,
                    "execStartActivity", parameterTypes,
                    who, contextThread, token, target, intent, requestCode, options);
        } catch (Exception e) {
            if (e.getCause() instanceof ActivityNotFoundException) {
                throw (ActivityNotFoundException) e.getCause();
            }
            e.printStackTrace();
        }

        return result;
    }
}
