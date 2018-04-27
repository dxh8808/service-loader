package com.nd.sdp.android.serviceloader.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by young on 2018/4/27.
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface App {
}
