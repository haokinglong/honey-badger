package com.honey.badger.eventhub.annotation;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Event 监听器
 * <p>
 * 被注解的方法为事件接收的入口，自动从指定 {@code source、type、sub} 中拉取事件并作为参数传入
 * <p>
 * <strong>NOTE: </strong>监听器所在类需要带有注解 {@link EventListenerClass}，以便被扫描注入容器
 * <p>
 * Demo:
 * </p>
 * <pre> {@code
 * @BadgerEventListenerClass
 * public class Listener {
 *     @BadgerEventListener
 *     public void listen(YourClass obj) { // 仅限一个参数，仅要实现 {@link Serializable }
 *         // do something with the event
 *     }
 * }
 * }</pre>
 *
 * @author yinzhao
 * @date 2021/9/29
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface EventListener {

    /**
     * 事件源
     */
    String source();

    /**
     * 事件类型
     */
    String type();

    /**
     * 事件订阅者
     */
    String sub();
}
