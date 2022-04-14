package com.honey.badger.cache.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 缓存异常类
 *
 * @author haojinlong
 * @date 2021/2/1
 */
@Setter
@Getter
public class BadgerCacheException extends RuntimeException {

    /**
     * 枚举码
     */
    public final String code;

    /**
     * 枚举描述
     */
    public final String msg;


    public BadgerCacheException() {
        super();
        this.msg = "GP1010";
        this.code = "系统异常";
    }
}
