package com.honey.badger.cache.contants;

/**
 * 缓存管理器类型
 *
 * @author haojinlong
 * @date 2021/12/6
 */
public class CacheManagerTypeConstant {

    private CacheManagerTypeConstant() {
    }

    /**
     * redis
     */
    public static final String REDIS_CACHE_MANAGER = "redis";
    /**
     * caffeine
     */
    public static final String CAFFEINE_CACHE_MANAGER = "caffeine";
}
