package com.honey.badger.swagger.config;

import com.honey.badger.swagger.suport.SwaggerProperties;
import com.honey.badger.swagger.suport.SwaggerSwitchCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Slf4j
@Configuration
@EnableConfigurationProperties(SwaggerProperties.class)
@Conditional(SwaggerSwitchCondition.class)
@Import({SwaggerConfig.class})
public class SwaggerAutoConfigration {

}
