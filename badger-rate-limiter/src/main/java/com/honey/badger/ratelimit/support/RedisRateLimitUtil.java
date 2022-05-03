package com.honey.badger.ratelimit.support;

import static cn.hutool.core.collection.CollUtil.isEmpty;
import static cn.hutool.core.text.CharSequenceUtil.isBlank;

import cn.hutool.core.util.ObjectUtil;
import com.honey.badger.ratelimit.autoconfigure.AbstractConfigManager;
import com.honey.badger.ratelimit.autoconfigure.properties.BadgerRateLimitProperties.Strategy;
import java.util.Collections;
import org.springframework.context.ApplicationListener;

/**
 * 描述:redis限流工具类
 *
 * @author haojinlong
 * @date 2022/4/25
 */
public class RedisRateLimitUtil extends AbstractConfigManager {

    private RedisRateLimitUtil() {
    }

    /**
     * 是否已经限流:自定义版,可以不用基于配置,但需要自己传配置,开箱即用
     *
     * @param limitKey   被限流的key
     * @param ttl        key的有效期:单位毫秒
     * @param windowTime 窗口时间:单位毫秒
     * @param limitCount 限流次数,窗口时间内不能超过多少
     * @return {@link boolean} 已被限流则返回true,未被限流则返回false
     * @author haojinlong
     * @date 2022/4/25
     */
    public static boolean isLimit(String limitKey, long ttl, long windowTime, long limitCount) {
        if (isBlank(limitKey)) {
            return false;
        }
        Long execute = redisTemplate.execute(REDIS_SCRIPT, Collections.singletonList(limitKey), String.valueOf(System.currentTimeMillis()), String.valueOf(ttl), String.valueOf(windowTime), String.valueOf(limitCount));
        if (ObjectUtil.isNull(execute)) {
            return false;
        }

        return 0 == execute;
    }

    /**
     * 是否已经限流:基于配置版的
     * <p>
     * 使用配置版本的接口时,需要自行实现参数更新的传递,使用方可以自行监听{@code Apollo}配置更新,然后通过{@code Spring}的{@link ApplicationListener}提供的规则将参数传递给限流器即可
     * </p>
     *
     * @param strategyGroup 限流的策略组
     * @param limitKey      被限流的key
     * @return {@link boolean} 已被限流则返回true,未被限流则返回false
     * @author haojinlong
     * @date 2022/4/25
     */
    public static boolean isLimit4Config(String strategyGroup, String limitKey) {
        if (isBlank(strategyGroup) || isBlank(limitKey) || isEmpty(strategies)) {
            return false;
        }

        Strategy strategy = strategies.get(strategyGroup);
        if (ObjectUtil.isNull(strategy)) {
            return false;
        }
        Long execute = redisTemplate.execute(REDIS_SCRIPT, Collections.singletonList(limitKey), String.valueOf(System.currentTimeMillis()), String.valueOf(strategy.getTtl()), String.valueOf(strategy.getWindowTime()), String.valueOf(strategy.getLimitCount()));
        if (ObjectUtil.isNull(execute)) {
            return false;
        }

        return 0 == execute;
    }
}
