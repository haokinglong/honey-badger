package com.honey.badger.cache.autoconfigure.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 描述:redis缓存配置
 *
 * @author haojinlong
 * @date 2021/12/3
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RedisTypeConfig extends AbstractCacheProperties {


    private String redisHost;

    private int redisPort;

    private int redisTimeout;

    private String redisPassword;

    private int database;

    private int maxActive;

    private int maxWait;

    private int maxIdle;

    private int minIdle;
}
