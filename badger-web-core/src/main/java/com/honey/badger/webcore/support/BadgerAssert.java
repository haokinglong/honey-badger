package com.honey.badger.webcore.support;

import static cn.hutool.core.text.CharSequenceUtil.isNotBlank;

import com.honey.badger.core.enums.base.BaseEnum;
import com.honey.badger.core.exception.BadgerBusinessException;
import com.honey.badger.core.exception.BadgerIllegalArgumentException;
import java.util.Collection;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * 描述: {@code badger}系统断言工具
 * <p>
 * 该工具抛出的异常均为{@code badger}系统自有异常,便于系统全局异常捕获使用
 * </p>
 *
 * @author haojinlong
 * @date 2021/3/25
 */
@Slf4j
public abstract class BadgerAssert {

    private BadgerAssert() {
    }

    /**
     * 是否正确的断言
     * <p>
     * 如果{@code expression}结果为{@code false},则会抛出指定{@link BadgerBusinessException}业务码的异常
     *
     * @param bizErrorEnum 实现了{@link BaseEnum}接口的异常枚举
     * @param expression   表达式
     * @param outputLog    日志输出的信息
     * @param logObj       日志打印的对象
     * @author haojinlong
     * @date 2021/7/26
     * @see BadgerBusinessException
     */
    public static void isTrue(boolean expression, @Nullable BaseEnum<String> bizErrorEnum, String outputLog, Object... logObj) {
        if (!expression) {
            if (isNotBlank(outputLog)) {
                log.warn(outputLog, logObj);
            }
            throwBizException(bizErrorEnum);
        }
    }

    public static void isTrue(boolean expression, String resMsg, String outputLog, Object... logObj) {
        if (!expression) {
            if (isNotBlank(outputLog)) {
                log.warn(outputLog, logObj);
            }
            throw new BadgerIllegalArgumentException(resMsg);
        }
    }

    public static void isTrue(boolean expression, String resMsg) {
        isTrue(expression, resMsg, null);
    }

    /**
     * 对象为空的断言
     * <p>
     * 当对象不为空时,则会抛出指定{@link BadgerBusinessException}业务码的异常
     *
     * @param bizErrorEnum 实现了{@link BaseEnum}接口的异常枚举
     * @param object       断言对象
     * @param outputLog    日志输出的信息
     * @param logObj       日志打印的对象
     * @author haojinlong
     * @date 2021/7/26
     * @see BadgerBusinessException
     */
    public static void isNull(@Nullable Object object, @Nullable BaseEnum<String> bizErrorEnum, String outputLog, Object... logObj) {
        if (object != null) {
            if (isNotBlank(outputLog)) {
                log.warn(outputLog, logObj);
            }
            throwBizException(bizErrorEnum);
        }
    }

    public static void isNull(@Nullable Object object, String resMsg, String outputLog, Object... logObj) {
        if (object != null) {
            if (isNotBlank(outputLog)) {
                log.warn(outputLog, logObj);
            }
            throw new BadgerIllegalArgumentException(resMsg);
        }
    }

    public static void isNull(@Nullable Object object, String resMsg) {
        isNull(object, resMsg, null);
    }

    /**
     * 对象不为空的断言
     * <p>
     * 当对象为空时,则会抛出指定{@link BadgerBusinessException}业务码的异常
     *
     * @param bizErrorEnum 实现了{@link BaseEnum}接口的异常枚举
     * @param object       断言对象
     * @param outputLog    日志输出的信息
     * @param logObj       日志打印的对象
     * @author haojinlong
     * @date 2021/7/26
     * @see BadgerBusinessException
     */
    public static void notNull(@Nullable Object object, @Nullable BaseEnum<String> bizErrorEnum, String outputLog, Object... logObj) {
        if (object == null) {
            if (isNotBlank(outputLog)) {
                log.warn(outputLog, logObj);
            }
            throwBizException(bizErrorEnum);
        }
    }

    public static void notNull(@Nullable Object object, String resMsg, String outputLog, Object... logObj) {
        if (object == null) {
            if (isNotBlank(outputLog)) {
                log.warn(outputLog, logObj);
            }
            throw new BadgerIllegalArgumentException(resMsg);
        }
    }

    public static void notNull(@Nullable Object object, String resMsg) {
        notNull(object, resMsg, null);
    }

    /**
     * 文本对象有长度的断言
     * <p>
     * 当文本对象有长度时,则会抛出指定{@link BadgerBusinessException}业务码的异常
     *
     * @param bizErrorEnum 实现了{@link BaseEnum}接口的异常枚举
     * @param text         断言对象
     * @author haojinlong
     * @date 2021/7/26
     * @see BadgerBusinessException
     */
    public static void hasLength(@Nullable String text, @Nullable BaseEnum<String> bizErrorEnum) {
        if (!StringUtils.hasLength(text)) {
            throwBizException(bizErrorEnum);
        }
    }

    public static void hasLength(@Nullable String text, String resMsg) {
        if (!StringUtils.hasLength(text)) {
            throw new BadgerIllegalArgumentException(resMsg);
        }
    }

    public static void hasLength(@Nullable String text, String resMsg, String outputLog, Object... logObj) {
        if (isNotBlank(outputLog)) {
            log.warn(outputLog, logObj);
        }
        hasLength(text, resMsg);
    }

    public static void hasText(@Nullable String text, String resMsg, String outputLog, Object... logObj) {
        if (!StringUtils.hasText(text)) {
            if (isNotBlank(outputLog)) {
                log.warn(outputLog, logObj);
            }
            throw new BadgerIllegalArgumentException(resMsg);
        }
    }

    /**
     * 文本对象有值的断言
     * <p>
     * 当文本对象有值时,则会抛出指定{@link BadgerBusinessException}业务码的异常
     *
     * @param bizErrorEnum 实现了{@link BaseEnum}接口的异常枚举
     * @param text         断言对象
     * @param outputLog    日志输出的信息
     * @param logObj       日志打印的对象
     * @author haojinlong
     * @date 2021/7/26
     * @see BadgerBusinessException
     */
    public static void hasText(@Nullable String text, @Nullable BaseEnum<String> bizErrorEnum, String outputLog, Object... logObj) {
        if (!StringUtils.hasText(text)) {
            if (isNotBlank(outputLog)) {
                log.warn(outputLog, logObj);
            }
            throwBizException(bizErrorEnum);
        }
    }

    public static void hasText(@Nullable String text, String resMsg) {
        hasText(text, resMsg, null);
    }

    public static void doesNotContain(@Nullable String textToSearch, String substring, String resMsg) {
        doesNotContain(textToSearch, substring, resMsg, null);
    }

    public static void doesNotContain(@Nullable String textToSearch, String substring, String resMsg, String outputLog, Object... logObj) {
        if (StringUtils.hasLength(textToSearch) && StringUtils.hasLength(substring) && textToSearch.contains(substring)) {
            if (isNotBlank(outputLog)) {
                log.warn(outputLog, logObj);
            }
            throw new BadgerIllegalArgumentException(resMsg);
        }
    }

    /**
     * 字符串内含指定元素的断言
     * <p>
     * 当字符串内含有指定字符时,则会抛出指定{@link BadgerBusinessException}业务码的异常
     *
     * @param bizErrorEnum 实现了{@link BaseEnum}接口的异常枚举
     * @param textToSearch 指定字符串
     * @param substring    指定字符
     * @param outputLog    日志输出的信息
     * @param logObj       日志打印的对象
     * @author haojinlong
     * @date 2021/7/26
     * @see BadgerBusinessException
     */
    public static void doesNotContain(@Nullable String textToSearch, @Nullable BaseEnum<String> bizErrorEnum, String substring, String outputLog, Object... logObj) {
        if (StringUtils.hasLength(textToSearch) && StringUtils.hasLength(substring) && textToSearch.contains(substring)) {
            if (isNotBlank(outputLog)) {
                log.warn(outputLog, logObj);
            }
            throwBizException(bizErrorEnum);
        }
    }

    /**
     * 数组对象不为空的断言
     * <p>
     * 当数组对象为空时,则会抛出指定{@link BadgerBusinessException}业务码的异常
     *
     * @param bizErrorEnum 实现了{@link BaseEnum}接口的异常枚举
     * @param array        数组对象
     * @param outputLog    日志输出的信息
     * @param logObj       日志打印的对象
     * @author haojinlong
     * @date 2021/7/26
     * @see BadgerBusinessException
     */
    public static void notEmpty(@Nullable Object[] array, @Nullable BaseEnum<String> bizErrorEnum, String outputLog, Object... logObj) {
        if (ObjectUtils.isEmpty(array)) {
            if (isNotBlank(outputLog)) {
                log.warn(outputLog, logObj);
            }
            throwBizException(bizErrorEnum);
        }
    }

    public static void notEmpty(@Nullable Object[] array, String resMsg, String outputLog, Object... logObj) {
        if (ObjectUtils.isEmpty(array)) {
            if (isNotBlank(outputLog)) {
                log.warn(outputLog, logObj);
            }
            throw new BadgerIllegalArgumentException(resMsg);
        }
    }

    public static void notEmpty(@Nullable Object[] array, String resMsg) {
        notEmpty(array, resMsg, null);
    }

    public static void noNullElements(@Nullable Object[] array, String resMsg, String outputLog, Object... logObj) {
        if (array != null) {
            Object[] var2 = array;
            int var3 = array.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                Object element = var2[var4];
                if (element == null) {
                    if (isNotBlank(outputLog)) {
                        log.warn(outputLog, logObj);
                    }
                    throw new BadgerIllegalArgumentException(resMsg);
                }
            }
        }
    }

    public static void noNullElements(@Nullable Object[] array, String resMsg) {
        noNullElements(array, resMsg, null);
    }

    /**
     * 集合对象不为空的断言
     * <p>
     * 当集合对象为空时,则会抛出指定{@link BadgerBusinessException}业务码的异常
     *
     * @param bizErrorEnum 实现了{@link BaseEnum}接口的异常枚举
     * @param collection   集合对象
     * @param outputLog    日志输出的信息
     * @param logObj       日志打印的对象
     * @author haojinlong
     * @date 2021/7/26
     * @see BadgerBusinessException
     */
    public static void notEmpty(@Nullable Collection<?> collection, @Nullable BaseEnum<String> bizErrorEnum, String outputLog, Object... logObj) {
        if (CollectionUtils.isEmpty(collection)) {
            if (isNotBlank(outputLog)) {
                log.warn(outputLog, logObj);
            }
            throwBizException(bizErrorEnum);
        }
    }

    public static void notEmpty(@Nullable Collection<?> collection, String resMsg, String outputLog, Object... logObj) {
        if (CollectionUtils.isEmpty(collection)) {
            if (isNotBlank(outputLog)) {
                log.warn(outputLog, logObj);
            }
            throw new BadgerIllegalArgumentException(resMsg);
        }
    }

    public static void notEmpty(@Nullable Collection<?> collection, String resMsg) {
        notEmpty(collection, resMsg, null);
    }

    public static void notEmpty(@Nullable Map<?, ?> map, String resMsg, String outputLog, Object... logObj) {
        if (CollectionUtils.isEmpty(map)) {
            if (isNotBlank(outputLog)) {
                log.warn(outputLog, logObj);
            }
            throw new BadgerIllegalArgumentException(resMsg);
        }
    }

    public static void notEmpty(@Nullable Map<?, ?> map, String resMsg) {
        notEmpty(map, resMsg, null);
    }

    /**
     * 统一抛出{@code badger}业务异常
     *
     * @param bizErrorEnum 实现了{@link BaseEnum}接口的异常枚举
     * @author haojinlong
     * @date 2021/7/26
     * @see BadgerBusinessException
     */
    private static void throwBizException(BaseEnum<String> bizErrorEnum) {
        if (bizErrorEnum != null) {
            throw new BadgerBusinessException(bizErrorEnum.getCode(), bizErrorEnum.getValue());
        }
    }
}
