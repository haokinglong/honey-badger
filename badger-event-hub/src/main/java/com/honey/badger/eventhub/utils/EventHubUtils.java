package com.honey.badger.eventhub.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.honey.badger.eventhub.annotation.EventListener;
import com.honey.badger.eventhub.annotation.EventSender;
import com.honey.badger.eventhub.constants.Constant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.util.Assert;

/**
 * event hub 工具类
 *
 * @author yinzhao
 * @date 2021/9/30
 */
public class EventHubUtils {

    private EventHubUtils() {
    }

    /**
     * 通过{@link EventSender}来获取rabbit mq的交换机名称
     *
     * @param event {@link EventSender}
     * @return {@link String}
     * @author yinzhao
     * @date 2021/9/30
     */
    public static String getRabbitExchangeNameFromEventSender(EventSender event) {
        return event.source() + Constant.EVENTHUB_NAME_SPLITTER + event.type();
    }

    /**
     * 获取 rabbit {@link ConsumerTagStrategy}
     *
     * @return {@link org.springframework.amqp.support.ConsumerTagStrategy}
     * @author yinzhao
     * @date 2021/7/22
     */
    public static ConsumerTagStrategy getRabbitUniqueConsumerTagStrategy() {
        return queue -> queue + StrUtil.UNDERLINE + UUID.randomUUID();
    }

    /**
     * 通过{@link EventListener}来获取rabbit mq监听队列名
     *
     * @param eventListener EventListener
     * @return {@link String}
     * @author yinzhao
     * @date 2021/7/22
     */
    public static String getListenerQueueNameByEventHandler(EventListener eventListener) {
        StringBuilder queueNameBuilder = new StringBuilder();
        queueNameBuilder.append(eventListener.source());
        if (queueNameBuilder.length() == 0) {
            queueNameBuilder.append(eventListener.type());
        } else {
            queueNameBuilder.append(Constant.EVENTHUB_NAME_SPLITTER).append(eventListener.type());
        }
        if (queueNameBuilder.length() == 0) {
            queueNameBuilder.append(eventListener.sub());
        } else {
            queueNameBuilder.append(Constant.EVENTHUB_NAME_SPLITTER).append(eventListener.sub());
        }

        return queueNameBuilder.toString();
    }

    /**
     * 获取唯一的消息UUID
     *
     * @return {@link String}
     * @author yinzhao
     * @date 2021/9/30
     */
    public static String getUniqueMsgId() {
        return UUID.randomUUID().toString();
    }

    /**
     * 通过指定的事件发送方法的参数来获取事件对象 Json 字符串
     *
     * @param argsMap  指定的参数序号，从左往右从0开始计数
     * @param argNames 事件发送方法的参数
     * @return {@link String}
     * @author yinzhao
     * @date 2021/9/30
     */
    public static String getEventObjectFromSpecifiedMethodArgs(Map<String, Object> argsMap, String[] argNames) {
        Assert.notNull(argsMap, "'argNumber' cannot be null");
        Assert.notNull(argNames, "'args' cannot be null");
        Assert.isTrue(argsMap.size() != 0, "'argNumber.size()' cannot be 0");
        Assert.isTrue(argNames.length != 0, "'args.length' cannot be 0");

        Map<String, Object> eventObjectsMap = new HashMap<>(4);
        for (String argName : argNames) {
            eventObjectsMap.put(argName, argsMap.getOrDefault(argName, null));
        }

        return JSONUtil.toJsonStr(eventObjectsMap);
    }

    /**
     * 通过{@link JoinPoint}获取 参数名->参数 的映射
     *
     * @param joinPoint JoinPoint
     * @return {@link Map< String, Object>}
     * @author yinzhao
     * @date 2021/7/22
     */
    public static Map<String, Object> getArgsMap(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] argNames = methodSignature.getParameterNames();
        Map<String, Object> argsMap = new HashMap<>(argNames.length);

        for (int i = 0; i < argNames.length; i++) {
            argsMap.put(argNames[i], args[i]);
        }

        return argsMap;
    }

    /**
     * 定制消息 header
     *
     * @param eventSender EventSender
     * @param clazz           消息体class
     * @param msgId           消息唯一标记
     * @return MessagePostProcessor
     */
    public static MessagePostProcessor getMessagePostProcessor(EventSender eventSender, Class<?> clazz, String msgId) {
        return (Message message) -> {
            message.getMessageProperties().setHeader(Constant.K_X_SOURCE, eventSender.source());
            message.getMessageProperties().setHeader(Constant.K_X_TYPE, eventSender.type());
            message.getMessageProperties().setHeader(Constant.K_X_ROUTING_KEY, eventSender.routingKey());
            message.getMessageProperties().setHeader(Constant.K_X_REQ_ID, msgId);
            message.getMessageProperties().setHeader(Constant.K_X_CLZ, clazz.getName());
            message.getMessageProperties().setContentType(MessageProperties.CONTENT_TYPE_JSON);
            message.getMessageProperties().setContentEncoding("UTF-8");

            return message;
        };
    }
}
