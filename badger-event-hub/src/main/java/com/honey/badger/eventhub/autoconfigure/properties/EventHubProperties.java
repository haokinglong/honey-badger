package com.honey.badger.eventhub.autoconfigure.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Event hub 配置
 *
 * @author yinzhao
 * @date 2021/9/30
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "honey.badger.eventhub")
public class EventHubProperties {

    /**
     * 是否开启event hub，默认不开启
     */
    private boolean enable = false;
}
