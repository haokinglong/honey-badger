package com.honey.badger.webcore.config;

import com.honey.badger.webcore.http.interceptor.LogInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 核心配置类
 * <pre>
 * 该配置类主要解决1个问题:
 * 1.向{@code Spring}的拦截器调用链中注册了{@link LogInterceptor}日志打印的拦截器
 * </pre>
 *
 * @author haojinlong
 * @date 2021/1/10
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 注册拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 拦截的路径
        String[] pathPatterns = {
                "/**"
        };

        // 如果有多个拦截器，多写几个即可
        registry.addInterceptor(new LogInterceptor()).addPathPatterns(pathPatterns);
    }
}
