package com.jared.virtualappdemo;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by jared on 2018/2/24.
 */

public class ComponentsHandler {
    public static final String TAG = "ComponentsHandler";

    private Context mContext;
    private static ComponentsHandler componentsHandler;

    public static ComponentsHandler getInstance(Context context) {
        if(componentsHandler == null) {
            componentsHandler = new ComponentsHandler(context);
        }
        return componentsHandler;
    }

    public static ComponentsHandler getInstance() {
        return componentsHandler;
    }

    public ComponentsHandler(Context context) {
        mContext = context;
    }

    public void markIntentIfNeeded(Intent intent) {
        if (intent.getComponent() == null) {
            return;
        }

        String targetPackageName = intent.getComponent().getPackageName();
        String targetClassName = intent.getComponent().getClassName();
        intent.putExtra("target.package", targetPackageName);
        intent.putExtra("target.activity", targetClassName);
        dispatchStubActivity(intent);
    }

    private void dispatchStubActivity(Intent intent) {
        String targetClassName = intent.getComponent().getClassName();
        String stubActivity = "com.jared.virtualappdemo.StubActivity";
        Log.d(TAG, String.format("dispatchStubActivity,[%s -> %s]", targetClassName, stubActivity));
        intent.setClassName(mContext, stubActivity);
    }
}
