package com.honey.badger.ratelimit.autoconfigure;

import java.util.Map;
import org.springframework.context.ApplicationEvent;

/**
 * 描述:限流器自定义的监听事件
 *
 * @author haojinlong
 * @date 2022/5/3
 */
public class RateLimitEvent extends ApplicationEvent {

    public RateLimitEvent(Map<String, Object> change) {
        super(change);
    }
}
