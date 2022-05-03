package com.honey.badger.ratelimit.autoconfigure;

import static cn.hutool.core.collection.CollUtil.isEmpty;

import com.honey.badger.ratelimit.autoconfigure.properties.BadgerRateLimitProperties.Strategy;
import com.honey.badger.ratelimit.support.RedisRateLimitUtil;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.util.Assert;

/**
 * 描述:核心配置管理器
 *
 * @author haojinlong
 * @date 2022/5/3
 */
public abstract class AbstractConfigManager {

    protected static Map<String, Strategy> strategies;

    protected static final DefaultRedisScript<Long> REDIS_SCRIPT = new DefaultRedisScript<>();

    protected static StringRedisTemplate redisTemplate;

    /**
     * 使用工具类前请先初始化该工具类
     *
     * @param rt            {@link StringRedisTemplate}
     * @param luaScriptName lua脚本文件名,该文件需要存放在根目录的resource里
     * @author haojinlong
     * @date 2022/4/25
     */
    protected static void init(StringRedisTemplate rt, String luaScriptName, Map<String, Strategy> sty) {
        Assert.notNull(rt, "redisTemplate is null");
        Assert.hasText(luaScriptName, "luaScriptName is null");

        if (null == redisTemplate) {
            synchronized (RedisRateLimitUtil.class) {
                if (null != redisTemplate) {
                    return;
                }

                strategies = sty;
                redisTemplate = rt;
                //指定lua脚本文件
                REDIS_SCRIPT.setScriptSource(new ResourceScriptSource(new ClassPathResource(luaScriptName)));
                //指定返回类型
                REDIS_SCRIPT.setResultType(Long.class);
            }
        }
    }

    /**
     * 更新限流策略
     *
     * @author haojinlong
     * @date 2022/5/3
     */
    protected static void updateStrategy(Map<String, Strategy> strategyMap) {
        if (isEmpty(strategyMap)) {
            return;
        }

        strategies.putAll(strategyMap);
    }
}
