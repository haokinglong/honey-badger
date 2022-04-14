package com.honey.badger.record.utils;

import static cn.hutool.core.text.CharSequenceUtil.isBlank;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.honey.badger.record.pojo.entity.ErrorRecord;
import com.honey.badger.record.pojo.enums.ErrorRecordLevelEnum;
import com.honey.badger.record.pojo.enums.ErrorRecordStatusEnum;
import com.honey.badger.record.annotation.BadgerNeedErrorRecord;
import com.honey.badger.record.pojo.entity.ErrorRecord.ErrorRecordBuilder;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.UUID;
import org.slf4j.MDC;

/**
 * 描述: 异常记录帮助类
 * <pre>{@code
 * errorRecordService.produceErrorRecordMsg(new ErrorRecordHelper<>(getClass(), "pushMsgToFeed").getErrorRecord(multiAbilityReqDto, multiAbilityReqDto.getTraceId()));
 * }</pre>
 *
 * @author haojinlong
 * @date 2021/6/9
 */
public class ErrorRecordHelper<T> {

    private final Class<T> cls;
    private final String methodName;

    public ErrorRecordHelper(Class<T> cls, String method) {
        this.cls = cls;
        this.methodName = method;
    }

    /**
     * 获取异常记录信息
     *
     * @return {@link ErrorRecord}
     * @author haojinlong
     * @date 2021/6/9
     */
    public ErrorRecord getErrorRecord(Object payload, String traceId) {
        return getErrorRecord(payload, ErrorRecordLevelEnum.LOWEST.getCode(), traceId);
    }

    /**
     * 获取异常记录信息，并配置错误级别
     *
     * @return {@link ErrorRecord}
     * @author haojinlong
     * @date 2021/6/9
     */
    public ErrorRecord getErrorRecord(Object payload, Integer level, String traceId) {
        ErrorRecordBuilder recordBuilder = ErrorRecord.builder();
        Method method = getMethod();
        if (method == null) {
            return new ErrorRecord();
        }

        BadgerNeedErrorRecord recordAnnotation = method.getAnnotation(BadgerNeedErrorRecord.class);
        recordBuilder.createBy(Constant.ERROR_RECORD_DEFAULT_CREAT_BY);
        recordBuilder.createTime(new Date());
        if (isBlank(traceId)) {
            recordBuilder.traceId(MDC.get(Constant.TRANSACTION_ID));
        } else {
            recordBuilder.traceId(traceId);
        }
        if (ObjectUtil.isNotNull(method)) {
            recordBuilder.action(method.getName());
            recordBuilder.classQualifier(method.toGenericString());
        }
        if (ObjectUtil.isNotNull(payload)) {
            recordBuilder.payload(JSONUtil.toJsonStr(payload));
        }
        if (ObjectUtil.isNotNull(recordAnnotation)) {
            recordBuilder.methodName(recordAnnotation.actionName());
            recordBuilder.serviceCode(recordAnnotation.serviceCode());
        }
        recordBuilder.status(ErrorRecordStatusEnum.UNSOLVED.getCode());
        recordBuilder.level(level);
        recordBuilder.uuid(UUID.randomUUID().toString());
        recordBuilder.triedCount(0);

        return recordBuilder.build();
    }

    /**
     * 获取指定方法
     *
     * @return {@link Method}
     * @author haojinlong
     * @date 2021/6/9
     */
    private Method getMethod() {
        Method[] declaredMethods = cls.getDeclaredMethods();
        for (Method m : declaredMethods) {
            if (this.methodName.equals(m.getName())) {
                return m;
            }
        }

        return null;
    }

}
