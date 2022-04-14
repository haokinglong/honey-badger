package com.honey.badger.core.enums.base;

/**
 * 基础枚举接口
 *
 * @author haojinlong
 * @date 2021/2/1
 */
public interface BaseEnum<T> {

    /**
     * 获取对应的{@code code}
     *
     * @return {@link T}
     */
    T getCode();

    /**
     * 获取对应的{@code value}值
     *
     * @return {@link String}
     */
    String getValue();

    /**
     * 根据{@code code}获取相应的枚举值
     *
     * @param code 枚举key
     * @return 对应的枚举值
     */
    default String getValueByCode(String code) {
        return null;
    }
}
