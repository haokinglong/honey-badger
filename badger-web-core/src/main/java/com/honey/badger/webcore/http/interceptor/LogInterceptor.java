package com.honey.badger.webcore.http.interceptor;


import static cn.hutool.core.text.CharSequenceUtil.isNotEmpty;
import static com.honey.badger.core.constants.BadgerConstant.REMOTE_IP;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import com.honey.badger.core.constants.BadgerConstant;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 系统日志拦截器
 *
 * <p>
 * 主要是为全链路日志追踪，错误排查做辅助
 * </p>
 *
 * @author haojinlong
 * @date 2020/9/9
 */
@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    private static final String UN_KNOWN = "unKnown";
    private static final String X_FORWARDED_FOR = "X-Forwarded-For";

    /**
     * 当请求退出时,需要清除{@link MDC} 中缓存的信息,以免占用资源
     *
     * @param request  {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     * @param arg2     {@link Object}
     * @param arg3     {@link Exception}
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object arg2, Exception arg3) {
        MDC.remove(REMOTE_IP);
        MDC.remove(BadgerConstant.TRANSACTION_ID);
        MDC.remove(BadgerConstant.REQUEST_IP);
        MDC.remove(BadgerConstant.USER_IP);
    }

    /**
     * 使用{@link MDC}辅助打印日志
     *
     * <p>{@code Pass}平台网关在过滤请求时会自动在请求头添加请求标识:
     * <ol>
     *     <li>{@link BadgerConstant#TRANSACTION_ID}:链路追踪全局id</li>
     *     <li>{@link BadgerConstant#REMOTE_IP}: 远程访问ip</li>
     * </ol>
     * <p>如果不是从{@code Pass}网关而来的请求,也就是{@link BadgerConstant#TRANSACTION_ID}为空,
     * 则会自动生成{@code uuid}放入其中,这里需要将其从请求头取出,存放入{@link MDC}内,当输出日志时会自动获取
     *
     * @param request  {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     * @param handler  {@link Object}
     * @return {@link Boolean}
     * @author haojinlong
     * @date 2020/9/9 10:31
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String traceId = request.getHeader(BadgerConstant.TRANSACTION_ID);

        MDC.put(BadgerConstant.TRANSACTION_ID, ObjectUtil.defaultIfBlank(traceId, UUID.randomUUID().toString()));
        MDC.put(BadgerConstant.REMOTE_IP, request.getHeader(BadgerConstant.REMOTE_IP));
        MDC.put(BadgerConstant.REQUEST_IP, getRequestIp(request));
        MDC.put(BadgerConstant.USER_IP, getUserIp(request));

        return true;
    }

    private static String getRequestIp(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    private static String getUserIp(HttpServletRequest request) {
        String ip = request.getHeader(X_FORWARDED_FOR);
        if (isNotEmpty(ip) && !UN_KNOWN.equalsIgnoreCase(ip)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader(REMOTE_IP);
        if (isNotEmpty(ip) && !UN_KNOWN.equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

}