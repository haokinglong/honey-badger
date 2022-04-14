package com.honey.badger.eventhub.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;

/**
 * 事件 listener adapter
 *
 * @author yinzhao
 * @date 2021/9/30
 */
@Slf4j
public class EventListenerAdapter extends MessageListenerAdapter {

    public EventListenerAdapter(EventDelegate param) {
        super(param);
    }

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        log.info("listen event, message ={}", message);

        try {
            super.onMessage(message, channel);
        } catch (Exception e) {
            log.info("eventhub listen event error, message ={}", message, e);
        } finally {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }
}
