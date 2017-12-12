package com.jared.viewfinder.provider;

import android.content.Context;
import android.view.View;

/**
 * Created by jared on 2017/12/12.
 */

public class ViewProvider implements Provider {

    @Override
    public Context getContext(Object source) {
        return ((View)source).getContext();
    }

    @Override
    public View findView(Object source, int id) {
        return ((View)source).findViewById(id);
    }
}
