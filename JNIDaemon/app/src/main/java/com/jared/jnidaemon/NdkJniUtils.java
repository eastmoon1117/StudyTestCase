package com.jared.jnidaemon;

import android.util.Log;

/**
 * Created by huaixi on 2018/2/3.
 */

public class NdkJniUtils {
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native void nativeInit();
    public native void nativeClassInit();
    public native void nativeCleanup();

    public native String stringFromJNI();
    public native int sumFromJNI(int a, int b);
    public native void startThread();

    private void onCallback(int type) {
        Log.d("NdkJniUtils", "Type:" + type);
    }
}
