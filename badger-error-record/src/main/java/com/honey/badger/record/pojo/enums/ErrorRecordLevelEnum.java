package com.honey.badger.record.pojo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误级别：10最高级别 1最低级别
 *
 * @author yinzhao
 * @date 2021/6/24
 */
@Getter
@AllArgsConstructor
public enum ErrorRecordLevelEnum {

    /**
     * 10最高级别
     */
    HIGHEST(10, "10最高级别"),

    /**
     * 1最低级别
     */
    LOWEST(1, "1最低级别");

    private final int code;
    private final String value;

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}