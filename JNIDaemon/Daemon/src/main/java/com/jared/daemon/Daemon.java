package com.jared.daemon;

import android.content.Context;

public class Daemon {
    private static final String TAG = Daemon.class.getSimpleName();

    private static final String BIN_DIR_NAME = "bin";
    private static final String BINARY_NAME = "daemon";

    /**
     * 执行可执行文件
     * @param context
     * @param args 可执行文件的参数
     */
    public static void run(final Context context, final String args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                CommandForNative.installBinary(context, BIN_DIR_NAME, BINARY_NAME);
                //CommandForNative.execBinary(context, BIN_DIR_NAME, BINARY_NAME, args);
                CommandForNative.restartBinary(context, BIN_DIR_NAME, BINARY_NAME, args);
            }
        }).start();
    }
}
