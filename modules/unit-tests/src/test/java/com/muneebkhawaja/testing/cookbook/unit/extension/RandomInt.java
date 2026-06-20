package com.muneebkhawaja.testing.cookbook.unit.extension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Marks an `int` test-method parameter to be supplied by [SeededRandomExtension],
/// drawn from `[min, max)`.
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RandomInt {

    int min() default 0;

    int max() default 100;
}
