package com.honey.badger.eventhub.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

/**
 * Event 监听器类
 * <p>
 * 该注解的目的为了将监听器类扫描进容器，自动识别类中带有 {@link EventListener} 注解的事件处理方法
 * </p>
 *
 * @author yinzhao
 * @date 2021/9/29
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Component
public @interface EventListenerClass {

}
