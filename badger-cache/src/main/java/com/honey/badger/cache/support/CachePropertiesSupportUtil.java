package com.honey.badger.cache.support;

import com.honey.badger.cache.autoconfigure.properties.AbstractCacheProperties;
import com.honey.badger.cache.autoconfigure.properties.CacheManagerProperties;
import com.honey.badger.cache.autoconfigure.properties.CacheManagerProperties.CacheConfig;
import com.honey.badger.cache.autoconfigure.properties.RedisTypeConfig;
import com.honey.badger.cache.contants.CacheManagerTypeConstant;
import java.util.Map;

/**
 * 描述:缓存配置工具类
 *
 * @author haojinlong
 * @date 2021/12/6
 */
public class CachePropertiesSupportUtil {

    private CachePropertiesSupportUtil() {
    }

    /**
     * 根据缓存管理器类型获取具体配置
     *
     * @param managerType            {@link CacheManagerTypeConstant}缓存管理器类型
     * @param cacheManagerProperties {@link CacheManagerProperties}缓存配置
     * @return {@link AbstractCacheProperties}
     * @author haojinlong
     * @date 2021/12/6
     */
    public static AbstractCacheProperties getCachePropertiesByType(String managerType, CacheManagerProperties cacheManagerProperties) {
        if (CacheManagerTypeConstant.REDIS_CACHE_MANAGER.equals(managerType)) {
            Map<String, CacheConfig> managers = cacheManagerProperties.getManagers();
            CacheConfig cacheConfig = managers.get(managerType);

            RedisTypeConfig redisTypeConfig = RedisTypeConfig.builder()
                .database(cacheConfig.getBaseConfig().getDatabase())
                .maxActive(cacheConfig.getBaseConfig().getMaxActive())
                .maxIdle(cacheConfig.getBaseConfig().getMaxIdle())
                .maxWait(cacheConfig.getBaseConfig().getMaxWait())
                .minIdle(cacheConfig.getBaseConfig().getMinIdle())
                .redisHost(cacheConfig.getBaseConfig().getHost())
                .redisPassword(cacheConfig.getBaseConfig().getPassword())
                .redisPort(cacheConfig.getBaseConfig().getPort())
                .redisTimeout(cacheConfig.getBaseConfig().getTimeout())
                .build();

            redisTypeConfig.setCacheNames(cacheManagerProperties.getCacheNames());
            redisTypeConfig.setPrefix(cacheManagerProperties.getPrefix());

            return redisTypeConfig;
        }

        return null;
    }
}
