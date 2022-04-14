# 设计模式公共包

**简介:**

该设计模式公共包意在使开发人员更加便利的使用经典设计模式,并降低使用成本,同时也可统一使用规范,目前仅提供了常用的策略模式

## 如何使用?

### 1. 在`pom`中引入该依赖

        <dependency>
            <groupId>com.honey</groupId>
            <artifactId>badger-design</artifactId>
            <version>${badger.version}</version>
        </dependency>

### 2. 在项目中建立`strategy`包

### 3. 创建策略接口与分发器类&处理器

#### 3.1 策略接口

> 如上文中的`DemoStrategy`,这是demo,使用方可以根据具体业务场景自行编写接口类

```java
/**
 * 机器人多能力策略
 *
 * @author haojinlong
 * @date 2021/6/10
 */
public interface DemoStrategy {

    /**
     * 多能力操作
     *
     * @param user 用户业务实体
     * @author haojinlong
     * @date 2021/6/10
     */
    void multiAbilityOperation(User user);
}
```

#### 3.2 分发器类

> 如上文中的`BadgerStrategyDemoDispatcher`,分发器类则需要继承本组件提供的`BadgerStrategyParentDispatcher`抽象类,
并添加`@BadgerStrategyDispatcher`注解,注解值`strategyId`即为自定义的策略id,我们会根据该策略id对后续的策略进行分组.

```java
import com.honey.badger.design.strategy.annotation.BadgerStrategyDispatcher;

/**
 * 描述:机器人多能力分发器
 *
 * @author haojinlong
 * @date 2021/6/10
 */
@Component
@BadgerStrategyDispatcher(strategyId = StrategyConstant.DEMO_STRATEGY_ID)
public class DemoBadgerStrategyDemoDispatcher extends BadgerStrategyParentDispatcher {

    /**
     * 分发
     *
     * @param operation 多能力操作
     * @param user      用户:该接口只需要保证{@code operation}能准确传递,其余参数均可根据具体场景自行扩展
     * @author haojinlong
     * @date 2021/6/10
     */
    public void dispatcher(String operation, User user) {
        Assert.hasText(operation, "operation is null");

        DemoStrategy strategy = (DemoStrategy) super.strategyPool.get(operation);
        Assert.notNull(strategy, "strategy is null");

        strategy.multiAbilityOperation(user);
    }
}
```

#### 3.3 处理器

> 在`strategy`包中创建`handler`包,并为该策略模式创建各类处理器,处理器需要实现自定义的策略接口,比如本例中的`DemoStrategy`,并实现策略接口定义的方法.
> 同时为处理器添加自定义的`@BadgerStrategy(strategyId = StrategyConstant.DEMO_STRATEGY_ID, operation = StrategyConstant.DEMO_STRATEGY_OPERATION_ONE)`注解.
> `strategyId`即为上文中`BadgerStrategyDemoDispatcher`中定义的值,需要保持相同.`operation`即为该处理器的唯一标识,不能与其他处理器重复

```java
/**
 * 描述: 发送动态的多能力
 *
 * @author haojinlong
 * @date 2021/6/10
 */
@Service
@BadgerStrategy(strategyId = StrategyConstant.DEMO_ONE_STRATEGY_ID, operation = StrategyConstant.DEMO_ONE_STRATEGY_OPERATION_ONE)
public class DemoOneHandler implements DemoStrategy {

    /**
     * 多能力操作
     *
     * @param user 用户业务实体
     * @author haojinlong
     * @date 2021/6/10
     */
    @Override
    public void multiAbilityOperation(User user) {
        System.out.println("one =====>  " + user);
    }
}
```

### 3. 使用

> 直接引入`StrategyDemoDispatcher`,调用其`dispatcher`接口即可

```java
/**
 * 描述：类 {@code User} 用户表API
 *
 * @author haojinlong
 * @date 2021-09-23
 */
@Api(tags = "客户端:用户表 API")
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private StrategyDemoDispatcher strategyDemoDispatcher;

    /**
     * 策略模式:策略一
     *
     * @param userId 用户id
     * @author haojinlong
     * @date 2021/10/8
     */
    @ApiOperation("策略模式:策略一")
    @GetMapping("/strategy/demo-one/{userId}")
    public void strategyOne(@PathVariable String userId) {
        strategyDemoDispatcher.dispatcher(StrategyConstant.DEMO_STRATEGY_OPERATION_ONE, remoteClientService.getUser(userId));
    }
}
```