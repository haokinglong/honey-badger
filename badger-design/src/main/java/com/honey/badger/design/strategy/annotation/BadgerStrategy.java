package com.honey.badger.design.strategy.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@code Badger}通用策略模式注解
 *
 * @author haojinlong
 * @date 2021/9/18
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BadgerStrategy {

    /**
     * 策略唯一标识
     */
    String strategyId();

    /**
     * 策略动作
     */
    String operation();
}
