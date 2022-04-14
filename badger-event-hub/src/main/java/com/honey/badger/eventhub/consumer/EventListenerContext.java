package com.honey.badger.eventhub.consumer;

import com.honey.badger.eventhub.annotation.EventListener;
import java.lang.reflect.Method;
import lombok.Data;

/**
 * 事件 listener 上下文
 *
 * @author yinzhao
 */
@Data
public class EventListenerContext {

    /**
     * listener handler
     *
     * @see EventListener
     */
    private Method method;

    /**
     * EventListener self自身
     */
    private EventListener eventListener;

    /**
     * listener 类
     */
    private Class<?> clz;

    /**
     * listener 类对象
     */
    private Object target;
}
