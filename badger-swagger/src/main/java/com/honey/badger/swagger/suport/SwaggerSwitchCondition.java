package com.honey.badger.swagger.suport;


import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

/**
 * 描述:{@code swagger}开关条件
 * <p>该组件的{@code swagger}文档只在非正式环境,即非{@code prod}环境;
 * 或配置文件中配置{@code honey.badger.swagger.enabled}为{@code true}时生效
 *
 * @author haojinlong
 * @date 2021/2/13
 */
public class SwaggerSwitchCondition implements Condition {

    private static final String DEFAULT_PROD_ENV = "prod";
    private static final String ENV = "env";
    private static final String TRUE = "true";
    private static final String DEFAULT_CONFIG_SWITCH = "honey.badger.swagger.enabled";

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {

        String curEnv = conditionContext.getEnvironment().getProperty(ENV);
        String switchFlag = conditionContext.getEnvironment().getProperty(DEFAULT_CONFIG_SWITCH);

        if (!StringUtils.isEmpty(curEnv) && DEFAULT_PROD_ENV.equals(curEnv)) {
            return false;
        }
        if (!StringUtils.isEmpty(switchFlag) && TRUE.equals(switchFlag)) {
            return true;
        }
        if (!StringUtils.isEmpty(curEnv) && !DEFAULT_PROD_ENV.equals(curEnv)) {
            return true;
        }

        return false;
    }
}
