package com.honey.badger.ratelimit.autoconfigure.properties;

import com.honey.badger.ratelimit.constant.RateLimitConstant;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 限流器配置
 * <pre>{@code
 * honey.badger.rate-limit.strategies.limit1.ttl=100000
 * honey.badger.rate-limit.strategies.limit1.windowTime=100000
 * honey.badger.rate-limit.strategies.limit1.limitCount=10
 * honey.badger.rate-limit.strategies.limit1.limitKey=account
 *
 * honey.badger.rate-limit.strategies.limit2.ttl=100000
 * honey.badger.rate-limit.strategies.limit2.windowTime=100000
 * honey.badger.rate-limit.strategies.limit2.limitCount=10
 * honey.badger.rate-limit.strategies.limit2.limitKey=apple
 * }</pre>
 *
 * @author haojinlong
 * @date 2022/5/3
 */
@Getter
@Setter
@ConfigurationProperties(prefix = RateLimitConstant.CONFIG_PREFIX)
public class BadgerRateLimitProperties {

    private final Map<String, Strategy> strategies = new LinkedHashMap<>();

    @Setter
    @Getter
    public static class Strategy implements Serializable {

        private String limitKey;
        private Long ttl;
        private Long windowTime;
        private Long limitCount;
    }
}