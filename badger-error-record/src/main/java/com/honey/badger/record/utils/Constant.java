package com.honey.badger.record.utils;

/**
 * 全局配置常量
 *
 * @author haojinlong
 * @date 2021/1/10
 */
public class Constant {

    private Constant() {
    }

    /**
     * {@code badger}系统请求头统一默认用户ID
     */
    public static final String X_USER_ID = "X-badger-UserId";

    /**
     * {@code AP}框架内部通行所用的链路追踪ID 但是若没有携带该值,框架会自动生成随机值放入其中
     * </p>
     *
     * @author haojinlong
     * @date 2021/1/10
     */
    public static final String TRANSACTION_ID = "badger-TraceId";

    /**
     * 异常记录默认的create_by
     */
    public static final String ERROR_RECORD_DEFAULT_CREAT_BY = "0";

}