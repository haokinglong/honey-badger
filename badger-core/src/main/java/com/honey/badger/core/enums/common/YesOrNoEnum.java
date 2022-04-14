package com.honey.badger.core.enums.common;

import com.honey.badger.core.enums.base.BaseEnum;

/**
 * 是or否枚举
 *
 * @author haojinlong
 * @date 2021/11/2
 */
public enum YesOrNoEnum implements BaseEnum<Integer> {

    /**
     * 否
     */
    NO(0, "否"),
    /**
     * 是
     */
    YES(1, "是");

    /**
     * 枚举码
     */
    private final Integer code;
    /**
     * 枚举描述
     */
    private final String value;

    YesOrNoEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
