package com.nd.sdp.android.serviceloader;

import com.nd.sdp.android.serviceloader.exception.FetchException;

import java.util.Iterator;

/**
 * The interface that holds all Configuration
 * Created by Young on 2018/4/25.
 */
public interface ServiceLoader<S> {

    /**
     * Return Impl Iterator(Lazy Iterator)
     *
     * @return Iterator
     */
    Iterator<S> iterator();

    /**
     * Get Impl By Service Name Annotation
     *
     * @param name 名字
     * @return Impl
     * @throws FetchException Exception that Fetch error
     */
    S get(String name) throws FetchException;

}
