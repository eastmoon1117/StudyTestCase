package com.jared.processor;

import com.squareup.javapoet.ClassName;

/**
 * Created by jared on 2017/12/12.
 */

public class TypeUtils {
    public static final ClassName ANDROID_VIEW = ClassName.get("android.view", "View");
    public static final ClassName ANDROID_ON_CLICK_LISTENER = ClassName.get("android.view", "View", "OnClickListener");
    public static final ClassName FINDER = ClassName.get("com.jared.viewfinder", "Finder");
    public static final ClassName PROVIDER = ClassName.get("com.jared.viewfinder.provider", "Provider");
}
