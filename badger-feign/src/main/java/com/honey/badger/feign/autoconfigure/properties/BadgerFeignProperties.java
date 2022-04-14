package com.honey.badger.feign.autoconfigure.properties;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 描述: {@code Badger Feign}核心配置类
 * <pre>{@code
 * honey.badger.feign.clients.contacts.apikey = 11343546576587
 * honey.badger.feign.clients.contacts.url = http://www.baidu.com
 * honey.badger.feign.clients.contacts.headers.demo1 = demo
 * honey.badger.feign.clients.contacts.headers.demo2 = contact
 * }</pre>
 *
 * @author haojinlong
 * @date 2021/4/1
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "honey.badger.feign")
public class BadgerFeignProperties {

    private final Map<String, Client> clients = new LinkedHashMap<>();

    @Value("${spring.application.name}")
    private String appName;

    /**
     * 打印请求头时需要屏蔽的请求头
     */
    private List<String> noLogHeaders;

    @Setter
    @Getter
    public static class Client implements Serializable {

        private String apikey;
        private String url;
        private Map<String, String> headers = new LinkedHashMap<>();
    }
}