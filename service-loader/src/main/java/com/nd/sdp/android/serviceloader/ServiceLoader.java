package com.nd.sdp.android.serviceloader;

import java.util.Iterator;

/**
 * The interface that holds all Configuration
 * Created by Young on 2018/4/25.
 */
public interface ServiceLoader<S> {

    Iterator<S> iterator();

}
