package com.honey.badger.record.service;

import com.honey.badger.record.autoconfigure.properties.BadgerErrorRecordProperties;
import com.honey.badger.record.pojo.entity.ErrorRecord;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * 异常记录处理器
 *
 * @author hanlining
 * @date 2021/4/21
 */
@Slf4j
@AllArgsConstructor
public class BadgerErrorRecordHandler {

    private final RabbitTemplate rabbitTemplate;

    private final BadgerErrorRecordProperties badgerErrorRecordProperties;

    /**
     * 将异常记录加入{@code rabbit mq}
     * <pre>{@code
     * errorRecordHandler.produceErrorRecordMsg(
     *      new ErrorRecordHelper<>(getClass(), "pushMsgToFeed").getErrorRecord(multiAbilityReqDto, multiAbilityReqDto.getTraceId())
     * );
     * }</pre>
     *
     * @param errorRecord 异常记录请求体
     * @author hanlining
     * @date 2021/4/21
     */
    public void produceErrorRecordMsg(ErrorRecord errorRecord) {
        try {
            rabbitTemplate.convertAndSend(badgerErrorRecordProperties.getErrorRecord().getDirectExchange(), badgerErrorRecordProperties.getErrorRecord().getQueue(), errorRecord);
        } catch (Exception e) {
            log.error("badger error record exception", e);
        }
    }
}
