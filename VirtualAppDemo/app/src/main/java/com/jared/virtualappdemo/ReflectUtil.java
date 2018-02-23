package com.jared.virtualappdemo;

import android.app.Instrumentation;
import android.content.Context;
import android.support.annotation.UiThread;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by jared on 2018/2/23.
 */

public class ReflectUtil {
    public static Object sActivityThread;
    public static Instrumentation sInstrumentation;

    public static Object getField(Class clazz, Object target, String name) throws Exception {
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(target);
    }

    public static void setField(Class clazz, Object target, String name, Object value) throws Exception {
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    @SuppressWarnings("unchecked")
    public static Object invoke(Class clazz, Object target, String name, Object... args)
            throws Exception {
        Class[] parameterTypes = null;
        if (args != null) {
            parameterTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                parameterTypes[i] = args[i].getClass();
            }
        }

        Method method = clazz.getDeclaredMethod(name, parameterTypes);
        method.setAccessible(true);
        return method.invoke(target, args);
    }

    @SuppressWarnings("unchecked")
    public static Object invoke(Class clazz, Object target, String name, Class[] parameterTypes, Object... args)
            throws Exception {
        Method method = clazz.getDeclaredMethod(name, parameterTypes);
        method.setAccessible(true);
        return method.invoke(target, args);
    }

    @UiThread
    public static Object getActivityThread(Context base) {
        if (sActivityThread == null) {
            try {
                Class<?> activityThreadClazz = Class.forName("android.app.ActivityThread");
                Object activityThread = null;
                try {
                    activityThread = ReflectUtil.getField(activityThreadClazz, null, "sCurrentActivityThread");
                } catch (Exception e) {
                    // ignored
                }
                if (activityThread == null) {
                    activityThread = ((ThreadLocal<?>) ReflectUtil.getField(activityThreadClazz, null, "sThreadLocal")).get();
                }
                sActivityThread = activityThread;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return sActivityThread;
    }

    public static Instrumentation getInstrumentation(Context base) {
        if (getActivityThread(base) != null) {
            try {
                sInstrumentation = (Instrumentation) ReflectUtil.invoke(
                        sActivityThread.getClass(), sActivityThread, "getInstrumentation");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return sInstrumentation;
    }

    public static void setInstrumentation(Object activityThread, Instrumentation instrumentation) {
        try {
            ReflectUtil.setField(activityThread.getClass(), activityThread, "mInstrumentation", instrumentation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
