package com.honey.badger.cache.autoconfigure.properties;

import com.honey.badger.cache.autoconfigure.properties.CacheManagerProperties.CacheName;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * 描述:缓存配置抽象类
 *
 * @author haojinlong
 * @date 2021/12/3
 */
@Setter
@Getter
public abstract class AbstractCacheProperties {

    /**
     * 缓存服务前缀
     */
    public String prefix;

    /**
     * 缓存信息
     */
    public List<CacheName> cacheNames;
}
