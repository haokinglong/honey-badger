package com.honey.badger.swagger.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在需要为swagger导出特定接口文档的时候使用该注解
 * <p>标注于需要导出接口的方法上即可
 *
 * @author haojinlong
 * @date 2020/12/21
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SwaggerMethodExport {
}

