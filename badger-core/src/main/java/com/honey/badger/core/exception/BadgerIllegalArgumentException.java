package com.honey.badger.core.exception;

import com.honey.badger.core.enums.error.BizErrorEnum;

/**
 * {@code badger}参数异常
 *
 * @author hanlining
 * @date 2021/3/3
 */
public class BadgerIllegalArgumentException extends BadgerBusinessException {

    public BadgerIllegalArgumentException() {
        super(BizErrorEnum.GP4000.getCode(), BizErrorEnum.GP4000.getValue());
    }

    /**
     * {@link BizErrorEnum#GP4000}
     *
     * @param msg 异常信息
     */
    public BadgerIllegalArgumentException(String msg) {
        super(BizErrorEnum.GP4000.getCode(), msg);
    }
}

