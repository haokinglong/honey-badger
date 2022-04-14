package com.honey.badger.core.support;

import com.honey.badger.core.constants.BadgerConstant;
import com.honey.badger.core.dto.BaseResponse;
import org.slf4j.MDC;

/**
 * 描述:响应体封装util
 *
 * @author haojinlong
 * @date 2021/2/1
 */
public class BaseResponseUtil {

    private static volatile BaseResponseUtil instance;

    public static BaseResponseUtil getInstance() {
        if (null != instance) {
            return instance;
        }

        synchronized (BaseResponseUtil.class) {
            if (null != instance) {
                return instance;
            }

            instance = new BaseResponseUtil();
        }

        return instance;
    }

    /**
     * 返回成功响应
     *
     * @return {@link BaseResponse<Object>}
     */
    public BaseResponse<Object> success() {
        return success(null);
    }

    /**
     * 返回成功响应
     *
     * @return {@link BaseResponse<Object>}
     */
    public BaseResponse<Object> success(Object object) {
        BaseResponse<Object> response = new BaseResponse();
        response.setTracestack(MDC.get(BadgerConstant.TRANSACTION_ID));
        response.setCode(BadgerConstant.SUCCESS_CODE);
        response.setMessage(BadgerConstant.SUCCESS_STATUS);
        response.setData(object);

        return response;
    }

    /**
     * 返回错误响应
     *
     * @param code 异常码
     * @param msg  异常信息
     * @return {@link BaseResponse<Object>}
     */
    public BaseResponse<Object> error(String code, String msg) {
        BaseResponse<Object> response = new BaseResponse();
        response.setTracestack(MDC.get(BadgerConstant.TRANSACTION_ID));
        response.setCode(code);
        response.setMessage(msg);

        return response;
    }
}
