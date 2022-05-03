# badger-rate-limiter

---

## 简介

该包主要提供了基于`redis lua`脚本实现的滑动窗口限流

* 特点:
  > 基于`spring listener`机制,比较优雅的实现了配置的热更新,不用重启服务即可更新限流策略. 同时提供了一个不用配置直接自行传限流配置的简易接口(开箱即用).有相关限流业务需求时可以考虑使用该包

## 使用:

添加包依赖即可使用

```xml

<dependency>
    <groupId>com.honey</groupId>
    <artifactId>badger-rate-limiter</artifactId>
    <version>${badger.version}</version>
</dependency>
```

`application.properties`配置示例:

```properties
honey.badger.rate-limit.strategies.limit1.ttl=100000
honey.badger.rate-limit.strategies.limit1.windowTime=100000
honey.badger.rate-limit.strategies.limit1.limitCount=10
honey.badger.rate-limit.strategies.limit1.limitKey=account
```

使用时,直接调用提供的静态方法即可

```java
public class DemoController {

    /**
     * 基于配置的限流接口示例
     *
     * @return {@link Boolean}
     * @author haojinlong
     * @date 2022/5/3
     */
    @ApiOperation("基于配置的限流接口示例")
    @GetMapping("/demo")
    public Boolean demo(@RequestParam String limitKey) {
        return RedisRateLimitUtil.isLimit4Config("limit1", limitKey);
    }

    /**
     * 非基于配置的限流接口示例
     *
     * @return {@link Boolean}
     * @author haojinlong
     * @date 2022/5/3
     */
    @ApiOperation("非基于配置的限流接口示例")
    @GetMapping("/demo")
    public Boolean demo(@RequestParam String limitKey) {
        return RedisRateLimitUtil.isLimit(limitKey, 1000, 1000, 3);
    }
}
```

## 注意事项:

* 该包是基于`Redis`实现的,所以使用该包前项目必须已经实例化过`Redis`
* 该包配置更新的核心`抓手`依靠的是`Spring`提供的`listener`交互逻辑,较为优雅的实现了配置热更新的需求,使得底层配置更新不用重启服务,如果有二次开发的需求,请先了解该方面的知识,便于更为精准的二次开发
* 考虑到场景的通用性,配置更新的方式写的比较通用,使用方可以通过监听`Apollo`/`Nacos`的配置变更然后 调用`ConfigurableApplicationContext`的`publishEvent`方法,即可以更新限流策略;或者可以通过运维接口将更新的配置通过上文提到的方式更新,亦可达到相同的目的

```java
    public class DemoListener {

    @Autowired
    private ConfigurableApplicationContext context;

    @ApiOperation("限流配置的监听器")
    @PutMapping("/rate-limit")
    public void updateLimit(String value) {
        Map<String, Object> map = new HashMap<>();
        map.put("honey.badger.rate-limit.strategies.limit1.limitCount", value);
        context.publishEvent(new RateLimitEvent(map));
    }
}
```