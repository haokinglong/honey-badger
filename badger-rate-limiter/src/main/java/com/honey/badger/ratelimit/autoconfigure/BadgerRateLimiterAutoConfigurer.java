package com.honey.badger.ratelimit.autoconfigure;

import static cn.hutool.core.collection.CollUtil.isEmpty;
import static cn.hutool.core.util.StrUtil.C_DOT;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import com.honey.badger.ratelimit.autoconfigure.properties.BadgerRateLimitProperties;
import com.honey.badger.ratelimit.autoconfigure.properties.BadgerRateLimitProperties.Strategy;
import com.honey.badger.ratelimit.constant.RateLimitConstant;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;

/**
 * 限流器的业务初始化器
 *
 * @author haojinlong
 * @date 2022/5/3
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({
    BadgerRateLimitProperties.class
})
public class BadgerRateLimiterAutoConfigurer extends AbstractConfigManager implements ApplicationListener<RateLimitEvent> {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private BadgerRateLimitProperties properties;

    @PostConstruct
    public void init() {
        init(redisTemplate, "LimitLuaScript.lua", properties.getStrategies());
    }

    /**
     * 更新配置的前提是通过{@code Spring}的{@code Listener}机制,下面是示例
     * <pre>{@code
     * public class DemoController {
     *
     *     @Autowired
     *     private ConfigurableApplicationContext context;
     *
     *     @ApiOperation("限流配置的监听器")
     *     @PutMapping("/rate-limit")
     *     public void updateLimit(String value) {
     *         Map<String, Object> map = new HashMap<>();
     *         map.put("honey.badger.rate-limit.strategies.limit1.limitCount", value);
     *         context.publishEvent(new RateLimitEvent(map));
     *     }
     * }
     * }</pre>
     *
     * @param rateLimitEvent {@link RateLimitEvent}
     * @author haojinlong
     * @date 2022/5/3
     */
    @Override
    public void onApplicationEvent(RateLimitEvent rateLimitEvent) {
        Object source = rateLimitEvent.getSource();
        if (null == source) {
            return;
        }

        if (source instanceof Map) {
            Map<String, Object> sourceMap = (Map<String, Object>) source;
            if (isEmpty(strategies)) {
                strategies = properties.getStrategies();
            }

            Set<Entry<String, Object>> entries = sourceMap.entrySet();
            for (Entry<String, Object> next : entries) {
                String key = next.getKey();
                String substring = key.replace(RateLimitConstant.CONFIG_PREFIX + C_DOT, "");
                String[] split = CharSequenceUtil.splitToArray(substring, C_DOT);
                Strategy strategy = strategies.get(split[1]);
                Assert.notNull(strategy, "strategy is null");
                ReflectUtil.setFieldValue(strategy, split[2], next.getValue());

                strategies.put(split[1], strategy);
            }

            updateStrategy(strategies);
        }
    }
}
