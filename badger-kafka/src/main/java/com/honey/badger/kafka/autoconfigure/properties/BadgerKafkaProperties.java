package com.honey.badger.kafka.autoconfigure.properties;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 描述: {@code badger Kafka}核心配置类
 * <pre>{@code
 * //生产者
 * honey.badger.clients.yts-search.producer.bootstrapServers = :9999
 * honey.badger.clients.yts-search.producer.bufferMemory = 33554432
 * honey.badger.clients.yts-search.producer.batchSize = 16384
 * honey.badger.clients.yts-search.producer.retries = 0
 * honey.badger.clients.yts-search.producer.acks = 1
 * honey.badger.clients.yts-search.producer.keySerializer = org.apache.kafka.common.serialization.StringSerializer
 * honey.badger.clients.yts-search.producer.valueSerializer = org.apache.kafka.common.serialization.StringSerializer
 * }</pre>
 *
 * @author haojinlong
 * @date 2021/9/16
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "honey.badger.kafka")
public class BadgerKafkaProperties {

    private final Map<String, Client> clients = new LinkedHashMap<>();

    @Setter
    @Getter
    public static class Client {

        private String clientId;
        private ProducerProperties producer;
        private ConsumerProperties consumer;
    }

    @Setter
    @Getter
    public static class ProducerProperties {

        private Map<String, String> topics;
        private String bootstrapServers;
        private int bufferMemory;
        private int batchSize;
        private int retries;
        private String acks;
        private String keySerializer;
        private String valueSerializer;
    }

    @Setter
    @Getter
    public static class ConsumerProperties {

        private String bootstrapServers;
        private List<ConsumerTopicProperties> topics;
        private String keySerializer;
        private String valueSerializer;
    }

    @Setter
    @Getter
    public static class ConsumerTopicProperties {

        private String groupId;
        private String topic;
    }

}