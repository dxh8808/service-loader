package com.nd.sdp.android.serviceloader.exception;

/**
 * ConfigurationFetchException
 * Created by Young on 2018/4/25.
 */
public class ConfigurationFetchException extends IllegalArgumentException {

    public ConfigurationFetchException(String s) {
        super(s);
    }

    public ConfigurationFetchException(Throwable throwable) {
        super(throwable);
    }

}
