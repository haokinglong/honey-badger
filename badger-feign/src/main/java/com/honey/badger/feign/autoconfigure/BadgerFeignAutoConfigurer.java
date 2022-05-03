package com.honey.badger.feign.autoconfigure;

import com.honey.badger.feign.autoconfigure.properties.BadgerFeignProperties;
import com.honey.badger.feign.decoder.BadgerErrorDecoder;
import com.honey.badger.feign.interceptor.BadgerFeignRequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@code Feign} 的业务初始化器
 *
 * @author haojinlong
 * @date 2021/9/18
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({
    BadgerFeignProperties.class
})
public class BadgerFeignAutoConfigurer {

    @Bean
    public BadgerFeignRequestInterceptor feignRequestInterceptor(BadgerFeignProperties badgerFeignProperties) {
        log.info("badger feign request interceptor init success !");
        return new BadgerFeignRequestInterceptor(badgerFeignProperties);
    }

    @Bean
    public ErrorDecoder badgerErrorDecoder(BadgerFeignProperties badgerFeignProperties) {
        return new BadgerErrorDecoder();
    }
}
