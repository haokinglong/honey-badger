package com.honey.badger.webcore.config;

import cn.hutool.core.util.StrUtil;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

/**
 * {@link String}时间类型参数自动转换配置
 *
 * <p>
 * 可以用来解决前端传入的时间参数与后端类型不匹配的问题
 * <p>
 * 主要避免类似问题 {@code org.springframework.validation.BindException: org.springframework.validation.BeanPropertyBindingResult}
 * </p>
 *
 * @author haojinlong
 * @date 2020/9/13
 */
@Configuration
public class StringToDateConverter implements Converter<String, Date> {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String SHORT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATE_FORMAT_2 = "yyyy/MM/dd HH:mm:ss";
    private static final String SHORT_DATE_FORMAT_2 = "yyyy/MM/dd";

    @Override
    public Date convert(String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }
        source = source.trim();
        try {
            SimpleDateFormat formatter;
            if (source.contains(StrUtil.DASHED)) {
                if (source.contains(StrUtil.COLON)) {
                    formatter = new SimpleDateFormat(DATE_FORMAT);
                } else {
                    formatter = new SimpleDateFormat(SHORT_DATE_FORMAT);
                }

                return formatter.parse(source);

            } else if (source.contains(StrUtil.SLASH)) {
                if (source.contains(StrUtil.COLON)) {
                    formatter = new SimpleDateFormat(DATE_FORMAT_2);
                } else {
                    formatter = new SimpleDateFormat(SHORT_DATE_FORMAT_2);
                }

                return formatter.parse(source);
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("parser %s to Date fail", source));
        }

        throw new RuntimeException(String.format("parser %s to Date fail", source));
    }
}
