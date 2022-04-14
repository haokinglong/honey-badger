# badger-CORE

**简介:**

`badger`后端基础核心包可以提供以下功能：

* 核心通用注解`@Raw`: 接口使用该注解后可以使得`badger-web-core`中的全局响应捕获`BadgerResponseHandler`针对有该注解的返回体不做封装,简单理解就是原生返回.结合`badger-feign`组件微服务内部使用效果更佳

* 提供通用核心常量类`BadgerConstant`: 主要提供了一些基于`PASS`的全局通用参数`X-Transaction-Id`,`X-Api-Key`,还有最新约定的请求成功的业务响应码等...

* 提供通用枚举包`enums`:

    * `base`: 主要提供了`BaseEnum`核心枚举接口类,可以规范全局枚举使用

* `BadgerEnumUtil`枚举工具类: 主要提供了便捷的枚举断言功能,能被他所断言的枚举都必须是实现了`BaseEnum`的枚举类.

* 提供特定业务异常类

## 使用

### 在`pom`中引入该依赖

```xml

<dependency>
    <groupId>com.honey</groupId>
    <artifactId>badger-core</artifactId>
    <version>${badger.version}</version>
</dependency>
```

### `@Raw`注解的使用

> 方法上需要添加`@Raw`注解,表示该方法不需要被`BaseResponse`统一封装,需要原生返回,比如下图返回的对象就是`User`

```java
/**
 * 描述：类 {@code User} 用户表API
 *
 * @author haojinlong
 * @date 2021-09-23
 */
@Api(tags = "内部:用户表 API")
@Slf4j
@RestController
@RequestMapping("/users/_private")
public class InternalUserController {

    @Autowired
    private UserService userService;

    /**
     * 根据id获取用户信息
     *
     * @param userId 用户id
     * @return {@link User}
     * @author haojinlong
     * @date 2021-09-23
     */
    @Raw
    @ApiOperation("根据id获取用户信息")
    @GetMapping("/{userId}")
    public User getUser(@PathVariable String userId) {
        return userService.getUserById(userId);
    }
}
```

### `BaseEnum`通用枚举接口的使用

为方便扩展,该枚举接口使用的是泛型,故可以支持各类枚举场景

> a. 整型枚举

```java
/**
 * 人员角色枚举
 *
 * @author haojinlong
 * @date 2021-09-23
 */
@Getter
@AllArgsConstructor
public enum AccountRoleEnum implements BaseEnum<Integer> {

    /**
     * 非集团人员
     */
    NOT_COMPANY(0, "非集团人员"),
    /**
     * 集团人员
     */
    COMPANY(1, "集团人员");

    /**
     * 枚举码
     */
    private final Integer code;
    /**
     * 枚举描述
     */
    private final String value;
}
```

> b. 字符型枚举

```java
/**
 * 业务异常枚举
 *
 * @author haojinlong
 * @date 2021-09-23
 */
@Getter
@AllArgsConstructor
public enum ErrorEnum implements BaseEnum<String> {

    /**
     * 事件不存在
     */
    AS6000("AS6000", "事件不存在");

    /**
     * 枚举码
     */
    private final String code;
    /**
     * 枚举描述
     */
    private final String value;
}
```

### `BadgerEnumUtil`工具类的使用

```java
/**
 * 描述：类 {@code User} 用户表API
 *
 * @author haojinlong
 * @date 2021-09-23
 */
@Api(tags = "内部:用户表 API")
@Slf4j
@RestController
@RequestMapping("/users/_private")
public class InternalUserController {

    @Autowired
    private UserService userService;

    /**
     * 枚举工具使用
     *
     * @author haojinlong
     * @date 2021/10/8
     */
    @ApiOperation("枚举工具使用")
    @PostMapping("/update-user-status/{status}")
    public void enumTest(@PathVariable Integer status) {
        boolean included = BadgerEnumUtil.isIncluded(status, YesOrNoEnum.class);
        //status参数的值不在枚举内,则视为非法请求,BadgerAssert会抛出业务异常,终止请求
        BadgerAssert.isTrue(included, "非法参数");

        //以下是正常的业务
    }
}
```

被`BadgerAssert`拒绝的非法请求,`http status`为`200`,业务异常码为`GP4000`

```json
{
  "code": "GP4000",
  "message": "非法参数",
  "tracestack": "27973d99-0521-4fd9-b0f2-7119c4b41c51"
}
```