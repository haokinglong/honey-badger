package com.honey.badger.core.constants;

/**
 * 全局配置常量
 *
 * @author haojinlong
 * @date 2021/1/10
 */
public class BadgerConstant {

    private BadgerConstant() {
    }

    /**
     * 成功编码
     */
    public static final String SUCCESS_CODE = "1000";
    /**
     * 成功状态码
     */
    public static final String SUCCESS_STATUS = "ok";
    /**
     * 失败状态码
     */
    public static final String FAIL_STATUS = "no";
    /**
     * 失败编码
     */
    public static final String FAIL_CODE = "9999";
    /**
     * 网关携带而来的链路追踪ID
     */
    public static final String TRANSACTION_ID = "Transaction-Id";
    /**
     * {@code Pass}网关携带而来的远程IP
     */
    public static final String REMOTE_IP = "X-Real-IP";
    /**
     * 请求的{@code IP},使用时通过{@code request.getRemoteAddr()}获取
     */
    public static final String REQUEST_IP = "requestIp";
    /**
     * 用户真实{@code IP}
     */
    public static final String USER_IP = "userIp";
    /**
     * 第三方远程服务名
     */
    public static final String X_REMOTE_MS_NAME = "X-Remote-Ms-Name";
}