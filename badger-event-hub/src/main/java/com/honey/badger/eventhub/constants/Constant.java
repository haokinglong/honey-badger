package com.honey.badger.eventhub.constants;

/**
 * 通用常量
 *
 * @author yinzhao
 * @date 2021/9/30
 */
public class Constant {

    private Constant() {
    }

    /**
     * rabbit topic交换机默认的路由key
     * <p>
     * 此处为了兼容PaaS的eventhub，默认的路由key使用了PaaS规定的
     * </p>
     */
    public static final String EVENTHUB_DEFAULT_ROUTING_KEY = "paas_default";

    /**
     * event hub 名称分隔符，用于拼接 domain/source/type/sub，来生成topic、queue等名称
     */
    public static final String EVENTHUB_NAME_SPLITTER = "__";

    /**
     * x-source 的 key
     */
    public static final String K_X_SOURCE = "x-source";

    /**
     * x-type 的 key
     */
    public static final String K_X_TYPE = "x-type";

    /**
     * x-routing-key 的 key
     */
    public static final String K_X_ROUTING_KEY = "x-routing-key";

    /**
     * x-req-id 的 key
     */
    public static final String K_X_REQ_ID = "x-req-id";

    /**
     * x-clz 的 key
     */
    public static final String K_X_CLZ = "x-clz";
}
