package com.honey.badger.record.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 执行方法异常时记录异常日志
 *
 * @author hanlining
 * @date 2021/4/21
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BadgerNeedErrorRecord {

    /**
     * 服务编号
     *
     */
    int serviceCode();

    /**
     * 执行的方法名称:中文
     */
    String actionName();

    /**
     * 是否需要AOP记录,默认不需要记录
     * <p>切记:手动记录与AOP记录只能二选一
     */
    boolean needAopRecord() default false;

}
