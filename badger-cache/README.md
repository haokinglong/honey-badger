# badger-Cache

---

## 简介

该核心包自定义了`Spring Cache`的核心实现,提供了更为灵活且丰富的缓存能力

### 主要提供的能力有:

* 1.缓存配置信息更新便捷
* 2.远程缓存服务失联时,支持自定义的应对策略:快速失败/跳过异常

## 配置参考

```properties
#spring cache配置
honey.badger.cache.prefix=badger
honey.badger.cache.cache-names[0].name=user
honey.badger.cache.cache-names[0].ttl=100000
honey.badger.cache.cache-names[0].writeException=true
honey.badger.cache.cache-names[1].name=account
#ttl超期时间不填,缺省值为60000
honey.badger.cache.cache-names[1].ttl=100000
#writeException可不填,缺省值为false
honey.badger.cache.cache-names[1].writeException=false
#spring cache的redis配置,以下是必填项
honey.badger.cache.managers.redis.base-config.host=127.0.0.1
honey.badger.cache.managers.redis.base-config.port=6379
honey.badger.cache.managers.redis.base-config.timeout=200
honey.badger.cache.managers.redis.base-config.password=123456
honey.badger.cache.managers.redis.base-config.min-idle=1
honey.badger.cache.managers.redis.base-config.max-idle=10
honey.badger.cache.managers.redis.base-config.max-active=20
honey.badger.cache.managers.redis.base-config.max-wait=500
```

## 使用

添加包依赖,按照`Spring Cache`规范的方式使用即可

```xml

<dependency>
    <groupId>com.honey</groupId>
    <artifactId>badger-cache</artifactId>
    <version>${badger.version}</version>
</dependency>
```

> 使用Demo,更多使用请参数spring cache的操作文档即可

**注意项:**
* 下例中`cacheNames`为配置文件中的`honey.badger.cache.cache-names[1].name=account`配置项
* 当远程服务`redis`失联后,服务会根据`honey.badger.cache.cache-names[1].writeException=false`配置项决定对该请求是否抛异常,该配置缺省值为`false`,也就是降级操作,直接跳过异常,优先保证服务可用性,当配置为`true`时,则会抛出异常,快速失败;待服务恢复连接时会自动恢复正常

```java
/**
 * 描述:
 *
 * @author haojinlong
 * @date 2021/12/1
 */
@Api(tags = "缓存验证")
@RequestMapping("/caches")
@RestController
public class SpringCacheController {

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据用户id获取用户信息
     *
     * @param userId 用户id 
     * @return {@link null}
     * @author haojinlong
     * @date 2021/12/8
     */
    @Cacheable(key = "#userId", cacheNames = "account")
    @GetMapping("/user-info")
    public User getUser(String userId) {
        User user = new User();
        user.setName("23456");
        user.setUserId(userId);
        user.setAccount("haojinlong");
        user.setStatus(1);

        return user;
    }
}
```