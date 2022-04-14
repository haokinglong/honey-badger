package com.honey.badger.core.support;

import cn.hutool.core.lang.Assert;
import com.honey.badger.core.enums.base.BaseEnum;
import com.honey.badger.core.exception.BadgerBusinessException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

/**
 * 描述: {@code Badger}系统枚举工具类
 *
 * @author haojinlong
 * @date 2021/3/25
 */
@Slf4j
public class BadgerEnumUtil {

    private BadgerEnumUtil() {
    }

    private static final String DEFAULT_GET_METHOD = "getCode";

    /**
     * 判断枚举中是否含有该{@code code}
     * <pre>
     * 该方法仅适用于业务系统中实现了
     * {@link BaseEnum},
     * 接口的所用枚举类
     * </pre>
     *
     * @param code  枚举的{@code code}
     * @param clazz 枚举类的字节码
     * @return {@code true/false}
     * @author haojinlong
     * @date 2021/3/25
     */
    public static boolean isIncluded(Object code, Class<?> clazz) {
        Assert.notNull(code, "code is null");
        Assert.notNull(clazz, "clazz is null");

        if (clazz.isEnum()) {
            try {
                Object[] objects = clazz.getEnumConstants();
                Method getCode = clazz.getMethod(DEFAULT_GET_METHOD);
                for (Object object : objects) {
                    Object reCode = getCode.invoke(object);
                    if (Objects.equals(reCode.toString(), code.toString())) {
                        return true;
                    }
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                log.info("enum isIncluded error", e);
                throw new BadgerBusinessException();
            }
        }

        return false;
    }
}
