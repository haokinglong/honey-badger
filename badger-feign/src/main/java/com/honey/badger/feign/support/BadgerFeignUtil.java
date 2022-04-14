package com.honey.badger.feign.support;

import static cn.hutool.core.collection.CollUtil.isNotEmpty;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.json.JSONUtil;
import com.honey.badger.core.constants.BadgerConstant;
import com.honey.badger.core.dto.BaseResponse;
import com.honey.badger.core.exception.BadgerBusinessException;
import com.honey.badger.feign.autoconfigure.properties.BadgerFeignProperties;
import com.honey.badger.feign.autoconfigure.properties.BadgerFeignProperties.Client;
import com.honey.badger.feign.response.ErrorResponse;
import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import feign.Util;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * 描述:{@code badger Feign}的工具类
 *
 * @author haojinlong
 * @date 2021/9/18
 */
@Slf4j
public class BadgerFeignUtil {

    private BadgerFeignUtil() {
    }

    /**
     * 根据请求头判断该远程调用方是否为第三方服务
     *
     * @param headers 请求头
     * @return {@link Boolean}
     */
    public static boolean isThirdMs(Map<String, Collection<String>> headers) {
        return isNotEmpty(headers.get(BadgerConstant.X_REMOTE_MS_NAME));
    }

    /**
     * 获取第三方服务指定的请求头
     *
     * @param template              {@link RequestTemplate}
     * @param badgerFeignProperties {@link BadgerFeignProperties}
     * @author haojinlong
     * @date 2021/4/1
     */
    public static void getThirdMsSpecifiedHeaders(RequestTemplate template, BadgerFeignProperties badgerFeignProperties) {
        Map<String, Collection<String>> headers = template.headers();
        if (isThirdMs(headers)) {
            Collection<String> thirdMsName = headers.get(BadgerConstant.X_REMOTE_MS_NAME);
            Map<String, Client> clients = badgerFeignProperties.getClients();

            Object remoteName = ArrayUtil.get(thirdMsName.toArray(), 0);
            Client client = clients.get(String.valueOf(remoteName));

            if (null != client) {
                Map<String, String> remoteHeaders = client.getHeaders();

                if (isNotEmpty(remoteHeaders)) {
                    Set<Entry<String, String>> entries = remoteHeaders.entrySet();
                    for (Entry<String, String> next : entries) {
                        template.header(next.getKey(), next.getValue());
                    }
                }
            }
        }
    }

    /**
     * 处理{@code badger}系统自有异常
     *
     * @param request  {@link Request}
     * @param response {@link Response}
     * @author haojinlong
     * @date 2021/4/25
     */
    public static void handleInternalError(Request request, Response response) {
        String responseBody = null;
        try {
            responseBody = Util.toString(response.body().asReader(CharsetUtil.CHARSET_UTF_8));
        } catch (IOException e) {
            log.info("parse response error, response ={}", response);
            throw new BadgerBusinessException();
        }
        BaseResponse result = JSONUtil.toBean(responseBody, BaseResponse.class);

        ErrorResponse msg = ErrorResponse.builder()
            .requestUrl(request.url())
            .responseBody(responseBody)
            .build();

        log.info(String.valueOf(msg));

        throw new BadgerBusinessException(result.getCode(), result.getMessage());
    }
}
