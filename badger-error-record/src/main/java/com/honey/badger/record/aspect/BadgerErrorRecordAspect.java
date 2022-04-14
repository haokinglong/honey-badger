package com.honey.badger.record.aspect;

import static cn.hutool.core.text.CharSequenceUtil.isBlank;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.honey.badger.record.pojo.entity.ErrorRecord;
import com.honey.badger.record.utils.Constant;
import com.honey.badger.record.annotation.BadgerNeedErrorRecord;
import com.honey.badger.record.service.BadgerErrorRecordHandler;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 处理异常记录的 {@code aspect}
 *
 * @author hanlining
 * @date 2021/4/21
 */
@Slf4j
@Aspect
@AllArgsConstructor
public class BadgerErrorRecordAspect {

    private final BadgerErrorRecordHandler errorRecordHandler;

    /**
     * 匹配所有的{@link BadgerNeedErrorRecord} 注解
     *
     * @author hanlining
     * @date 2021/4/21
     */
    @Pointcut(value = "@annotation(com.honey.badger.record.annotation.BadgerNeedErrorRecord)")
    public void pointCut() {
    }

    /**
     * 方法异常时,执行记录{@code error_record}
     *
     * @param joinPoint 织入点对象
     * @param ex        异常对象
     * @author hanlining
     * @date 2021/4/21
     */
    @AfterThrowing(throwing = "ex", value = "pointCut()")
    public void handleErrorRecord(JoinPoint joinPoint, Throwable ex) {
        Date createTime = new Date();

        try {
            Class<?> clazz = joinPoint.getTarget().getClass();
            String methodName = joinPoint.getSignature().getName();
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Class<?>[] parameterTypes = methodSignature.getMethod().getParameterTypes();
            Method method = clazz.getMethod(methodName, parameterTypes);
            BadgerNeedErrorRecord recordAnnotation = method.getAnnotation(BadgerNeedErrorRecord.class);

            if (ObjectUtil.isNotNull(recordAnnotation) && !recordAnnotation.needAopRecord()) {
                return;
            }

            ErrorRecord errorRecord = ErrorRecord.builder().serviceCode(recordAnnotation.serviceCode())
                .action(methodName).traceId(MDC.get(Constant.TRANSACTION_ID)).payload(getPayload(joinPoint))
                .methodName(methodName).classQualifier(method.toGenericString())
                .createTime(createTime).createBy(getCreateBy()).build();

            errorRecordHandler.produceErrorRecordMsg(errorRecord);
        } catch (NoSuchMethodException e) {
            log.error("InsertErrorRecord error , target ={}", joinPoint.getTarget(), e);
        }
    }

    /**
     * 生成{@code payload}
     *
     * @param joinPoint 切入点对象
     * @return {@link String}
     * @author hanlining
     * @date 2021/4/23
     */
    private String getPayload(JoinPoint joinPoint) {
        String payload = null;
        Object[] args = joinPoint.getArgs();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] argNames = methodSignature.getParameterNames();
        Map<String, Object> payloadMap = new HashMap<>(argNames.length);

        for (int i = 0; i < argNames.length; i++) {
            payloadMap.put(argNames[i], args[i]);
        }

        if (CollUtil.isNotEmpty(payloadMap)) {
            payload = JSONUtil.toJsonStr(payloadMap);
        }

        return payload;
    }

    /**
     * 获取当前用户ID
     * <p>
     * 先从请求头中获取,如果获取不到则取默认的用户ID
     * </p>
     *
     * @return {@link String}
     * @author hanlining
     * @date 2021/4/22
     */
    private String getCreateBy() {
        String userId = null;
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();
            userId = request.getHeader(Constant.X_USER_ID);
        }

        return isBlank(userId) ? Constant.ERROR_RECORD_DEFAULT_CREAT_BY : userId;
    }

}
