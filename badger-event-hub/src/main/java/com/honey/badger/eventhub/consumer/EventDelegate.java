package com.honey.badger.eventhub.consumer;

import com.honey.badger.eventhub.model.EventPayload;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 事件接收 delegate
 *
 * @author yinzhao
 * @date 2021/9/30
 */
@Slf4j
@Getter
@AllArgsConstructor
@SuppressWarnings("rawtypes")
public class EventDelegate {

    private final Map<String, EventListenerContext> queueToListenerContextMap;

    public void handleMessage(EventPayload payload) {
        if (payload == null) {
            log.info("eventhub payload is null");
            return;
        }

        EventListenerContext context = queueToListenerContextMap.get(payload.getQueueName());

        try {
            context.getMethod().invoke(context.getTarget(), payload.getParam());
        } catch (Exception e) {
            log.info("handle event error, payload ={}", payload, e);
        }
    }
}
