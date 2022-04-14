package com.honey.badger.eventhub.converter;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

/**
 * MQ的信息转换配置,支持json
 *
 * @author yinzhao
 * @date 2020/9/23
 */
public class JacksonMessageConverter extends Jackson2JsonMessageConverter {

    public JacksonMessageConverter() {
        super();
    }

    @Override
    public Object fromMessage(Message message) {
        message.getMessageProperties().setContentType(MessageProperties.CONTENT_TYPE_JSON);
        message.getMessageProperties().setContentEncoding(MessageProperties.CONTENT_TYPE_JSON);
        return super.fromMessage(message);
    }
}
