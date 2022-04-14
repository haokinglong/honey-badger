package com.honey.badger.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 描述: 基础响应体
 *
 * @author haojinlong
 * @date 2021/3/23
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {

    /**
     * 业务状态码
     */
    private String code;
    /**
     * 业务响应信息
     */
    private String message;
    /**
     * 全局业务链路追踪id
     */
    private String tracestack;
    /**
     * 数据
     */
    private T data;
}
