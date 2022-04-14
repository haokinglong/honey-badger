package com.honey.badger.record.autoconfigure.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 配置类
 *
 * @author hanlining
 * @date 2021/4/21
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "honey.badger.rabbit")
public class BadgerErrorRecordProperties {

    private BadgerRabbitProperties errorRecord;
}
