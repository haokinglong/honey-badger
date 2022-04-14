package com.honey.badger.kafka.producer;

import cn.hutool.json.JSONUtil;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.util.Assert;

/**
 * 描述:{@code kafka}消息生产者
 *
 * <p>这里基于目前使用现状:如需多线程,业务系统自行使用多线程进行发送,这里只对消息发送做了简单封装,目的是给使用者留有更大的扩展空间;
 * 如果该处理类自行封装一个多线程发送的方法,难度不大,但是如此一来,不利于使用者做补偿措施.
 * <p>所以基于我们的使用经验考虑,只提供了便于使用者后续扩展的两个方法
 *
 * @author haojinlong
 * @date 2021/9/17
 */
public class BadgerKafkaProducer {

    /**
     * {@code kafka}实例池
     */
    private final ConcurrentMap<String, Producer<String, Object>> kafkaProducerInstancePool;

    public BadgerKafkaProducer(ConcurrentMap<String, Producer<String, Object>> kafkaProducerInstancePool) {
        Assert.notEmpty(kafkaProducerInstancePool, "kafkaProducerInstancePool is empty");
        this.kafkaProducerInstancePool = kafkaProducerInstancePool;
    }

    /**
     * 同步发送消息
     * <p>该方法会阻塞当前发送动作,直到发送动作完成
     * <p>如果服务端因为各种原因消息未送达,该方法会抛出异常{@link ExecutionException},{@link InterruptedException}.
     * <p>如果对消息发送有严格要求的场景,比如需要做失败补偿操作时推荐考虑该方法发送消息
     * <p>注意:该方法会降低消息吞吐量
     *
     * @param topic 主题
     * @param msg   消息内容,传对象即可,方法内部会默认将消息内容转为{@code JSON}字符串
     * @return {@link RecordMetadata}
     * @author haojinlong
     * @date 2021/9/17
     */
    public RecordMetadata sendMsgSync(String topic, Object msg) throws ExecutionException, InterruptedException {
        Assert.hasText(topic, "topic is null");
        Assert.notNull(msg, "msg is null");

        Producer<String, Object> kafkaProducer = kafkaProducerInstancePool.get(topic);
        Assert.notNull(kafkaProducer, String.format("%s kafkaProducer not found", topic));

        return kafkaProducer.send(new ProducerRecord<>(topic, JSONUtil.toJsonStr(msg))).get();
    }

    /**
     * 异步发送消息
     * <p>该方法不会阻塞当前发送动作,会直接返回{@link Future<RecordMetadata>}
     * <p>推荐使用场景为:注重高吞吐量但不注重消息可靠性的场景
     *
     * @param topic 主题
     * @param msg   消息内容
     * @return {@link Future<RecordMetadata>}
     */
    public Future<RecordMetadata> sendMsgAsync(String topic, Object msg) {
        Assert.hasText(topic, "topic is null");
        Assert.notNull(msg, "msg is null");

        Producer<String, Object> kafkaProducer = kafkaProducerInstancePool.get(topic);
        Assert.notNull(kafkaProducer, String.format("%s kafkaProducer not found", topic));

        return kafkaProducer.send(new ProducerRecord<>(topic, JSONUtil.toJsonStr(msg)));
    }
}
