package com.honey.badger.eventhub.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 事件体
 *
 * @author yinzhao
 * @date 2021/9/30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventPayload<T extends Serializable> implements Serializable {

    /**
     * sender 事件 json 对象
     */
    private String body;

    /**
     * json 反序列化后的 listener 返回体
     */
    private T param;

    /**
     * rabbit mq 队列名
     */
    private String queueName;
}
