package com.nd.sdp.android.serviceloader.internal;

/**
 * BeanHolder
 * Created by Young on 2018/4/25.
 */
public interface IServicePool {

    <S> IServiceProvider getServices(Class<S> sClass);

}
