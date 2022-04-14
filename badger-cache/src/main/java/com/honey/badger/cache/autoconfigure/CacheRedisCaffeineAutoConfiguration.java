package com.honey.badger.cache.autoconfigure;

import com.honey.badger.cache.manager.RedisCaffeineCacheManager;
import com.honey.badger.cache.support.CachePropertiesSupportUtil;
import com.honey.badger.cache.autoconfigure.properties.CacheManagerProperties;
import com.honey.badger.cache.autoconfigure.properties.RedisTypeConfig;
import com.honey.badger.cache.contants.CacheManagerTypeConstant;
import java.time.Duration;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;

/**
 * 缓存管理器自动装配 注册缓存管理器，及手动注册的缓存
 *
 * @author zhangchao01
 * @date 2021/5/31
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({
    CacheManagerProperties.class
})
public class CacheRedisCaffeineAutoConfiguration extends CachingConfigurerSupport {

    @Resource
    private CacheManagerProperties cacheManagerProperties;

    @Resource
    private RedisTemplate<Object, Object> redisTemplate;

    @Override
    public CacheManager cacheManager() {
        return new RedisCaffeineCacheManager(cacheManagerProperties, redisTemplate);
    }

    /**
     * 初始化StringRedisTemplate
     *
     * @return {@link RedisTemplate}
     * @author haojinlong
     * @date 2021/3/11
     */
    @Bean
    @DependsOn("redisConnectionFactory4Cache")
    public RedisTemplate<Object, Object> redisTemplate(@Qualifier("redisConnectionFactory4Cache") RedisConnectionFactory factory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用fastjson
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        // hash的value序列化方式采用fastjson
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();

        return template;
    }

    @Primary
    @Bean(name = "redisConnectionFactory4Cache")
    public LettuceConnectionFactory lettuceConnectionFactory(GenericObjectPoolConfig genericObjectPoolConfig) {
        RedisTypeConfig redisConfig = (RedisTypeConfig) CachePropertiesSupportUtil.getCachePropertiesByType(CacheManagerTypeConstant.REDIS_CACHE_MANAGER, cacheManagerProperties);
        Assert.notNull(redisConfig, "redis properties is null");
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setDatabase(redisConfig.getDatabase());
        redisStandaloneConfiguration.setHostName(redisConfig.getRedisHost());
        redisStandaloneConfiguration.setPort(redisConfig.getRedisPort());
        redisStandaloneConfiguration.setPassword(redisConfig.getRedisPassword());

        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder().commandTimeout(Duration.ofMillis(redisConfig.getRedisTimeout())).poolConfig(genericObjectPoolConfig).build();

        return new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfig);
    }

    @Bean
    public GenericObjectPoolConfig genericObjectPoolConfig() {
        RedisTypeConfig redisConfig = (RedisTypeConfig) CachePropertiesSupportUtil.getCachePropertiesByType(CacheManagerTypeConstant.REDIS_CACHE_MANAGER, cacheManagerProperties);
        Assert.notNull(redisConfig, "redis properties is null");

        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(redisConfig.getMaxIdle());
        genericObjectPoolConfig.setMinIdle(redisConfig.getMinIdle());
        genericObjectPoolConfig.setMaxTotal(redisConfig.getMaxActive());
        genericObjectPoolConfig.setMaxWaitMillis(redisConfig.getMaxWait());

        return genericObjectPoolConfig;
    }
}
