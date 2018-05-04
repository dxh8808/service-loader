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
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

class ServicePoolGenerator {

    static void write(Multimap<String, String> result, ProcessingEnvironment processingEnvironment) throws IOException {
        Elements elementUtils = processingEnvironment.getElementUtils();
        Filer filer = processingEnvironment.getFiler();
        Messager messager = processingEnvironment.getMessager();
        for (String service : result.keySet()) {
            TypeElement typeElement = elementUtils.getTypeElement(service);
            if (typeElement == null) {
                messager.printMessage(Diagnostic.Kind.WARNING, "Not found " + service);
                continue;
            }
            PackageElement packageElement = elementUtils.getPackageOf(typeElement);
            Name packageName = packageElement.getQualifiedName();
            ClassName providerClassName = ClassName.get(packageName.toString(), "Provider_" + typeElement.getSimpleName());
            ClassName interfaceClassName = ClassName.get("com.nd.sdp.android.serviceloader.internal", "IServiceProvider");
            TypeVariableName sTypeVariableName = TypeVariableName.get(service);
            ParameterizedTypeName genericServiceProvider = ParameterizedTypeName.get(interfaceClassName, sTypeVariableName);
            ParameterizedTypeName genericServiceClassName = ParameterizedTypeName.get(ClassName.get(Class.class), TypeVariableName.get("? extends " + sTypeVariableName));
            Collection<String> implementsClassStrings = result.get(service);
            if (implementsClassStrings.isEmpty()) {
                messager.printMessage(Diagnostic.Kind.WARNING, "No Service For" + service);
                continue;
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (String implementsClassString : implementsClassStrings) {
                stringBuilder.append(implementsClassString)
                        .append(".class")
                        .append(",");
                stringBuilder.append("\n");
            }
            String implClassStringResult = stringBuilder.substring(0, stringBuilder.length() - 2);
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
