package com.honey.badger.webcore.http.handler;


import static cn.hutool.core.text.CharSequenceUtil.isBlank;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.honey.badger.core.constants.BadgerConstant;
import com.honey.badger.core.dto.BaseResponse;
import com.honey.badger.core.enums.error.BizErrorEnum;
import com.honey.badger.core.exception.BadgerBusinessException;
import com.honey.badger.core.support.BaseResponseUtil;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.ContentCachingRequestWrapper;

/**
 * 全局异常捕获
 *
 * @author haojinlong
 * @date 2021/2/1
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@ResponseBody
public class BadgerExceptionHandler {

    /**
     * 内部微服务路径前缀
     * <p>
     * 我们约定:内部微服务路径前缀统一为{@code '/_private/'}
     * </p>
     */
    private static final String INTERNAL_MS_PATH_PREFIX = "/_private/";

    /**
     * {@code Validate}异常
     *
     * @param ex      异常
     * @param request http请求
     * @return {@link BaseResponse}
     * @author haojinlong
     * @date 2021/11/2
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<Object> validationExceptionHandler(MethodArgumentNotValidException ex, HttpServletRequest request) {
        StringBuilder errorMessage = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach(x ->
            errorMessage.append(x.getDefaultMessage()).append(",")
        );

        if (request instanceof ContentCachingRequestWrapper) {
            log.warn("error url ={}", request.getRequestURI());
        }

        log.warn("ValidationException error ={}", errorMessage.toString().substring(0, errorMessage.length() - 1));

        return BaseResponseUtil.getInstance().error(BizErrorEnum.GP4000.getCode(), errorMessage.toString().substring(0, errorMessage.length() - 1));
    }

    /**
     * 空指针异常
     *
     * @param ex      异常
     * @param request http请求
     * @return {@link BaseResponse}
     * @author haojinlong
     * @date 2021/11/2
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(NullPointerException.class)
    public BaseResponse<Object> nullPointerExceptionHandler(NullPointerException ex, HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            log.error("error url ={}", request.getRequestURI());
        }

        log.error("NullPointerException error ", ex);

        return BaseResponseUtil.getInstance().error(BizErrorEnum.GP1001.getCode(), BizErrorEnum.GP1001.getValue());
    }

    /**
     * 类型转换异常
     *
     * @param ex      异常
     * @param request http请求
     * @return {@link BaseResponse}
     * @author haojinlong
     * @date 2021/11/2
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ClassCastException.class)
    public BaseResponse<Object> classCastExceptionHandler(ClassCastException ex, HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            log.error("error url ={}", request.getRequestURI());
        }

        log.error("ClassCastException error ", ex);

        return BaseResponseUtil.getInstance().error(BizErrorEnum.GP1001.getCode(), BizErrorEnum.GP1001.getValue());
    }

    /**
     * IO异常
     *
     * @param ex      异常
     * @param request http请求
     * @return {@link BaseResponse}
     * @author haojinlong
     * @date 2021/11/2
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IOException.class)
    public BaseResponse<Object> ioExceptionHandler(IOException ex, HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            log.error("error url ={}", request.getRequestURI());
        }

        log.error("IOException error ", ex);

        return BaseResponseUtil.getInstance().error(BizErrorEnum.GP1001.getCode(), BizErrorEnum.GP1001.getValue());
    }

    /**
     * 数组越界异常
     *
     * @param ex      异常
     * @param request http请求
     * @return {@link BaseResponse}
     * @author haojinlong
     * @date 2021/11/2
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IndexOutOfBoundsException.class)
    public BaseResponse<Object> indexOutOfBoundsExceptionHandler(IndexOutOfBoundsException ex, HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            log.error("error url ={}", request.getRequestURI());
        }

        log.error("IndexOutOfBoundsException error ", ex);

        return BaseResponseUtil.getInstance().error(BizErrorEnum.GP1001.getCode(), BizErrorEnum.GP1001.getValue());
    }

    /**
     * 405错误
     *
     * @param ex      异常
     * @param request http请求
     * @return {@link BaseResponse}
     * @author haojinlong
     * @date 2021/11/2
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public BaseResponse<Object> request405(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            log.error("error url ={}", request.getRequestURI());
        }

        log.error("HttpRequestMethodNotSupportedException error ", ex);

        return BaseResponseUtil.getInstance().error(BizErrorEnum.GP4005.getCode(), BizErrorEnum.GP4005.getValue());
    }

    /**
     * {@code sentinel}流控错误
     *
     * @param ex      异常
     * @param request http请求
     * @return {@link BaseResponse}
     * @author haojinlong
     * @date 2021/11/2
     */
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    @ExceptionHandler({BlockException.class})
    public BaseResponse<Object> sentinelBlockExceptionHandler(BlockException ex, HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            log.error("error url ={}", request.getRequestURI());
        }

        log.error("BlockException error ", ex);

        return BaseResponseUtil.getInstance().error(BizErrorEnum.GP5429.getCode(), BizErrorEnum.GP5429.getValue());
    }

    /**
     * 406错误
     *
     * @param ex      异常
     * @param request http请求
     * @return {@link BaseResponse}
     * @author haojinlong
     * @date 2021/11/2
     */
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler({HttpMediaTypeNotAcceptableException.class})
    public BaseResponse<Object> request406(HttpMediaTypeNotAcceptableException ex, HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            log.error("error url ={}", request.getRequestURI());
        }

        log.error("HttpMediaTypeNotAcceptableException error ", ex);

        return BaseResponseUtil.getInstance().error(BizErrorEnum.GP4006.getCode(), BizErrorEnum.GP1001.getValue());
    }

    /**
     * 运行时错误
     *
     * @param ex      异常
     * @param request http请求
     * @return {@link BaseResponse}
     * @author haojinlong
     * @date 2021/11/2
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({ConversionNotSupportedException.class, HttpMessageNotWritableException.class})
    public BaseResponse<Object> runtimeException(RuntimeException ex, HttpServletRequest request, HttpServletResponse response) {
        if (request instanceof ContentCachingRequestWrapper) {
            log.error("error url ={}", request.getRequestURI());
        }

        log.error("RuntimeException error ", ex);

        return BaseResponseUtil.getInstance().error(BizErrorEnum.GP1001.getCode(), BizErrorEnum.GP1001.getValue());
    }

    /**
     * 其他错误
     *
     * @param ex      异常
     * @param request http请求
     * @return {@link BaseResponse}
     * @author haojinlong
     * @date 2021/11/2
     */
    @ExceptionHandler({Exception.class})
    public BaseResponse<Object> exception(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        if (request instanceof ContentCachingRequestWrapper) {
            log.error("error url ={}", request.getRequestURI());
        }

        log.error("Exception error ", ex);

        boolean blockException = BlockException.isBlockException(ex.getCause());
        if (blockException) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            return BaseResponseUtil.getInstance().error(BizErrorEnum.GP5429.getCode(), BizErrorEnum.GP5429.getValue());
        }

        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return BaseResponseUtil.getInstance().error(BizErrorEnum.GP1001.getCode(), BizErrorEnum.GP1001.getValue());
    }

    /**
     * 数据库异常全局捕获
     *
     * @param ex       {@link SQLException}
     * @param request  {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     * @return {@link BaseResponse<Object>}
     * @author haojinlong
     * @date 2021/11/2
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SQLException.class)
    public BaseResponse<Object> badSqlGrammarExceptionHandler(SQLException ex, HttpServletRequest request, HttpServletResponse response) {
        log.error("sql exception", ex);
        return BaseResponseUtil.getInstance().error(BizErrorEnum.GP1002.getCode(), BizErrorEnum.GP1002.getValue());
    }

    //********************************************************* 以下均为自定义异常的封装处理 *********************************************************************//

    /**
     * 自定义异常处理
     * <p>
     * 凡是继承了{@link BadgerBusinessException}的异常,均可在此统一进行拦截、处理
     * </p>
     *
     * @param ex       异常
     * @param request  http请求
     * @param response http响应
     * @return {@link BaseResponse}
     */
    @ExceptionHandler(BadgerBusinessException.class)
    public BaseResponse<Object> badgerBusinessExceptionHandler(BadgerBusinessException ex, HttpServletRequest request, HttpServletResponse response) {
        if (request.getRequestURI().contains(INTERNAL_MS_PATH_PREFIX)) {
            response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
        } else {
            response.setStatus(HttpStatus.OK.value());
        }
        if (request instanceof ContentCachingRequestWrapper) {
            log.info("error url ={}", request.getRequestURI());
        }

        log.info("{} error: code ={}, msg ={}", ex.getClass().getSimpleName(), isBlank(ex.getCode()) ? BadgerConstant.FAIL_CODE : ex.getCode(), ex.getMsg(), ex);

        return BaseResponseUtil.getInstance().error(isBlank(ex.getCode()) ? BadgerConstant.FAIL_CODE : ex.getCode(), ex.getMsg());
    }
}