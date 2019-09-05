package com.tl.common.ext.annotation;

import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MaxLength {
    //错误提示
    String msg() default "参数错误";
    int length() default 100;
}
