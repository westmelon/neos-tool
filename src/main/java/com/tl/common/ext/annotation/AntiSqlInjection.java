package com.tl.common.ext.annotation;

import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AntiSqlInjection {
    //错误提示
    String msg() default "";
}
