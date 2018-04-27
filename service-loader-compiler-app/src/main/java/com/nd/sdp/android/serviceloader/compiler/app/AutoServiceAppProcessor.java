package com.nd.sdp.android.serviceloader.compiler.app;

import com.google.auto.service.AutoService;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileManager;
import javax.tools.StandardLocation;

@AutoService(Processor.class)
public class AutoServiceAppProcessor extends AbstractProcessor {

    private JavaFileManager mFileManager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        providers.clear();
        try {
            Field contextField = processingEnvironment.getClass().getDeclaredField("context");
            contextField.setAccessible(true);
            Object context = contextField.get(processingEnvironment);
            Method contextGetMethod = context.getClass().getDeclaredMethod("get", Class.class);
            contextGetMethod.setAccessible(true);
            mFileManager = (JavaFileManager) contextGetMethod.invoke(context, JavaFileManager.class);
        } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private Multimap<String, String> providers = HashMultimap.create();

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (roundEnvironment.processingOver()) {
            return true;
        }
        if (roundEnvironment.getRootElements().size() == 1
                && roundEnvironment.getRootElements()
                .iterator()
                .next()
                .getSimpleName()
                .toString()
                .contains("ServicePool")) {
            return true;
        }
        String resourceFile = "META-INF/services";
        try {
            Method getLocationMethod = mFileManager.getClass().getDeclaredMethod("getLocation", JavaFileManager.Location.class);
            getLocationMethod.setAccessible(true);
            @SuppressWarnings("unchecked") Iterable<? extends File> locations = (Iterable<? extends File>) getLocationMethod.invoke(mFileManager, StandardLocation.CLASS_PATH);
            locations.forEach(it -> {
                try {
                    JarFile jarFile = new JarFile(it);
                    Stream<JarEntry> stream = jarFile.stream();
                    for (Iterator<JarEntry> i = stream.iterator(); i.hasNext(); ) {
                        JarEntry next = i.next();
                        if (next.getName().startsWith(resourceFile)) {
                            log(it.getAbsolutePath());
                            URL url = new URL("jar:file:" + it.getAbsolutePath() + "!/" + next.getName());
                            Set<String> serviceFile = ServicesFiles.readServiceFile(url.openStream());
                            for (String impl : serviceFile) {
                                log(impl);
                                providers.put(next.getName().substring(next.getName().lastIndexOf("/") + 1), impl);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!providers.isEmpty()) {
            try {
                ServicePoolGenerator.write(providers, processingEnv.getFiler(), processingEnv.getElementUtils());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    private void log(String msg) {
        if (processingEnv.getOptions().containsKey("debug")) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, msg);
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of("*");
    }

}