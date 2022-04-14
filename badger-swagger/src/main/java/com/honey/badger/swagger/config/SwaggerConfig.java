package com.honey.badger.swagger.config;

import cn.hutool.core.util.ObjectUtil;
import com.honey.badger.swagger.annotation.SwaggerMethodExport;
import com.honey.badger.swagger.annotation.SwaggerTypeExport;
import com.honey.badger.swagger.suport.SwaggerProperties;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 描述: {@code Swagger}核心自定义配置
 * <p>此配置用于在某些需要导出特定某几个{@code Swagger}接口文档的场景下
 * 主要使用的是{@code Swagger}的分组功能.
 * <p>使用:
 * 使用时在需要指定导出的接口方法上添加{@link SwaggerMethodExport} 注解即可,或者在需要指定导出的接口类上添加{@link SwaggerTypeExport} 注解即可
 * <p>系统在初始化时会扫描所有带有该注解的接口进行分组
 *
 * @author haojinlong
 * @date 2021/9/21
 */
@Slf4j
@EnableSwagger2
public class SwaggerConfig {

    private static final String DEFAULT_ALL = "default-all";
    private static final String METHOD_EXPORT = "method-export";
    private static final String TYPE_EXPORT = "type-export";
    private static final String CONTROLLER_BASE_PACKAGE = ".controller";


    @Value("${spring.application.name}")
    private String appName;
    @Value("${honey.badger.swagger.version:}")
    private String appVersion;
    @Resource
    private SwaggerProperties swaggerProperties;

    /**
     * 为类文件导出特定{@code swagger}接口文档的配置
     *
     * @return {@link Docket}
     * @see SwaggerTypeExport
     */
    @Bean
    public Docket swaggerTypeExport() {
        return new Docket(DocumentationType.SWAGGER_2)
            .groupName(TYPE_EXPORT)
            .apiInfo(apiInfo())
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(SwaggerTypeExport.class)).paths(PathSelectors.any())
            .build();
    }

    /**
     * 为方法导出特定{@code swagger}接口文档的配置
     *
     * @return {@link Docket}
     * @see SwaggerMethodExport
     */
    @Bean
    public Docket swaggerMethodExport() {
        return new Docket(DocumentationType.SWAGGER_2)
            .groupName(METHOD_EXPORT)
            .apiInfo(apiInfo())
            .select()
            .apis(RequestHandlerSelectors.withMethodAnnotation(SwaggerMethodExport.class)).paths(PathSelectors.any())
            .build();
    }

    /**
     * 默认导出所有{@code swagger}接口文档的配置
     *
     * @return {@link Docket}
     * @see SwaggerMethodExport
     */
    @Bean
    public Docket swaggerDefault() {
        return new Docket(DocumentationType.SWAGGER_2)
            .groupName(DEFAULT_ALL)
            .apiInfo(apiInfo())
            .select()
            .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage() + CONTROLLER_BASE_PACKAGE)).paths(PathSelectors.any())
            .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title(ObjectUtil.defaultIfBlank(appName, "swagger文档API接口"))
            .version(ObjectUtil.defaultIfBlank(appVersion, "3.0"))
            .description(ObjectUtil.defaultIfBlank(swaggerProperties.getDescription(), "API 描述"))
            .contact(contact())
            .build();
    }

    private Contact contact() {
        return new Contact(swaggerProperties.getContact().getName(), swaggerProperties.getContact().getUrl(), swaggerProperties.getContact().getEmail());
    }
}
