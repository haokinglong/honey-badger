package com.honey.badger.eventhub.autoconfigure.properties;

import com.rabbitmq.client.ConnectionFactory;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Event hub rabbit mq 配置
 *
 * @author yinzhao
 * @date 2021/7/9
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "honey.badger.eventhub.rabbitmq")
public class EventHubRabbitMqProperties {

    /**
     * rabbit 登录用户名
     */
    @NotBlank(message = "rabbit登录用户名不能为空")
    private String username;

    /**
     * rabbit 登录密码
     */
    @NotBlank(message = "rabbit登录密码不能为空")
    private String password;

    /**
     * rabbit host
     */
    @NotBlank(message = "rabbit host不能为空")
    private String host;

    /**
     * rabbit 端口
     */
    private int port = 5672;

    /**
     * rabbit virtual host
     */
    private String virtualHost;

    /**
     * rabbit requested heartbeat timeout
     */
    private int requestedHeartbeat = ConnectionFactory.DEFAULT_HEARTBEAT;

    /**
     * rabbit TCP connection timeout
     */
    private int connectionTimeout = ConnectionFactory.DEFAULT_CONNECTION_TIMEOUT;

    /**
     * rabbit AMQP0-9-1 protocol handshake timeout
     */
    private int handshakeTimeout = ConnectionFactory.DEFAULT_HANDSHAKE_TIMEOUT;

    /**
     * rabbit shutdown timeout
     */
    private int shutdownTimeout = ConnectionFactory.DEFAULT_SHUTDOWN_TIMEOUT;

    /**
     * rabbit requested maximum channel number
     */
    private int requestedChannelMax = ConnectionFactory.DEFAULT_CHANNEL_MAX;

    /**
     * rabbit automatic connection recovery
     * <p>
     * Spring AMQP is NOT compatible with automaticRecoveryEnabled.
     * <p>
     * It has its own recovery mechanisms and has no awareness of the underlying recovery being performed by the client. This leaves dangling connection(s) and Channel(s)
     */
    private boolean automaticRecoveryEnabled = false;

    /**
     * rabbit connection recovery interval
     */
    private int recoveryInterval = (int) ConnectionFactory.DEFAULT_NETWORK_RECOVERY_INTERVAL;

    /**
     * acknowledge mode
     */
    private AcknowledgeMode acknowledgeMode = AcknowledgeMode.MANUAL;

    /**
     * 当前消费者线程数
     */
    private int currentConsumers = 1;

    /**
     * 最大当前消费者线程数
     */
    private int maxCurrentConsumers = 5;

    /**
     * 是否重回队列
     */
    private boolean defaultRequeueRejected = true;

    /**
     * Set whether to expose the listener Rabbit Channel to a registered {@link ChannelAwareMessageListener} as well as
     * to {@link org.springframework.amqp.rabbit.core.RabbitTemplate} calls.
     */
    private boolean exposeListenerChannel = true;
}
