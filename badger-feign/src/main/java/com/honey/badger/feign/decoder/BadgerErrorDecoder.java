package com.honey.badger.feign.decoder;

import cn.hutool.core.collection.CollUtil;
import com.honey.badger.core.enums.base.BaseEnum;
import com.honey.badger.core.enums.error.BizErrorEnum;
import com.honey.badger.core.exception.BadgerBusinessException;
import com.honey.badger.feign.autoconfigure.properties.BadgerFeignProperties;
import com.honey.badger.feign.support.BadgerFeignUtil;
import feign.Request;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * {@code Feign} 请求异常的请求拦截器
 *
 * @author haojinlong
 * @date 2021/9/18
 */
@Slf4j
public class BadgerErrorDecoder implements ErrorDecoder {

    private static final int STATUS_502 = 502;
    private static final int STATUS_503 = 503;
    private static final int STATUS_504 = 504;
    private static final int STATUS_429 = 429;

    private final BadgerFeignProperties badgerFeignProperties;

    public BadgerErrorDecoder(BadgerFeignProperties imFeignProperties) {
        this.badgerFeignProperties = imFeignProperties;
    }

    @SneakyThrows
    @Override
    public Exception decode(String method, Response response) {
        Request request = response.request();

        if (!BadgerFeignUtil.isThirdMs(request.headers())) {
            BadgerFeignUtil.handleInternalError(request, response);
        }

        Map<String, Collection<String>> headers = new HashMap<>(request.headers());
        // 有请求头且配置不为空时进行请求头日志过滤
        if (CollUtil.isNotEmpty(headers) && CollUtil.isNotEmpty(badgerFeignProperties.getNoLogHeaders())) {
            for (String noLogHeader : badgerFeignProperties.getNoLogHeaders()) {
                headers.remove(noLogHeader);
            }
        }

        log.error("request.url ={}, request.httpMethod ={}, request.headers ={}, response ={}", request.url(), request.httpMethod(), headers, response);
        BaseEnum<String> bizErrorEnum = getBizErrorEnumFromHttpStatus(response.status());
        throw new BadgerBusinessException(bizErrorEnum.getCode(), bizErrorEnum.getValue());
    }

    /**
     * 根据http status获取对应的自定义异常枚举
     * <p>
     * 默认的兜底异常码为{@code GP5001}
     * </p>
     *
     * @param httpStatus Http响应码
     * @return {@link BaseEnum<String>}
     * @author haojinlong
     * @date 2021/11/15
     */
    private BaseEnum<String> getBizErrorEnumFromHttpStatus(int httpStatus) {
        switch (httpStatus) {
            case STATUS_502:
                return BizErrorEnum.GP5002;
            case STATUS_503:
                return BizErrorEnum.GP5003;
            case STATUS_504:
                return BizErrorEnum.GP5004;
            case STATUS_429:
                return BizErrorEnum.GP5429;
            default:
                return BizErrorEnum.GP5001;
        }
    }
}
