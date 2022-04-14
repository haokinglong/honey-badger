package com.honey.badger.eventhub.producer;

import com.honey.badger.eventhub.utils.EventHubUtils;
import com.honey.badger.eventhub.annotation.EventSender;
import com.honey.badger.eventhub.model.EventPayload;
import java.lang.reflect.Method;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * 事件发送aop
 *
 * @author yinzhao
 */
@Slf4j
@Aspect
public class EventSenderAspect {

    private final RabbitTemplate rabbitTemplate;

    public EventSenderAspect(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Pointcut(value = "@annotation(com.honey.badger.eventhub.annotation.EventSender)")
    public void pointCut() {
        // do nothing
    }

    @AfterReturning(value = "pointCut()")
    public void invoke(JoinPoint joinPoint) {
        try {
            Class<?> clazz = joinPoint.getTarget().getClass();
            String methodName = joinPoint.getSignature().getName();
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Class<?>[] parameterTypes = methodSignature.getMethod().getParameterTypes();
            Method method = clazz.getMethod(methodName, parameterTypes);

            EventSender eventSender = method.getAnnotation(EventSender.class);
            if (eventSender != null && eventSender.aop()) {
                sendEvent(eventSender, EventHubUtils.getArgsMap(joinPoint));
            }
        } catch (Exception e) {
            log.error("send event error, targetClass ={}, args ={}", joinPoint.getTarget().getClass(), joinPoint.getArgs(), e);
        }
    }

    /**
     * 发送事件
     *
     * @param eventSender EventSender
     * @param argsMap       方法参数
     * @author yinzhao
     * @date 2021/9/30
     */
    private void sendEvent(EventSender eventSender, Map<String, Object> argsMap) {
        String exchange = EventHubUtils.getRabbitExchangeNameFromEventSender(eventSender);
        String payloadJsonStr = EventHubUtils.getEventObjectFromSpecifiedMethodArgs(argsMap, eventSender.argNames());
        String msgId = EventHubUtils.getUniqueMsgId();

        EventPayload<String> eventPayload = new EventPayload<>();
        eventPayload.setBody(payloadJsonStr);

        rabbitTemplate.convertAndSend(exchange, eventSender.routingKey(), eventPayload, EventHubUtils.getMessagePostProcessor(eventSender, String.class, msgId), new CorrelationData(msgId));

        log.info("send event success, data ={}, exchange ={}, routing key ={}", eventPayload, exchange, eventSender.routingKey());
    }
}
