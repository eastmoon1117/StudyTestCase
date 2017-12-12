package com.jared.viewfinder.provider;

import android.app.Activity;
import android.content.Context;
import android.view.View;

/**
 * Created by Administrator on 2017/12/12.
 */

public class ActivityProvider implements Provider {

    @Override
    public Context getContext(Object source) {
        return ((Activity) source);
    }

    @Override
    public View findView(Object source, int id) {
        return ((Activity)source).findViewById(id);
    }
}
