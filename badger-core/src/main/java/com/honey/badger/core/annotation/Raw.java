package com.honey.badger.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 该注解用于REST API
 *
 * <p>
 * 如果一个API的返回不需要被ResponseWrapper包装，添加此注解
 * </p>
 *
 * @author haojinlong
 * @date 2020/9/13
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Raw {

}