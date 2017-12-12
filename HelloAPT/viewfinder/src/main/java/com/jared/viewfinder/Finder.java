package com.jared.viewfinder;

import com.jared.viewfinder.provider.Provider;

/**
 * Created by jared on 2017/12/12.
 */

public interface Finder<T> {
    void inject(T host, Object source, Provider provider);
}
