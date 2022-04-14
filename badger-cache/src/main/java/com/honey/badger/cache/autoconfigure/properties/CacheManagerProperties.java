package com.honey.badger.cache.autoconfigure.properties;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * CacheManagerProperties
 * <pre>{@code
 * honey.badger.cache.managers.redis.prefix=
 * honey.badger.cache.managers.redis.cache-names[0].name=
 * honey.badger.cache.managers.redis.cache-names[0].ttl=
 * }</pre>
 *
 * @author haojinlong
 * @date 2021/6/1
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "honey.badger.cache")
public class CacheManagerProperties {

    private Map<String, CacheConfig> managers = new LinkedHashMap<>();

    /**
     * 缓存服务前缀
     */
    private String prefix;

    /**
     * 缓存信息
     */
    private List<CacheName> cacheNames;

    @Getter
    @Setter
    public static class CacheConfig {

        /**
         * 基本配置
         */
        private BaseConfig baseConfig;
    }

    /**
     * 基本配置信息
     */
    @Getter
    @Setter
    public static class BaseConfig {

        //*******************************以下是REDIS基本配置****************************

        /**
         * redis host
         */
        private String host;
        /**
         * redis端口
         */
        private int port;
        /**
         * redis超时时间
         */
        private int timeout;
        /**
         * redis密码
         */
        private String password;
        /**
         * 使用的redis哪个库
         */
        private int database;
        /**
         * 连接池最大连接数(使用负值表示没有限制) 默认为8
         */
        private int maxActive;
        /**
         * 连接池最大阻塞等待时间(使用负值表示没有限制) 默认为-1,单位ms
         */
        private int maxWait;
        /**
         * 连接池中的最大空闲连接 默认为8
         */
        private int maxIdle;
        /**
         * 连接池中的最小空闲连接 默认为 0
         */
        private int minIdle;
    }

    @Getter
    @Setter
    public static class CacheName {

        /**
         * 缓存名称
         */
        private String name;
        /**
         * 远程缓存服务失联时写操作是否抛异常,默认不抛异常
         */
        private boolean writeException;
        /**
         * 缓存过期时间:单位秒
         */
        private Long ttl;
    }
}