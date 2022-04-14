package com.honey.badger.cache.manager;

import com.honey.badger.cache.autoconfigure.properties.CacheManagerProperties;
import com.honey.badger.cache.autoconfigure.properties.CacheManagerProperties.CacheName;
import com.honey.badger.cache.exception.BadgerCacheException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * 描述:多级缓存
 * <p>
 * 目前只支持spring和caffeine的,其他的还未适配
 * </p>
 *
 * @author haojinlong
 * @date 2021/12/2
 */
@Slf4j
public class BadgerMultiCache extends AbstractValueAdaptingCache {

    private String name;
    private RedisTemplate<Object, Object> redisTemplate;
    private String cachePrefix;
    private long defaultExpiration;
    private final Map<String, CacheName> expires = new HashMap<>();
    private RedisDaemonProcess redisDaemonProcess;

    protected BadgerMultiCache(boolean allowNullValues) {
        super(allowNullValues);
    }

    public BadgerMultiCache(String name, RedisTemplate<Object, Object> redisTemplate, RedisDaemonProcess redisDaemonProcess, CacheManagerProperties cacheManagerProperties) {
        super(true);
        this.name = name;
        this.redisTemplate = redisTemplate;
        this.cachePrefix = cacheManagerProperties.getPrefix();
        this.defaultExpiration = 600000;
        List<CacheName> cacheNames = cacheManagerProperties.getCacheNames();
        Assert.notEmpty(cacheNames, "cacheNames is empty");
        for (CacheName cn : cacheNames) {
            this.expires.put(cn.getName(), cn);
        }
        this.redisDaemonProcess = redisDaemonProcess;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    @Override
    public synchronized <T> T get(@NonNull Object key, @NonNull Callable<T> valueLoader) {
        Object value = lookup(key);
        if (value != null) {
            return (T) value;
        }

        value = valueLoader.call();
        Object storeValue = toStoreValue(valueLoader.call());
        put(key, storeValue);

        return (T) value;
    }

    @SneakyThrows
    @Override
    public void put(@NonNull Object key, Object value) {
        CacheName cacheName = getCacheName();
        if ((!super.isAllowNullValues() && value == null)) {
            this.evict(key);
            return;
        }
        if (redisDaemonProcess.isRunning()) {
            if (cacheName.isWriteException()) {
                log.error("redis is not available, key ={}", key);
                throw new BadgerCacheException();
            }
            return;
        }
        if (cacheName.getTtl() > 0) {
            try {
                redisTemplate.opsForValue().set(getKey(key), toStoreValue(value), cacheName.getTtl(), TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                log.error("redis error", e);
            }
        } else {
            try {
                redisTemplate.opsForValue().set(getKey(key), toStoreValue(value));
            } catch (Exception e) {
                log.error("redis error", e);
            }
        }
    }

    @Override
    public ValueWrapper putIfAbsent(@NonNull Object key, Object value) {
        Object cacheKey = getKey(key);
        Object prevValue = null;

        synchronized (Objects.requireNonNull(key)) {
            prevValue = redisTemplate.opsForValue().get(cacheKey);
            if (prevValue == null) {
                Long ttl = getCacheName().getTtl();
                if (ttl > 0) {
                    redisTemplate.opsForValue().set(getKey(key), toStoreValue(value), ttl, TimeUnit.SECONDS);
                } else {
                    redisTemplate.opsForValue().set(getKey(key), toStoreValue(value));
                }
            }
        }

        return toValueWrapper(prevValue);
    }

    @Override
    public void evict(@NonNull Object key) {
        redisTemplate.delete(getKey(key));
    }

    @Override
    public void clear() {
        Set<Object> keys = redisTemplate.keys(this.name.concat(":"));
        for (Object key : keys) {
            redisTemplate.delete(key);
        }
    }

    @Override
    protected Object lookup(@NonNull Object key) {
        Object cacheKey = getKey(key);
        Object value = null;

        if (!redisDaemonProcess.isRunning()) {
            try {
                value = redisTemplate.opsForValue().get(cacheKey);
            } catch (Exception e) {
                redisDaemonProcess.setRunning(true);
                LockSupport.unpark(redisDaemonProcess);
                log.error("redis get error, cacheKey ={}", cacheKey, e);
            }
        }

        return value;
    }

    private Object getKey(Object key) {
        return (StringUtils.isEmpty(this.cachePrefix) ? this.name : this.cachePrefix.concat(":").concat(this.name)).concat(":").concat(key.toString());
    }

    /**
     * 获取过期时间
     *
     * @return {@link long}
     * @author haojinlong
     * @date 2021/12/2
     */
    private CacheName getCacheName() {
        CacheName cacheName = expires.get(this.name);
        Assert.notNull(cacheName, "not found cacheName config, name =" + this.name);

        if (null == cacheName.getTtl()) {
            cacheName.setTtl(defaultExpiration);
        }

        return cacheName;
    }

    /**
     * redis连接状态守护进程
     *
     * @author haojinlong
     * @date 2021/12/6
     */
    public static class RedisDaemonProcess extends Thread {

        private volatile boolean isRunning = false;
        private RedisTemplate<Object, Object> redisTemplate;

        private RedisDaemonProcess() {
        }

        public RedisDaemonProcess(RedisTemplate<Object, Object> redisTemplate) {
            this.redisTemplate = redisTemplate;
            this.isRunning = false;
        }

        public boolean isRunning() {
            return this.isRunning;
        }

        public synchronized void setRunning(boolean isRunning) {
            if (this.isRunning) {
                return;
            }
            this.isRunning = isRunning;
        }

        @Override
        public void run() {
            do {
                if (this.isRunning) {
                    try {
                        redisTemplate.getClientList();
                        this.isRunning = false;
                    } catch (Exception e) {
                        log.error("redis is down");
                    }
                } else {
                    log.info("redis is up");
                    LockSupport.park();
                }
            } while (true);
        }
    }
}