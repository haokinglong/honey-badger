package com.honey.badger.eventhub.converter;

import cn.hutool.json.JSONUtil;
import com.honey.badger.eventhub.consumer.EventListenerContext;
import com.honey.badger.eventhub.model.EventPayload;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.AbstractMessageConverter;
import org.springframework.util.Assert;

/**
 * 自定义消息转换器
 *
 * @author yinzhao
 * @date 2021/9/30
 */
@Slf4j
@AllArgsConstructor
@SuppressWarnings("rawtypes")
public class EventHubMessageConverter extends AbstractMessageConverter {

    private final Map<String, EventListenerContext> queueToListenerContextMap;

    @Override
    protected Message createMessage(Object object, MessageProperties messageProperties) {
        return null;
    }

    @Override
    public Object fromMessage(Message message) {
        try {
            String payloadStr = new String(message.getBody(), StandardCharsets.UTF_8);
            EventPayload payload = JSONUtil.toBean(payloadStr, EventPayload.class);
            String payloadBody = payload.getBody();
            String queueName = message.getMessageProperties().getConsumerQueue();

            payload.setParam(JSONUtil.toBean(payloadBody, getPayLoadBodyClass(queueName)));
            payload.setQueueName(queueName);

            return payload;
        } catch (Exception e) {
            log.info("eventhub message convert error", e);
        }

        return null;
    }

    /**
     * 获取listener参数类型
     *
     * @param queueName rabbit队列名
     * @return {@link Class<?>}
     * @author yinzhao
     * @date 2021/7/22
     */
    private Class<? extends Serializable> getPayLoadBodyClass(String queueName) {
        EventListenerContext context = queueToListenerContextMap.get(queueName);
        Method method = context.getMethod();
        Class<?>[] classes = method.getParameterTypes();
        Assert.isTrue(classes.length == 1, "listener allow only one argument");

        return (Class<? extends Serializable>) classes[0];
    }
}
