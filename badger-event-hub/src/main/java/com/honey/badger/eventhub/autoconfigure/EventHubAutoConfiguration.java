package com.honey.badger.eventhub.autoconfigure;

import static cn.hutool.core.text.CharSequenceUtil.isBlank;

import com.honey.badger.eventhub.consumer.EventDelegate;
import com.honey.badger.eventhub.consumer.EventListenerAdapter;
import com.honey.badger.eventhub.consumer.EventListenerContext;
import com.honey.badger.eventhub.converter.EventHubMessageConverter;
import com.honey.badger.eventhub.producer.EventSenderAspect;
import com.honey.badger.eventhub.utils.EventHubUtils;
import com.honey.badger.eventhub.EventHubTemplate;
import com.honey.badger.eventhub.annotation.EventListener;
import com.honey.badger.eventhub.annotation.EventListenerClass;
import com.honey.badger.eventhub.autoconfigure.properties.EventHubRabbitMqProperties;
import com.honey.badger.eventhub.autoconfigure.properties.EventHubProperties;
import com.honey.badger.eventhub.converter.JacksonMessageConverter;
import com.rabbitmq.client.ConnectionFactory;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;

/**
 * Event hub 自动配置类
 *
 * @author yinzhao
 * @date 2021/9/30
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "eventhub", name = "enabled", havingValue = "true")
@EnableConfigurationProperties({EventHubProperties.class, EventHubRabbitMqProperties.class})
public class EventHubAutoConfiguration implements ApplicationContextAware {

    private final EventHubRabbitMqProperties eventHubRabbitMqProperties;

    private final List<EventListenerContext> eventListeners = new ArrayList<>();

    private org.springframework.amqp.rabbit.connection.ConnectionFactory amqpConnectionFactory;

    public EventHubAutoConfiguration(EventHubRabbitMqProperties eventHubRabbitMqProperties) {
        this.eventHubRabbitMqProperties = eventHubRabbitMqProperties;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        prepareEventListeners(applicationContext, applicationContext.getBeansWithAnnotation(EventListenerClass.class).values());
    }

    @Bean
    @Order
    public ConnectionFactory eventHubRabbitMqConnectionFactory() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setUsername(eventHubRabbitMqProperties.getUsername());
        connectionFactory.setPassword(eventHubRabbitMqProperties.getPassword());
        connectionFactory.setHost(eventHubRabbitMqProperties.getHost());
        connectionFactory.setPort(eventHubRabbitMqProperties.getPort());
        connectionFactory.setVirtualHost(isBlank(eventHubRabbitMqProperties.getVirtualHost()) ? eventHubRabbitMqProperties.getUsername() : eventHubRabbitMqProperties.getVirtualHost());
        connectionFactory.setRequestedHeartbeat(eventHubRabbitMqProperties.getRequestedHeartbeat());
        connectionFactory.setConnectionTimeout(eventHubRabbitMqProperties.getConnectionTimeout());
        connectionFactory.setHandshakeTimeout(eventHubRabbitMqProperties.getHandshakeTimeout());
        connectionFactory.setShutdownTimeout(eventHubRabbitMqProperties.getShutdownTimeout());
        connectionFactory.setRequestedChannelMax(eventHubRabbitMqProperties.getRequestedChannelMax());
        connectionFactory.setAutomaticRecoveryEnabled(eventHubRabbitMqProperties.isAutomaticRecoveryEnabled());
        connectionFactory.setNetworkRecoveryInterval(eventHubRabbitMqProperties.getRecoveryInterval());

        amqpConnectionFactory = new CachingConnectionFactory(connectionFactory);

        return connectionFactory;
    }

    @Bean("eventHubSimpleMessageListenerContainer")
    @DependsOn("eventHubRabbitMqConnectionFactory")
    public SimpleMessageListenerContainer eventHubSimpleMessageListenerContainer(@Qualifier("eventHubRabbitMqConnectionFactory") ConnectionFactory clientConnectionFactory) {
        List<String> queueNameList = new ArrayList<>();
        Map<String, EventListenerContext> queueToListenerContextMap = getQueueToListenerContextMap(queueNameList);
        EventDelegate messageDelegate = new EventDelegate(queueToListenerContextMap);
        EventListenerAdapter adapter = new EventListenerAdapter(messageDelegate);
        adapter.setMessageConverter(new EventHubMessageConverter(queueToListenerContextMap));
        adapter.containerAckMode(eventHubRabbitMqProperties.getAcknowledgeMode());

        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer(amqpConnectionFactory);
        simpleMessageListenerContainer.setQueueNames(queueNameList.toArray(new String[0]));
        simpleMessageListenerContainer.setAcknowledgeMode(eventHubRabbitMqProperties.getAcknowledgeMode());
        simpleMessageListenerContainer.setConcurrentConsumers(eventHubRabbitMqProperties.getCurrentConsumers());
        simpleMessageListenerContainer.setMaxConcurrentConsumers(eventHubRabbitMqProperties.getMaxCurrentConsumers());
        simpleMessageListenerContainer.setDefaultRequeueRejected(eventHubRabbitMqProperties.isDefaultRequeueRejected());
        simpleMessageListenerContainer.setExposeListenerChannel(eventHubRabbitMqProperties.isExposeListenerChannel());
        simpleMessageListenerContainer.setConsumerTagStrategy(EventHubUtils.getRabbitUniqueConsumerTagStrategy());
        simpleMessageListenerContainer.setMessageListener(adapter);

        return simpleMessageListenerContainer;
    }

    @Bean("eventHubRabbitTemplate")
    @DependsOn("eventHubRabbitMqConnectionFactory")
    public RabbitTemplate eventHubRabbitTemplate(@Qualifier("eventHubRabbitMqConnectionFactory") ConnectionFactory clientConnectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(amqpConnectionFactory);
        rabbitTemplate.setMessageConverter(new JacksonMessageConverter());

        // 消息是否成功发送到Exchange,这里只对失败的信息进行日志打印
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                log.info("event hub, send msg to exchange error, correlationData ={}, cause ={}", correlationData, cause);
            }
        });

        // 触发setReturnCallback回调必须设置mandatory=true, 否则Exchange没有找到Queue就会丢弃掉消息, 而不会触发回调
        rabbitTemplate.setMandatory(true);
        // 消息是否从Exchange路由到Queue, 注意: 这是一个失败回调, 只有消息从Exchange路由到Queue失败才会回调这个方法
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) ->
            log.error("event hub, msg from exchange to Queue error: exchange ={}, route ={}, replyCode ={}, replyText ={}, message ={}", exchange, routingKey, replyCode, replyText, message));

        return rabbitTemplate;
    }

    @Bean
    @DependsOn("eventHubRabbitTemplate")
    @ConditionalOnMissingBean
    public EventSenderAspect badgerEventHubPubProxy(@Qualifier("eventHubRabbitTemplate") RabbitTemplate rabbitTemplate) {
        return new EventSenderAspect(rabbitTemplate);
    }

    @Bean
    @DependsOn("eventHubRabbitTemplate")
    @ConditionalOnMissingBean
    public EventHubTemplate imEventHubTemplate(@Qualifier("eventHubRabbitTemplate") RabbitTemplate rabbitTemplate) {
        return new EventHubTemplate(rabbitTemplate);
    }

    /**
     * 自动配置 event listeners
     *
     * @param applicationContext {@link ApplicationContext}
     * @param listeners          listeners
     * @author yinzhao
     * @date 2021/7/22
     */
    private void prepareEventListeners(ApplicationContext applicationContext, Collection<Object> listeners) {
        for (Object listener : listeners) {
            Class<?> listenerClz = listener.getClass();
            Annotation listenerAnnotation = listenerClz.getAnnotation(EventListenerClass.class);
            if (listenerAnnotation == null) {
                continue;
            }
            try {
                Method[] subMethods = listenerClz.getMethods();
                for (Method subMethod : subMethods) {
                    if (subMethod.getAnnotation(EventListener.class) == null) {
                        continue;
                    }
                    EventListener eventListener = subMethod.getAnnotation(EventListener.class);
                    if (eventListener != null) {
                        EventListenerContext eventHubListenerContext = new EventListenerContext();
                        eventHubListenerContext.setClz(listenerClz);
                        eventHubListenerContext.setEventListener(eventListener);
                        eventHubListenerContext.setMethod(subMethod);
                        eventHubListenerContext.setTarget(applicationContext.getBean(listenerClz));
                        eventListeners.add(eventHubListenerContext);
                    }
                }
            } catch (Exception ex) {
                log.error("event hub client init error", ex);
            }
        }
    }

    /**
     * 获取 queue -> listener context 映射
     *
     * @param queueNameList 队列list，亦是返参
     * @return queue -> listener context 映射
     * @author yinzhao
     * @date 2021/7/22
     */
    private Map<String, EventListenerContext> getQueueToListenerContextMap(List<String> queueNameList) {
        Map<String, EventListenerContext> queueToListenerContextMap = new ConcurrentHashMap<>(16);
        for (EventListenerContext listenerContext : eventListeners) {
            EventListener eventListener = listenerContext.getEventListener();
            String queueName = EventHubUtils.getListenerQueueNameByEventHandler(eventListener);
            queueNameList.add(queueName);
            queueToListenerContextMap.put(queueName, listenerContext);
        }

        return queueToListenerContextMap;
    }
}
