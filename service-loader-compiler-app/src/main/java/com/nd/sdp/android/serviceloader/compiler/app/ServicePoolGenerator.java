package com.nd.sdp.android.serviceloader.compiler.app;

import android.support.annotation.Keep;

import com.google.common.collect.Multimap;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

class ServicePoolGenerator {

    static void write(Multimap<String, String> result, Filer filer, Elements elementUtils) throws IOException {
        System.out.println(result.keys());
        for (String service : result.keys()) {
            ClassName providerClassName = ClassName.get("com.nd.sdp.android.serviceloader.internal", "Provider_" + service.replace(".", "_"));
            ClassName interfaceClassName = ClassName.get("com.nd.sdp.android.serviceloader.internal", "IServiceProvider");
            TypeVariableName sTypeVariableName = TypeVariableName.get(service);
            ParameterizedTypeName genericServiceProvider = ParameterizedTypeName.get(interfaceClassName, sTypeVariableName);
            ParameterizedTypeName genericServiceClassName = ParameterizedTypeName.get(ClassName.get(Class.class), TypeVariableName.get("? extends " + sTypeVariableName));
            Collection<String> implementsClassStrings = result.get(service);
            TypeElement[] implementsClassNames = new TypeElement[implementsClassStrings.size()];
            StringBuilder stringBuilder = new StringBuilder();
            int i = 0;
            for (Iterator<String> iterator = implementsClassStrings.iterator(); iterator.hasNext(); i++) {
                String implementsClassString = iterator.next();
                implementsClassNames[i] = elementUtils.getTypeElement(implementsClassString);
                stringBuilder.append(implementsClassString)
                        .append(".class")
                        .append(",");
            }
            String implClassStringResult = stringBuilder.substring(0, stringBuilder.length() - 1);
            MethodSpec provide = MethodSpec.methodBuilder("provide")
                    .returns(ParameterizedTypeName.get(ClassName.get(Collection.class), genericServiceClassName))
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addStatement("return $T.<$T>asList(" + implClassStringResult + ")", ClassName.get(Arrays.class), genericServiceClassName)
                    .build();
            TypeSpec injectClass = TypeSpec.classBuilder(providerClassName.simpleName())
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addSuperinterface(genericServiceProvider)
                    .addMethod(provide)
                    .addAnnotation(Keep.class)
                    .build();
            JavaFile javaFile = JavaFile.builder(providerClassName.packageName(), injectClass).build();
            javaFile.writeTo(filer);
        }
    }

}
