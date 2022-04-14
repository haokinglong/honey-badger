package com.honey.badger.design.strategy.initializer;

import com.honey.badger.design.strategy.annotation.BadgerStrategy;
import com.honey.badger.design.strategy.annotation.BadgerStrategyDispatcher;
import com.honey.badger.design.strategy.dispatcher.BadgerStrategyParentDispatcher;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 描述:策略类初始化器
 * <p>
 * {@code Badger}策略类统一初始化器
 * </p>
 *
 * @author haojinlong
 * @date 2021/9/18
 */
@Component
public class BadgerStrategyInitializer implements InitializingBean {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() {
        Map<String, Object> badgerStrategyDispatcherBeans = applicationContext.getBeansWithAnnotation(BadgerStrategyDispatcher.class);
        Map<String, Object> badgerStrategyBeans = applicationContext.getBeansWithAnnotation(BadgerStrategy.class);

        Set<Entry<String, Object>> entries = badgerStrategyDispatcherBeans.entrySet();
        for (Entry<String, Object> next : entries) {
            Class<?> aClass = next.getValue().getClass();
            BadgerStrategyDispatcher dispatcherAnnotation = aClass.getAnnotation(BadgerStrategyDispatcher.class);
            String strategyId = dispatcherAnnotation.strategyId();

            Map<String, Object> strategyPool = getStrategyPoolById(strategyId, badgerStrategyBeans);

            ((BadgerStrategyParentDispatcher) next.getValue()).init(strategyPool);
        }
    }

    /**
     * 根据策略id获取所属所有策略模式
     *
     * @param strategyId          策略id
     * @param badgerStrategyBeans 策略实例
     * @return {@link Map}
     * @author haojinlong
     * @date 2021/9/18
     */
    private Map<String, Object> getStrategyPoolById(String strategyId, Map<String, Object> badgerStrategyBeans) {
        Map<String, Object> strategyPool = new ConcurrentHashMap<>(8);

        Set<Entry<String, Object>> entries = badgerStrategyBeans.entrySet();
        for (Entry<String, Object> next : entries) {
            Class<?> aClass = next.getValue().getClass();
            BadgerStrategy strategyAnnotation = aClass.getAnnotation(BadgerStrategy.class);

            if (strategyId.equals(strategyAnnotation.strategyId())) {
                strategyPool.put(strategyAnnotation.operation(), next.getValue());
            }
        }

        return strategyPool;
    }
}
