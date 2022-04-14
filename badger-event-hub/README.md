# Badger event hub 公共包

**核心功能**
- 启动类无注解，无须指定开启event hub功能
- 事件发送使用注解方式，无代码侵入。同时也支持无AOP的方式，增加灵活度
- 事件接收listener由Spring Boot自动扫描注册，无须指定，写法和Rabbit/Kafka相似

## 快速入门
### 引入依赖
```xml
<dependency>
    <groupId>com.honey</groupId>
    <artifactId>badger-eventhub</artifactId>
    <version>${badger.version}</version>
</dependency>
```

### 添加配置
```properties
# event hub rabbit 配置
honey.badger.eventhub.enabled = true    # 默认为false，不开启
honey.badger.eventhub.rabbitmq.username = xxxx
honey.badger.eventhub.rabbitmq.password = xxxx
honey.badger.eventhub.rabbitmq.host = xxx.xxx.xxx.xxx
```

### 事件发送
方法上添加注解：
```java
@EventSender(source = "group", type = "group-deleted", argNames = {"groupId"})
@Override
public void send(String userId, String groupId) { }
```
- source: 事件源  
- type: 事件类型  
- argNumber: 该方法参数的名称。用来指定哪个参数作为数据发送出去。  
**说明**： 数据会被自动转为JSON格式包装进EventData POJO中
  
### 事件接收
```java
@EventListener
public class EventListener {

    @EventHandler(source = "group", type = "group-deleted", sub = "xxxx")
    public void receive(EventData data) {
        log.info("received, data ={}", data.getData());
    }
}
```
**说明**：
- 类上添加 @EventListener 注解，起到 @Component 的效果
- 方法上添加 @EventHandler 注解，方法参数类型需要咨询事件发布方
- source表示事件源，type表示事件类型，sub表示订阅方