package com.jared.viewfinder.provider;

import android.content.Context;
import android.view.View;

/**
 * Created by jared on 2017/12/12.
 */

public interface Provider {
    Context getContext(Object source);
    View findView(Object source, int id);
}
