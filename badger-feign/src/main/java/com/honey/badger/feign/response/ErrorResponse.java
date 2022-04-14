package com.honey.badger.feign.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自定义的异常实体
 *
 * @author haojinlong
 * @date 2021/9/18
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ErrorResponse {

    /**
     * 调用的第三方微服务请求路径
     */
    private String requestUrl;
    /**
     * 调用的第三方微服务名称
     */
    private String serviceName;
    /**
     * 调用的第三方微服务错误的响应体
     */
    private String responseBody;
}
