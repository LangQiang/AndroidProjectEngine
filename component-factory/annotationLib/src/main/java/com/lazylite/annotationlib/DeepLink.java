package com.lazylite.annotationlib;

public @interface DeepLink {
    String path() default "/";
}
