package com.nd.sdp.android.serviceloader.internal;

import java.util.Collection;

/**
 * IServiceProvider
 * Created by Young on 2018/4/27.
 */
public interface IServiceProvider<S> {

    Collection<Class<? extends S>> provide();

}
