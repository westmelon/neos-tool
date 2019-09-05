package com.tl.common.ext.annotation;

import java.lang.annotation.*;


@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConvertCode {
    String dtype() default "";
    String dataFrom() default "";
}
