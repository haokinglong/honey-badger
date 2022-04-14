package com.honey.badger.record.autoconfigure.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * 描述: {@code badger ErrorRecord Rabbit}核心配置类
 *
 * @author hanlining
 * @date 2021/4/24
 */
@Getter
@Setter
public class BadgerRabbitProperties {

    private String host;

    private String virtualHost;

    private String port;

    private String username;

    private String password;

    private boolean publisherConfirms;

    private boolean publisherReturns;

    private String directExchange;

    private String queue;

}

