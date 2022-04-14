package com.honey.badger.kafka.autoconfigure;

import static cn.hutool.core.text.CharSequenceUtil.isBlank;

import com.honey.badger.kafka.producer.BadgerKafkaProducer;
import com.honey.badger.kafka.autoconfigure.properties.BadgerKafkaProperties;
import com.honey.badger.kafka.autoconfigure.properties.BadgerKafkaProperties.Client;
import com.honey.badger.kafka.autoconfigure.properties.BadgerKafkaProperties.ConsumerProperties;
import com.honey.badger.kafka.autoconfigure.properties.BadgerKafkaProperties.ConsumerTopicProperties;
import com.honey.badger.kafka.autoconfigure.properties.BadgerKafkaProperties.ProducerProperties;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * {@code Kafka} 的业务初始化器
 *
 * @author haojinlong
 * @date 2021/9/16
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({
    BadgerKafkaProperties.class
})
public class BadgerKafkaAutoConfigurer {

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private BadgerKafkaProperties badgerKafkaProperties;

    @Bean("badgerKafkaProducer")
    public BadgerKafkaProducer badgerKafkaProducer() {
        return new BadgerKafkaProducer(getProducerInstances());
    }


    private ConcurrentMap<String, Producer<String, Object>> getProducerInstances() {
        Map<String, Client> clients = badgerKafkaProperties.getClients();
        ConcurrentMap<String, Producer<String, Object>> producerInstances = new ConcurrentHashMap<>(8);

        if (CollectionUtils.isEmpty(clients)) {
            return producerInstances;
        }

        AtomicInteger clientNumber = new AtomicInteger(1);
        clients.forEach((client, producer) -> {
            ProducerProperties producerProperties = producer.getProducer();
            Properties props = new Properties();

            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producerProperties.getBootstrapServers());
            props.put(ProducerConfig.ACKS_CONFIG, producerProperties.getAcks());
            props.put(ProducerConfig.RETRIES_CONFIG, producerProperties.getRetries());
            props.put(ProducerConfig.BATCH_SIZE_CONFIG, producerProperties.getBatchSize());
            props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, producerProperties.getBufferMemory());
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, producerProperties.getKeySerializer());
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, producerProperties.getValueSerializer());

            Map<String, String> topics = producerProperties.getTopics();
            Assert.notEmpty(topics, String.format("%s topics is empty", client));

            topics.forEach((k, v) -> {
                props.put(ProducerConfig.CLIENT_ID_CONFIG, getClientId(producer.getClientId(), clientNumber.get()));
                KafkaProducer<String, Object> kafkaProducer = new KafkaProducer<>(props);
                producerInstances.put(v, kafkaProducer);
                clientNumber.getAndIncrement();
            });
        });

        return producerInstances;
    }

    private ConcurrentMap<String, Consumer<String, String>> getConsumerInstances() {
        Map<String, Client> clients = badgerKafkaProperties.getClients();
        ConcurrentMap<String, Consumer<String, String>> consumerInstances = new ConcurrentHashMap<>(8);

        if (CollectionUtils.isEmpty(clients)) {
            return consumerInstances;
        }

        AtomicInteger clientNumber = new AtomicInteger(1);
        clients.forEach((client, consumer) -> {
            ConsumerProperties consumerProperties = consumer.getConsumer();
            if (null == consumerProperties) {
                return;
            }
            Properties props = new Properties();

            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerProperties.getBootstrapServers());
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, consumerProperties.getKeySerializer());
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, consumerProperties.getValueSerializer());

            List<ConsumerTopicProperties> topics = consumerProperties.getTopics();
            Assert.notEmpty(topics, String.format("%s topics is empty", client));

            topics.forEach(tp -> {
                props.put(ConsumerConfig.GROUP_ID_CONFIG, tp.getGroupId());
                props.put(ConsumerConfig.CLIENT_ID_CONFIG, getClientId(consumer.getClientId(), clientNumber.get()));
                KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(props);
                kafkaConsumer.subscribe(Collections.singletonList(tp.getTopic()));
                consumerInstances.put(tp.getTopic(), kafkaConsumer);
                clientNumber.getAndIncrement();
            });


        });

        return consumerInstances;
    }

    /**
     * 获取客户端id
     *
     * @param clientId     配置的客户端id
     * @param clientNumber 客户端编号,防止客户端重复
     * @return {@link String}
     * @author haojinlong
     * @date 2021/9/17
     */
    private String getClientId(String clientId, int clientNumber) {
        if (isBlank(clientId)) {
            return getLocalhostIp() + "-" + clientNumber;
        }

        return clientId + "-" + clientNumber + ":" + getLocalhostIp();
    }

    /**
     * 获取本地id
     *
     * @return {@link String}
     * @author haojinlong
     * @date 2021/9/17
     */
    private String getLocalhostIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.info("InetAddress getHostAddress error", e);
        }

        return null;
    }
}
