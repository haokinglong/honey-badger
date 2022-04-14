package com.honey.badger.feign.decoder;

import static cn.hutool.core.text.CharSequenceUtil.isBlank;

import cn.hutool.core.util.CharsetUtil;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.honey.badger.feign.support.BadgerFeignUtil;
import feign.Request;
import feign.Response;
import feign.Util;
import feign.codec.Decoder;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;


/**
 * {@code Feign} 业务的请求拦截器,用来做全链路追踪
 *
 * @author haojinlong
 * @date 2021/10/15
 */
@Slf4j
@Component
public class BadgerBizErrorDecoder implements Decoder {

    @Autowired
    private ObjectMapper mapper;

    @Override
    public Object decode(Response response, Type type) throws IOException {
        Request request = response.request();

        if (HttpStatus.PARTIAL_CONTENT.value() == response.status() && !BadgerFeignUtil.isThirdMs(request.headers())) {
            BadgerFeignUtil.handleInternalError(request, response);
        }

        String responseBody = Util.toString(response.body().asReader(CharsetUtil.CHARSET_UTF_8));
        if (isBlank(responseBody)) {
            return null;
        }

        return mapper.readValue(responseBody, getJavaType(type));
    }

    /**
     * 获取{@link JavaType}类型
     * <p>由于原始方法携带的{@link Type}无法直接使用,因为可能携带有非{@code Java}基础类型的{@link Type},比如携带有泛型,或者返回的对象不是{@link String}等,都会导致我们自定义的类型解析异常
     * <p>所以,这里需要单独获取我们自定义后的{@link JavaType}
     * 关键代码解释:
     * <pre>
     * {@code 1. type instanceof ParameterizedType} 判断是否带有泛型
     * {@code 2. ((ParameterizedType) type).getActualTypeArguments()} 获取泛型集合
     * {@code 3. (Class) ((ParameterizedType) type).getRawType()} 获取原生的类型
     * {@code 4. for (int i = 0; i < actualTypeArguments.length; i++) {
     *                 // 泛型也可能带有泛型，递归获取
     *                 javaTypes[i] = getJavaType(actualTypeArguments[i]);
     *         }}
     * </pre>
     *
     * @param type {@link Type}
     * @return {@link JavaType}
     * @author haojinlong
     * @date 2021/6/2
     */
    private static JavaType getJavaType(Type type) {
        if (type instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            Class rowClass = (Class) ((ParameterizedType) type).getRawType();

            JavaType[] javaTypes = new JavaType[actualTypeArguments.length];
            for (int i = 0; i < actualTypeArguments.length; i++) {
                javaTypes[i] = getJavaType(actualTypeArguments[i]);
            }

            return TypeFactory.defaultInstance().constructParametricType(rowClass, javaTypes);
        } else {
            // 简单类型直接使用该类构建JavaType
            return TypeFactory.defaultInstance().constructParametricType((Class) type, new JavaType[0]);
        }
    }
}
