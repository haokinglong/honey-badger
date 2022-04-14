package com.honey.badger.design.strategy.dispatcher;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * 描述: {@code Badger}策略模式分发器父类
 *
 * @author haojinlong
 * @date 2021/9/18
 */
public abstract class BadgerStrategyParentDispatcher {

    /**
     * 策略模式池
     */
    private final AtomicReference<Map<String, Object>> strategyPool = new AtomicReference<>();

    /**
     * 初始化策略分发器
     *
     * @param strategyPool 策略池
     */
    public void init(Map<String, Object> strategyPool) {
        Assert.notEmpty(strategyPool, "strategyPool is empty");
        if (!CollectionUtils.isEmpty(strategyPool)) {
            this.strategyPool.set(strategyPool);
        }
    }

    /**
     * 根据操作获取策略
     *
     * @param operation 操作
     * @return {@link Object} 对应的策略
     * @author haojinlong
     * @date 2021/12/29
     */
    public Object getStrategyByOperation(String operation) {
        if (null == strategyPool.get()) {
            return null;
        }

        return strategyPool.get().get(operation);
    }
}
