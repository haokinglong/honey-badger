# badger-WEB-STARTER

---

## 简介

该包主要是针对`web`服务的`POM`引用做了简单的聚合

以下是该包引入的聚合依赖

```
<dependencies>
    <dependency>
        <groupId>com.honey</groupId>
        <artifactId>badger-web-core</artifactId>
        <version>${badger.version}</version>
    </dependency>
    <dependency>
        <groupId>com.honey</groupId>
        <artifactId>badger-feign</artifactId>
        <version>${badger.version}</version>
    </dependency>
    <dependency>
        <groupId>com.honey</groupId>
        <artifactId>badger-swagger</artifactId>
        <version>${badger.version}</version>
    </dependency>
    <!--druid-->
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>druid</artifactId>
        <version>${druid.version}</version>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>
</dependencies>
```

## 使用

> 项目中直接引入他即可不用再引入上文中提到的那些依赖项
```
<dependencies>
  <dependency>
    <groupId>com.honey</groupId>
    <artifactId>badger-web-starter</artifactId>
    <version>${badger.version}</version>
  </dependency>
</dependencies>
```

