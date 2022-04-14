package com.honey.badger.record.pojo.entity;

import com.honey.badger.record.pojo.enums.ErrorRecordLevelEnum;
import com.honey.badger.record.pojo.enums.ErrorRecordStatusEnum;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 异常日志记录表
 *
 * @author hanlining
 * @date 2021/3/31
 */
@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ErrorRecord implements Serializable {

    /**
     * 自增主键ID
     */
    private Long id;

    /**
     * 微服务名(nacos注册中的服务名)code
     */
    private Integer serviceCode;

    /**
     * 操作(每个异常记录的动作)
     */
    private String action;

    /**
     * 载体
     */
    private String payload;

    /**
     * 链路追踪id
     */
    private String traceId;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 目标类的全限定名
     */
    private String classQualifier;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 状态：0已处理 1待处理 2处理中 3暂停处理
     *
     * @see ErrorRecordStatusEnum
     */
    private Integer status;

    /**
     * 级别：10最高级别 1最低级别
     *
     * @see ErrorRecordLevelEnum
     */
    private Integer level;

    /**
     * 尝试的次数
     */
    private Integer triedCount;

    /**
     * 业务id
     *
     * @see java.util.UUID#randomUUID()
     */
    private String uuid;

    /**
     * 冗余字段,建议使用json存储,便于后期统一扩展
     */
    private String extra;
}
