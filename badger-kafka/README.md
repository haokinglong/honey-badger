# badger-KAFKA

---

## 简介

`Kafka`核心包,目前主要提供了`producer`的多实例配置,消费者的还未完成

## 使用:

添加包依赖即可使用

```xml

<dependency>
    <groupId>com.honey</groupId>
    <artifactId>badger-kafka</artifactId>
    <version>${badger.version}</version>
</dependency>
```

`application.properties`配置示例:

```properties
#kafka配置1
honey.badgerkafka.clients.demo1.producer.bootstrapServers=192.168.0.105:9092,192.168.0.105:9093,192.168.0.105:9094
#=============== provider  =======================
honey.badgerkafka.clients.demo1.producer.acks=1
honey.badgerkafka.clients.demo1.producer.retries=0
honey.badgerkafka.clients.demo1.producer.topics[0]=demo1_one_topic
honey.badgerkafka.clients.demo1.producer.topics[1]=demo1_two_topic
# 每次批量发送消息的数量
honey.badgerkafka.clients.demo1.producer.batchSize=16384
honey.badgerkafka.clients.demo1.producer.bufferMemory=33554432
# 指定消息key和消息体的编解码方式
honey.badgerkafka.clients.demo1.producer.keySerializer=org.apache.kafka.common.serialization.StringSerializer
honey.badgerkafka.clients.demo1.producer.valueSerializer=org.apache.kafka.common.serialization.StringSerializer
#kafka配置2
honey.badgerkafka.clients.demo2.producer.bootstrapServers=192.168.0.105:9092,192.168.0.105:9093,192.168.0.105:9094
#=============== provider  =======================
honey.badgerkafka.clients.demo2.producer.acks=1
honey.badgerkafka.clients.demo2.producer.retries=0
honey.badgerkafka.clients.demo2.producer.topics[0]=demo2_one_topic
honey.badgerkafka.clients.demo2.producer.topics[1]=demo2_two_topic
# 每次批量发送消息的数量
honey.badgerkafka.clients.demo2.producer.batchSize=16384
honey.badgerkafka.clients.demo2.producer.bufferMemory=33554432
# 指定消息key和消息体的编解码方式
honey.badgerkafka.clients.demo2.producer.keySerializer=org.apache.kafka.common.serialization.StringSerializer
honey.badgerkafka.clients.demo2.producer.valueSerializer=org.apache.kafka.common.serialization.StringSerializer
```