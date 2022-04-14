package com.honey.badger.webcore.http.handler;


import cn.hutool.json.JSONUtil;
import com.honey.badger.core.annotation.Raw;
import com.honey.badger.core.constants.BadgerConstant;
import com.honey.badger.core.dto.BaseResponse;
import com.honey.badger.core.support.BaseResponseUtil;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 全局返回体封装
 *
 * @author haojinlong
 * @date 2021/2/1
 */
@ControllerAdvice
@ResponseBody
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class BadgerResponseHandler implements ResponseBodyAdvice {

    /**
     * 这里需要对{@code pass}封装的{@code swagger}返回体不作处理
     * <p>
     * 这个方法名是{@code pass}获取{@code swagger}文档信息的方法
     * </p>
     */
    private static final String SWAGGER_RESOURCE_METHOD_NAME = "openapiJson";

    @Override
    public boolean supports(MethodParameter returnType, Class aClass) {
        return null == returnType.getMethodAnnotation(Raw.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (MediaType.APPLICATION_JSON.equals(selectedContentType)) {
            Method method = returnType.getMethod();
            if (null != method && SWAGGER_RESOURCE_METHOD_NAME.equals(method.getName())) {
                return body;
            }
            if (null != method && ResponseEntity.class.equals(method.getReturnType())) {
                return body;
            }
            if (null == body) {
                return BaseResponseUtil.getInstance().success();
            }
            if (body instanceof BaseResponse) {
                ((BaseResponse<?>) body).setTracestack(MDC.get(BadgerConstant.TRANSACTION_ID));
                return body;
            }

            return BaseResponseUtil.getInstance().success(body);
        } else if (MediaType.TEXT_PLAIN.equals(selectedContentType)) {
            return JSONUtil.toJsonStr(BaseResponseUtil.getInstance().success(body));
        }

        return body;
    }
}