# badger 后端核心组件

**1.0.1-SNAPSHOT 版本介绍:**

**简介:**

本项目可以提供以下依赖包：

* `badger-core`
* `badger-cache`
* `badger-design`
* `badger-feign`
* `badger-web-core`
* `badger-web-starter`
* `badger-swagger`
* `badger-error-record`
* `badger-event-hub`
* `badger-kafka`
* `badger-rate-limiter`

## 各包的用途介绍

### 1.1 `badger-core`

该包提供了一些后端公共基础核心依赖,比如:枚举,常量,通用业务异常封装,核心通用注解等

引用使用:

```xml

<dependency>
    <groupId>com.honey</groupId>
    <artifactId>badger-core</artifactId>
    <version>${badger.version}</version>
</dependency>
```

### 1.2 `badger-web-core`

该包主要是针对`web`服务请求做一些封装

* 增加请求拦截,基于`MDC`,从请求头中获取相应的参数供日志统一打印
* 增加了通过`AOP`将响应体统一封装返回
* 新增了针对`ahas`埋点限流异常的捕获,以特定业务异常码`GP5429`返回,`http status`为`429`

```json
{
  "code": "GP5429",
  "message": "too many request"
}  
```

* 联合`badger-feign`包,针对微服务集群内部调用(路径中包含`/_private/`的并且同时添加注解`@Raw`都默认为内部调用),`http status`异常码改为`206`返回,同时响应体默认不做任何处理,原生返回.不用担心的是,当请求真正返回调用方时,会以正常的业务异常返回,如此操作只是为了降低微服务内部调用成本

引用使用:

```xml

<dependency>
    <groupId>com.honey</groupId>
    <artifactId>badger-web-core</artifactId>
    <version>${badger.version}</version>
</dependency>
```

### 1.3 `badger-feign`

基于`SpringCloud`原生`feign`封装了该包,该包主要提供一下功能:

* 与`nacos`联动,可自动获取服务注册集群,进行路由
* 针对`Feign`的请求进行拦截,添加`traceId`,用来做全链路追踪,便于分布式项目中排错使用
* 针对`Feign`调用后的异常返回统一进行拦截,封装,统一打印规范日志,便于分布式项目中排错使用

引用使用:

```xml

<dependency>
    <groupId>com.honey</groupId>
    <artifactId>badger-feign</artifactId>
    <version>${badger.version}</version>
</dependency>
```

### 1.4 `badger-swagger`

该`Swagger`组件主要是基于[knife4j](https://doc.xiaominfo.com/) 进行扩展而来,拥有了更为绚丽的UI以及丰富的便捷操作,同时支持 用户名+密码 的鉴权

引用使用:

```xml

<dependency>
    <groupId>com.honey</groupId>
    <artifactId>badger-swagger</artifactId>
    <version>${badger.version}</version>
</dependency>
```

### 1.5 `badger-design`

该设计模式公共包意在使开发人员更加便利的使用经典设计模式,并降低使用成本,同时也可统一使用规范,目前仅提供了常用的策略模式

引用使用:

```xml

<dependency>
    <groupId>com.honey</groupId>
    <artifactId>badger-design</artifactId>
    <version>${badger.version}</version>
</dependency>
```

### 1.6 `badger-event-hub`

**核心功能**
- 启动类无注解，无须指定开启event hub功能
- 事件发送使用注解方式，无代码侵入。同时也支持无AOP的方式，增加灵活度
- 事件接收listener由Spring Boot自动扫描注册，无须指定，写法和Rabbit/Kafka相似

引用使用:

```xml

<dependency>
    <groupId>com.honey</groupId>
    <artifactId>badger-event-hub</artifactId>
    <version>${badger.version}</version>
</dependency>
```

### 1.7 `badger-kafka`

`Kafka`核心包,目前主要提供了`producer`的多实例配置,消费者的还未完成

引用使用:

```xml

<dependency>
    <groupId>com.honey</groupId>
    <artifactId>badger-kafka</artifactId>
    <version>${badger.version}</version>
</dependency>
```

### 1.8 `badger-error-record`

该异常记录公共包主要是为了使开发人员当遇到业务出现异常错误时兜底记录当时的数据信息,以便事后请求重放或者数据恢复
此公共包提供两种方式供选择
1. 基于aop的注解方式
   @BadgerNeedErrorRecord
2. 手动编程方式
   errorRecordHandler.produceErrorRecordMsg(new ErrorRecordHelper<>().getErrorRecord())

引用使用:

```xml

<dependency>
    <groupId>com.honey</groupId>
    <artifactId>badger-error-record</artifactId>
    <version>${badger.version}</version>
</dependency>
```

### 1.9 `badger-cache`

该核心包自定义了`Spring Cache`的核心实现,提供了更为灵活且丰富的缓存能力

### 主要提供的能力有:

* 1.缓存配置信息更新便捷
* 2.远程缓存服务失联时,支持自定义的应对策略:快速失败/跳过异常

引用使用:

```xml

<dependency>
    <groupId>com.honey</groupId>
    <artifactId>badger-cache</artifactId>
    <version>${badger.version}</version>
</dependency>
```

### 1.10 `badger-web-starter`

该包主要是针对`web`服务的`POM`引用做了简单的聚合,便于精简`POM`依赖项目

引用使用:

```xml

<dependency>
    <groupId>com.honey</groupId>
    <artifactId>badger-web-starter</artifactId>
    <version>${badger.version}</version>
</dependency>
```

### 1.11 `badger-rate-limiter`

该包主要提供了基于`redis lua`脚本实现的滑动窗口限流

引用使用:

```xml
<dependency>
   <groupId>com.honey</groupId>
   <artifactId>badger-rate-limiter</artifactId>
   <version>${badger.version}</version>
</dependency>
```

