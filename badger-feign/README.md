# badger-FEIGN

---
## 简介

基于`SpringCloud`原生`feign`封装了该包,该包主要提供一下功能:

* 与`nacos`联动,可自动获取服务注册集群,进行路由
* 针对`Feign`的请求进行拦截,添加`traceId`,用来做全链路追踪,便于分布式项目中排错使用
* 针对`Feign`调用后的异常返回统一进行拦截,封装,统一打印规范日志,便于分布式项目中排错使用

## 使用:

添加包依赖即可使用
```xml
        <dependency>
            <groupId>com.honey</groupId>
            <artifactId>badger-feign</artifactId>
            <version>${badger-feign.version}</version>
        </dependency>
```


application.properties 中你的项目进行描述.

```properties
# 让feign使用okhttp做请求；而不是默认的urlconnection
feign.okhttp.enabled = true
feign.client.config.defualt.read-timeout=10000
feign.client.config.defualt.connect-timeout=10000
# feign的最大连接数
feign.httpclient.max-connections=200
# feign单个路径的最大连接数
feign.httpclient.max-connections-per-route=50
```

最大连接数和单个路径的最大连接数需要每个项目根据自身的压测结果进行调整,以达到最佳性能