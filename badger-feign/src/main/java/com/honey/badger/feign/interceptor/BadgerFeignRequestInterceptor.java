package com.honey.badger.feign.interceptor;

import com.honey.badger.core.constants.BadgerConstant;
import com.honey.badger.feign.autoconfigure.properties.BadgerFeignProperties;
import com.honey.badger.feign.support.BadgerFeignUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

/**
 * {@code Feign} 的请求拦截器,用来做全链路追踪
 *
 * @author haojinlong
 * @date 2021/9/18
 */
@Slf4j
public class BadgerFeignRequestInterceptor implements RequestInterceptor {

    private final BadgerFeignProperties badgerFeignProperties;

    public BadgerFeignRequestInterceptor(BadgerFeignProperties badgerFeignProperties) {
        this.badgerFeignProperties = badgerFeignProperties;
    }

    @Override
    public void apply(RequestTemplate template) {
        template.header(BadgerConstant.TRANSACTION_ID, MDC.get(BadgerConstant.TRANSACTION_ID));
        BadgerFeignUtil.getThirdMsSpecifiedHeaders(template, badgerFeignProperties);
    }
}
