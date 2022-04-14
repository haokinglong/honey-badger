# Badger 异常记录公共包

**简介:**

该异常记录公共包主要是为了使开发人员当遇到业务出现异常错误时兜底记录当时的数据信息,以便事后请求重放或者数据恢复
此公共包提供两种方式供选择
1. 基于aop的注解方式
   @BadgerNeedErrorRecord
2. 手动编程方式
   errorRecordHandler.produceErrorRecordMsg(new ErrorRecordHelper<>().getErrorRecord())

## 如何使用?

### 1. 在`pom`中引入该依赖

        <dependency>
            <groupId>com.honey</groupId>
            <artifactId>badger-error-record</artifactId>
            <version>${badger.version}</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>

### 2. 项目中依赖rabbitmq及数据库

### 3. 使用demo

#### 3.1 基于注解式

> 当doBusiness方法执行时如果抛出了`Exception`,`BadgerNeedErrorRecord`注解会将请求的数据自动存储起来

```java
import BadgerNeedErrorRecord;

@Service
public class DemoServiceImpl implements DemoService {

   @Override
   @BadgerNeedErrorRecord(serviceCode = "服务编号", actionName = "xx业务处理", needAopRecord = true)
   public void doBusiness(Object param) {
      System.out.print(1 / 0);
   }
}
```

#### 3.2 手动编程式

> 在需要异常记录的方法上添加`BadgerNeedErrorRecord` 注解,并捕获异常,当异常发生时`errorRecordHandler.produceErrorRecordMsg`方法手动记录,
`ErrorRecordHelper`帮助类提供了重载的`getErrorRecord`方法去生成`ErrorRecord`对象.

```java
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DemoServiceImpl extends DemoService {

    @Autowired
    private ErrorRecordHandler errorRecordHandler;
    
    /**
     *  xx业务处理
     *
     * @param paramDto 业务参数对象
     * @author hanlining
     * @date 2021/12/7
     */
    @Override
    @BadgerNeedErrorRecord(serviceCode = "服务编号", actionName = "xx业务处理")
    public void doBusiness(XXXDto paramDto) {
        try {
            System.out.print(1 / 0);
        } catch (Exception e) {
            log.warn("doBusiness error, param ={}", param, e);
            // 错误记录默认最低级别
            errorRecordHandler.produceErrorRecordMsg(new ErrorRecordHelper<>(getClass(), "doBusiness").getErrorRecord(paramDto, paramDto.getTraceId()));
            // 指定错误记录的级别
            // errorRecordHandler.produceErrorRecordMsg(new ErrorRecordHelper<>(getClass(), "doBusiness").getErrorRecord(paramDto, ErrorRecordLevelEnum.HIGHEST.getCode(), paramDto.getTraceId()));
        }
    }
}
```

