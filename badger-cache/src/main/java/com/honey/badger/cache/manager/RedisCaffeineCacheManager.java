package com.honey.badger.cache.manager;

import com.honey.badger.cache.manager.BadgerMultiCache.RedisDaemonProcess;
import com.honey.badger.cache.autoconfigure.properties.CacheManagerProperties;
import com.honey.badger.cache.autoconfigure.properties.CacheManagerProperties.CacheName;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;

/**
 * 描述: 自定义的缓存管理器
 *
 * @author haojinlong
 * @date 2021/12/2
 */
@Slf4j
public class RedisCaffeineCacheManager implements CacheManager {

    /**
     * 缓存配置
     */
    private final CacheManagerProperties cacheManagerProperties;
    /**
     * 缓存key的集合
     */
    private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<>();
    /**
     * redis的实例
     */
    private final RedisTemplate<Object, Object> redisTemplate;
    /**
     * 是否动态根据cacheName创建Cache的实现,默认是true
     */
    private final AtomicBoolean dynamic = new AtomicBoolean(true);

    private RedisDaemonProcess redisDaemonProcess;
    /**
     * 缓存集合
     */
    private final Set<String> cacheNames = new HashSet<>();

    public RedisCaffeineCacheManager(CacheManagerProperties cacheManagerProperties, RedisTemplate<Object, Object> redisTemplate) {
        super();
        this.cacheManagerProperties = cacheManagerProperties;
        this.redisTemplate = redisTemplate;

        List<CacheName> cacheNameList = cacheManagerProperties.getCacheNames();
        for (CacheName cacheName : cacheNameList) {
            this.cacheNames.add(cacheName.getName());
        }
    }

    @Override
    public Cache getCache(@NonNull String name) {
        Cache cache = cacheMap.get(name);
        if (cache != null) {
            return cache;
        }
        if (!this.dynamic.get() && !cacheNames.contains(name)) {
            return null;
        }

        if (null == this.redisDaemonProcess) {
            this.redisDaemonProcess = new RedisDaemonProcess(redisTemplate);
            this.redisDaemonProcess.start();
        }
        cache = new BadgerMultiCache(name, redisTemplate, this.redisDaemonProcess, cacheManagerProperties);
        Cache oldCache = cacheMap.putIfAbsent(name, cache);
        log.debug("create cache instance, the cache name is ={}", name);

        return oldCache == null ? cache : oldCache;
    }

    @Override
    public Collection<String> getCacheNames() {
        return this.cacheNames;
    }
}
