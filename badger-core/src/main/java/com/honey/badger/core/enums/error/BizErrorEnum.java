package com.honey.badger.core.enums.error;


import com.honey.badger.core.enums.base.BaseEnum;
import lombok.Getter;

/**
 * 业务异常定义
 *
 * @author haojinlong
 * @date 2021/2/1
 */
@Getter
public enum BizErrorEnum implements BaseEnum<String> {

    /**
     * 常见的运行时异常 {@link RuntimeException}
     */
    GP1001("GP1001", "系统异常"),
    /**
     * 数据库异常
     */
    GP1002("GP1002", "系统异常"),
    /**
     * Tablestore异常
     */
    GP1007("GP1007", "Tablestore异常"),
    /**
     * 请求太多:429异常
     */
    GP5429("GP5429", "请求太多"),
    /**
     * 参数异常
     */
    GP4000("GP4000", "参数异常"),
    /**
     * Method Not Allowed
     */
    GP4005("GP4005", "Method Not Allowed"),
    /**
     * Not Acceptable
     */
    GP4006("GP4006", "Not Acceptable"),
    /**
     * 三方服务异常
     */
    GP5001("GP5001", "三方服务异常"),
    /**
     * 三方服务异常:502异常
     */
    GP5002("GP5002", "三方服务异常"),
    /**
     * 三方服务异常:503异常
     */
    GP5003("GP5003", "三方服务异常"),
    /**
     * 三方服务异常:504异常
     */
    GP5004("GP5004", "三方服务异常");

    /**
     * 枚举码
     */
    private final String code;
    /**
     * 枚举描述
     */
    private final String value;

    BizErrorEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public String getCode() {
        return this.code;
    }
}
