package com.honey.badger.eventhub.annotation;

import com.honey.badger.eventhub.EventHubTemplate;
import com.honey.badger.eventhub.constants.Constant;
import java.io.Serializable;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Event 事件发送器
 * <p>
 * 事件发送的<strong>唯一</strong>入口
 * <p>
 * Demo:
 * </p>
 * <pre>  {@code
 * @EventSender(source = "IT", type = "fix", argNames = {"userId", "name"})
 * public int calc(String userId, String name, String code) {
 *     // do something
 *     return ...
 * }
 * }</pre>
 * <p>
 * 事件体由 argNames 来指定（方法入参中）需要携带的数据，包装为一个 json 格式字符串向指定的的 source 和 type 发送出去
 * <p>
 * {@code argNames} 需要匹配入参的名称，支持多参传入
 * <p>
 * {@code aop} 用来指明是否使用 Spring AOP 来实现事件的发送，默认使用。若不希望借助 AOP，也可以直接使用
 * {@link EventHubTemplate#sendEvent(Class, String, Serializable)} 来完全自定义地发送事件，但要注意该注解不可少，且 {@code aop} 要设为 {@code false}
 * </p>
 *
 * @author yinzhao
 * @date 2021/9/29
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface EventSender {

    /**
     * 事件源
     */
    String source();

    /**
     * 事件类型
     */
    String type();

    /**
     * 指定需要发送的参数名
     */
    String[] argNames();

    /**
     * rabbit topic 交换机的路由 key
     * <p>
     * <strong>NOTE:</strong>这里保持默认值。
     * </p>
     */
    String routingKey() default Constant.EVENTHUB_DEFAULT_ROUTING_KEY;

    /**
     * 是否aop处理，推荐使用
     */
    boolean aop() default true;
}
