package com.honey.badger.record.pojo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误状态：0已处理 1待处理 2处理中 3暂停处理
 *
 * @author yinzhao
 * @date 2021/6/24
 */
@Getter
@AllArgsConstructor
public enum ErrorRecordStatusEnum {

    /**
     * 已处理
     */
    SOLVED(0, "已处理"),

    /**
     * 待处理
     */
    UNSOLVED(1, "待处理"),

    /**
     * 处理中
     */
    SOLVING(2, "处理中"),

    /**
     * 暂停处理
     */
    FREEZE(3, "暂停处理");

    private final int code;
    private final String value;

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
