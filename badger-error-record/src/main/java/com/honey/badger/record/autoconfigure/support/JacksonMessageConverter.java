package com.honey.badger.record.autoconfigure.support;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.http.MediaType;

/**
 * MQ的信息转换配置,支持json
 *
 * @author haojinlong
 * @date 2020/9/23
 */
public class JacksonMessageConverter extends Jackson2JsonMessageConverter {

    public JacksonMessageConverter() {
        super();
    }

    @Override
    public Object fromMessage(Message message) {
        message.getMessageProperties().setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        return super.fromMessage(message);
    }
}
