package com.honey.badger.core.exception;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 关于通用的业务异常类
 *
 * @author haojinlong
 * @date 2021/2/1
 */
public class BadgerBusinessException extends RuntimeException {

    /**
     * 枚举码
     */
    public final AtomicReference<String> code = new AtomicReference<>();
    /**
     * 枚举描述
     */
    public final AtomicReference<String> msg = new AtomicReference<>();

    public BadgerBusinessException() {
        super();
    }

    public BadgerBusinessException(String msg) {
        super(msg);
        this.msg.set(msg);
    }

    public BadgerBusinessException(String code, String msg) {
        super(msg);
        this.msg.set(msg);
        this.code.set(code);
    }

    public BadgerBusinessException(String code, String msg, Throwable cause) {
        super(msg, cause);
        this.code.set(code);
    }

    public String getCode() {
        return code.get();
    }

    public String getMsg() {
        return msg.get();
    }
}
