package com.tl.common.ext.annotation;

import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelSetting {
    String colTitleName(); //列名
    String nextColName() default ""; //后置列名
    int width() default 20; //列宽
    String datePattern() default "yyyy-MM-dd"; //日期格式化
    boolean isFirst() default false;   //是否第一列
    boolean isLast() default false;   //是否左后一列
    boolean isFatherTitle() default false; // 是否是副标题
    String[] childTitles() default {};
}
