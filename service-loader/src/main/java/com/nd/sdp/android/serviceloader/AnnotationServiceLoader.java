package com.nd.sdp.android.serviceloader;

import android.support.annotation.Keep;

import com.nd.sdp.android.serviceloader.internal.IServicePool;

import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ServiceConfigurationError;

/**
 * AnnotationServiceLoader
 * Created by Young on 2018/4/25.
 */
@SuppressWarnings({"unchecked", "WeakerAccess", "unused"})
@Keep
public class AnnotationServiceLoader<S> implements ServiceLoader<S> {

    private final Class<S> service;

    private LinkedHashMap<Class<? extends S>, S> providers = new LinkedHashMap<>();

    private LazyIterator lookupIterator;
    private IServicePool mServicePool;

    public void reload() {
        providers.clear();
        try {
            mServicePool = (IServicePool) Class.forName("com.nd.sdp.android.serviceloader.internal.ServicePool")
                    .newInstance();
            lookupIterator = new LazyIterator(service, mServicePool);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceConfigurationError(service.getName() + ": " + e.getMessage());
        }
    }

    public IServicePool getServicePool() {
        return mServicePool;
    }

    private AnnotationServiceLoader(Class<S> svc) {
        service = requireNonNull(svc, "Service interface cannot be null");
        reload();
    }

    private static void fail(Class<?> service, String msg, Throwable cause)
            throws ServiceConfigurationError {
        throw new ServiceConfigurationError(service.getName() + ": " + msg,
                cause);
    }

    private static void fail(Class<?> service, String msg)
            throws ServiceConfigurationError {
        throw new ServiceConfigurationError(service.getName() + ": " + msg);
    }

    private static void fail(Class<?> service, URL u, int line, String msg)
            throws ServiceConfigurationError {
        fail(service, u + ":" + line + ": " + msg);
    }

    @Override
    public Iterator<S> iterator() {
        return new Iterator<S>() {

            Iterator<Map.Entry<Class<? extends S>, S>> knownProviders
                    = providers.entrySet().iterator();

            public boolean hasNext() {
                return knownProviders.hasNext() || lookupIterator.hasNext();
            }

            public S next() {
                if (knownProviders.hasNext())
                    return knownProviders.next().getValue();
                return lookupIterator.next();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

        };
    }

    // Private inner class implementing fully-lazy provider lookup
    //
    private class LazyIterator
            implements Iterator<S> {

        Class<S> service;
        Collection<Class<S>> classes;
        Iterator<Class<S>> iterator;

        private LazyIterator(Class<S> service, IServicePool servicePool) {
            this.service = service;
            classes = servicePool.getServices(service).provide();
            iterator = classes.iterator();
        }

        private S nextService() {
            if (!iterator.hasNext())
                throw new NoSuchElementException();
            Class<? extends S> c = iterator.next();
            if (!service.isAssignableFrom(c)) {
                // Android-changed: Let the ServiceConfigurationError have a cause.
                ClassCastException cce = new ClassCastException(
                        service.getCanonicalName() + " is not assignable from " + c.getCanonicalName());
                fail(service,
                        "Provider " + c.getName() + " not a subtype", cce);
                // fail(service,
                //        "Provider " + cn  + " not a subtype");
            }
            try {
                S p = service.cast(c.newInstance());
                providers.put(c, p);
                return p;
            } catch (Throwable x) {
                fail(service,
                        "Provider " + c.getName() + " could not be instantiated",
                        x);
            }
            throw new Error();          // This cannot happen
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public S next() {
            return nextService();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    public static <S> ServiceLoader<S> load(Class<S> service) {
        return new AnnotationServiceLoader<>(service);
    }

    private static <T> T requireNonNull(T obj, @SuppressWarnings("SameParameterValue") String message) {
        if (obj == null)
            throw new NullPointerException(message);
        return obj;
    }
}
