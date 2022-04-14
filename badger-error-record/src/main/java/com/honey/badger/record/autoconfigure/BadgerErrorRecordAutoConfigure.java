package com.honey.badger.record.autoconfigure;

import com.honey.badger.record.aspect.BadgerErrorRecordAspect;
import com.honey.badger.record.autoconfigure.properties.BadgerErrorRecordProperties;
import com.honey.badger.record.autoconfigure.support.JacksonMessageConverter;
import com.honey.badger.record.service.BadgerErrorRecordHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

/**
 * 异常日志记录初始器
 *
 * @author hanlining
 * @date 2021/4/21
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({
    BadgerErrorRecordProperties.class
})
public class BadgerErrorRecordAutoConfigure {

    @Autowired
    private BadgerErrorRecordProperties badgerErrorRecordProperties;

    /**
     * 配置{@code Rabbit Mq} 连接工厂
     *
     * @return {@link CachingConnectionFactory}
     * @author hanlining
     * @date 2021/4/24
     */
    @Primary
    @Bean("errorRecordConnectionFactory")
    public CachingConnectionFactory cachingConnectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();

        factory.setAddresses(badgerErrorRecordProperties.getErrorRecord().getHost());
        factory.setUsername(badgerErrorRecordProperties.getErrorRecord().getUsername());
        factory.setPassword(badgerErrorRecordProperties.getErrorRecord().getPassword());
        factory.setVirtualHost(badgerErrorRecordProperties.getErrorRecord().getVirtualHost());
        factory.setPublisherReturns(badgerErrorRecordProperties.getErrorRecord().isPublisherReturns());

        return factory;
    }

    @Bean("errorRecordRabbitTemplate")
    @DependsOn("errorRecordConnectionFactory")
    public RabbitTemplate errorRecordRabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(cachingConnectionFactory());
        rabbitTemplate.setMessageConverter(errorRecordMessageConverter());

        // 消息是否成功发送到Exchange,这里只对失败的信息进行日志打印
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                log.info("send msg to Exchange error, correlationData ={}, cause ={}", correlationData, cause);
            }
        });

        // 触发setReturnCallback回调必须设置mandatory=true, 否则Exchange没有找到Queue就会丢弃掉消息, 而不会触发回调
        rabbitTemplate.setMandatory(true);
        // 消息是否从Exchange路由到Queue, 注意: 这是一个失败回调, 只有消息从Exchange路由到Queue失败才会回调这个方法
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) ->
            log.error("msg from Exchange to Queue error: exchange ={}, route ={}, replyCode ={}, replyText ={}, message ={}", exchange, routingKey, replyCode, replyText, message));

        return rabbitTemplate;
    }

    @Bean(name = "errorRecordContainerFactory")
    public SimpleRabbitListenerContainerFactory errorRecordContainerFactory(SimpleRabbitListenerContainerFactoryConfigurer containerFactoryConfigurer, @Qualifier("errorRecordConnectionFactory") CachingConnectionFactory connectionFactory
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        containerFactoryConfigurer.configure(factory, connectionFactory);
        return factory;
    }

    @Bean("errorRecordMessageConverter")
    public JacksonMessageConverter errorRecordMessageConverter() {
        return new JacksonMessageConverter();
    }

    /**
     * 功能描述：Direct服务交换机
     */
    @Bean
    public DirectExchange errorRecordDirectExchange() {
        return new DirectExchange(badgerErrorRecordProperties.getErrorRecord().getDirectExchange());
    }

    @Bean
    public Queue errorRecordQueue() {
        return new Queue(badgerErrorRecordProperties.getErrorRecord().getQueue(), true);
    }

    @Bean
    public Binding errorRecordBinding() {
        return BindingBuilder.bind(errorRecordQueue()).to(errorRecordDirectExchange()).with(badgerErrorRecordProperties.getErrorRecord().getQueue());
    }

    /**
     * 创建异常处理器实例并交给{@code spring} 托管
     *
     * @return {@link BadgerErrorRecordHandler}
     * @author hanlining
     * @date 2021/4/21
     */
    @Bean("errorRecordHandler")
    public BadgerErrorRecordHandler newErrorRecordService() {
        return new BadgerErrorRecordHandler(errorRecordRabbitTemplate(), badgerErrorRecordProperties);
    }

    /**
     * 创建切面对象实例并交给{@code spring} 托管
     *
     * @param errorRecordHandler 异常记录处理器
     * @return {@link BadgerErrorRecordAspect}
     * @author hanlining
     * @date 2021/4/21
     */
    @Bean
    @DependsOn("errorRecordHandler")
    public BadgerErrorRecordAspect newErrorRecordAspect(BadgerErrorRecordHandler errorRecordHandler) {
        return new BadgerErrorRecordAspect(errorRecordHandler);
    }


}
