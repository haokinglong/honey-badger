package com.honey.badger.swagger.suport;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = SwaggerProperties.PREFIX, ignoreUnknownFields = false)
@SuppressWarnings("squid:S1068")
public class SwaggerProperties {

    public static final String PREFIX = "honey.badger.swagger";

    private final Contact contact = new Contact();
    private Boolean enabled = false;
    private String description = "";
    private String username = "";
    private String password = "";
    private String basePackage = "";
    private String version = "";
    private final Auth auth = new Auth();

    @Setter
    @Getter
    public static class Contact {
        private String name;
        private String url;
        private String email;
    }

    @Setter
    @Getter
    public static class Auth {
        /**
         * 支持两种验证方式, basic / token
         */
        private String type;
        /**
         * 仅当 type 为 token 时生效, 可以设置 token 放到哪个 header 中
         */
        private String tokenHeader;

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }

}
