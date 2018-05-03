package com.nd.sdp.android.serviceloader;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.util.Log;

import com.nd.sdp.android.serviceloader.annotation.ServiceName;
import com.nd.sdp.android.serviceloader.exception.FetchException;
import com.nd.sdp.android.serviceloader.internal.IServiceProvider;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ServiceConfigurationError;

/**
 * AnnotationServiceLoader
 * Created by Young on 2018/4/25.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
@Keep
public class AnnotationServiceLoader<S> implements ServiceLoader<S> {

    private final Class<S> service;

    private Collection<Class<? extends S>> classes;

    private Map<String, Class<? extends S>> classNameMap;

    private LazyIterator lookupIterator;

    private AnnotationServiceLoader(Class<S> svc) {
        service = requireNonNull(svc, "Service interface cannot be null");
    }

    public void reload() {
        try {
            IServiceProvider<S> serviceProvider = getServiceProvider();
            classes = serviceProvider.provide();
            lookupIterator = new LazyIterator(service);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceConfigurationError(service.getName() + ": " + e.getMessage());
        }
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

    public S get(String name) throws FetchException {
        try {
            init();
            initMap();
            Class<? extends S> implClass = classNameMap.get(name);
            return implClass.newInstance();
        } catch (Exception e) {
            throw new FetchException(e);
        }
    }

    @Override
    public Iterator<S> iterator() {
        // 延迟初始化
        init();
        return new Iterator<S>() {

            public boolean hasNext() {
                return lookupIterator.hasNext();
            }

            public S next() {
                return lookupIterator.next();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

        };
    }

    private void init() {
        if (lookupIterator == null) {
            reload();
        }
    }

    private void initMap() {
        if (classNameMap != null) {
            return;
        }
        classNameMap = new HashMap<>();
        for (Class<? extends S> impl : classes) {
            ServiceName annotation = impl.getAnnotation(ServiceName.class);
            if (annotation != null) {
                String name = annotation.value();
                classNameMap.put(name, impl);
            }
        }
    }

    // Private inner class implementing fully-lazy provider lookup
    //
    private class LazyIterator
            implements Iterator<S> {

        Class<S> service;
        Iterator<Class<? extends S>> iterator;

        private LazyIterator(Class<S> service) {
            this.service = service;
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
                return service.cast(c.newInstance());
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

    @NonNull
    private IServiceProvider<S> getServiceProvider() throws IllegalAccessException, InstantiationException {
        try {
            String packageName = service.getPackage().getName();
            @SuppressWarnings("unchecked") Class<IServiceProvider<S>> serviceProviderClass = (Class<IServiceProvider<S>>) Class.forName(packageName + ".Provider_" + service.getSimpleName());
            return serviceProviderClass.newInstance();
        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
            Log.e("ServiceLoader", "No Provider for" + service.getSimpleName() + ", Please Check whether annotationProcessor for app used");
            return new EmptyServiceProvider<>();
        }
    }

    private static class EmptyServiceProvider<S> implements IServiceProvider<S> {

        @Override
        public Collection<Class<? extends S>> provide() {
            return new ArrayList<>();
        }

    }

}
