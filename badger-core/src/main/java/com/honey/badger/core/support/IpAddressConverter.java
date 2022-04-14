package com.honey.badger.core.support;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import lombok.extern.slf4j.Slf4j;

/**
 * 描述: 为log日志提供地址转换
 *
 * @author haojinlong
 * @date 2021/9/29
 */
@Slf4j
public class IpAddressConverter extends ClassicConverter {

    private static String ipAddress;

    static {
        try {
            ipAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            ipAddress = "UNKNOWN";
        }
    }

    @Override
    public String convert(ILoggingEvent iLoggingEvent) {
        return ipAddress;
    }
}
