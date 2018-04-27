package com.nd.sdp.android.serviceloader.internal;

import android.support.annotation.Keep;

@Keep
public final class ServicePool implements IServicePool {

    @Override
    public <S> IServiceProvider<S> getServices(Class<S> sClass) {
        try {
            @SuppressWarnings("unchecked") Class<IServiceProvider<S>> serviceProviderClass = (Class<IServiceProvider<S>>) Class.forName("com.nd.sdp.android.serviceloader.internal.Provider_" + sClass.getName().replace(".", "_"));
            return serviceProviderClass.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
