package com.javaoffers.batis.modelhelper.anno.fun.params.varchar;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * CONCAT_WS(separator,str1,str2,...) as fieldName : Merge string function
 * create by cmj
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ConcatWs {
    public static final String TAG = "CONCAT_WS";

    /**
     * separator.
     * sample: separator = " '**' "  or separator = " colName "
     */
    String separator();

    /**
     * Additional table field information to be spliced
     */
    String[] value() ;

    /**
     * Specify the parameter location of colName. 0 means at the far left- 1 stands for the far right.
     * Greater than 0, from left to right Less than 0, from right to left
     */
    int position() default 0;
}
