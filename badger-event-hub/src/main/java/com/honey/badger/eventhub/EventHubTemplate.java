package com.honey.badger.eventhub;

import cn.hutool.json.JSONUtil;
import com.honey.badger.eventhub.utils.EventHubUtils;
import com.honey.badger.eventhub.annotation.EventSender;
import com.honey.badger.eventhub.model.EventPayload;
import java.io.Serializable;
import java.lang.reflect.Method;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * event hub 操作
 *
 * @author yinzhao
 * @date 2021/9/30
 */
@Slf4j
@AllArgsConstructor
public class EventHubTemplate {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 手动发送事件
     *
     * @param clazz      类
     * @param methodName 方法名
     * @param payload    事件载体
     * @author yinzhao
     * @date 2021/7/22
     */
    public <T extends Serializable> void sendEvent(Class<?> clazz, String methodName, T payload) {
        try {
            Method method = getMethod(clazz, methodName);
            if (null == method) {
                log.info("manual send event, method '{}' not available", methodName);
                return;
            }

            EventSender eventSender = method.getAnnotation(EventSender.class);
            String exchange = EventHubUtils.getRabbitExchangeNameFromEventSender(eventSender);
            String msgId = EventHubUtils.getUniqueMsgId();

            EventPayload<T> eventPayload = new EventPayload<>();
            eventPayload.setBody(JSONUtil.toJsonStr(payload));

            rabbitTemplate.convertAndSend(exchange, eventSender.routingKey(), eventPayload, EventHubUtils.getMessagePostProcessor(eventSender, payload.getClass(), msgId), new CorrelationData(msgId));

            log.info("manual send event success, data ={}, exchange ={}, routing key ={}", eventPayload, exchange, eventSender.routingKey());
        } catch (Exception e) {
            log.error("send event error, clazz ={}, methodName ={}, payload ={}", clazz, methodName, payload, e);
        }
    }

    /**
     * 获取指定方法
     *
     * @return {@link Method}
     * @author yinzhao
     * @date 2021/7/22
     */
    private Method getMethod(Class<?> clazz, String methodName) {
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method m : declaredMethods) {
            if (methodName.equals(m.getName())) {
                return m;
            }
        }

        return null;
    }
}
