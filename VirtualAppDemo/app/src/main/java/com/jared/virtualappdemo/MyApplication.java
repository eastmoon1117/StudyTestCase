package com.jared.virtualappdemo;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;

/**
 * Created by jared on 2018/2/23.
 */

public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        hookInstrumentation(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * hook掉Instrumentation
     *
     * @param base
     */
    private void hookInstrumentation(Context base) {
        try {
            /**
             * 拿到原始的 mInstrumentation字段
             */
            Instrumentation baseInstrumentation = ReflectUtil.getInstrumentation(base);
            if (baseInstrumentation.getClass().getName().contains("lbe")) {
                // reject executing in paralell space, for example, lbe.
                System.exit(0);
            }
            /**
             * 创建代理VAInstrumentation
             */
            final VAInstrumentation instrumentation = new VAInstrumentation(baseInstrumentation);
            /**
             * 获取当前activityThread对象
             */
            Object activityThread = ReflectUtil.getActivityThread(base);
            /**
             * 设置instrumentation为我们代理的对象
             */
            ReflectUtil.setInstrumentation(activityThread, instrumentation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
