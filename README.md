# nicelock

## 1.介绍
nicelock：一个注解，即可使用Java的锁；支持本地锁和分布式锁（分布式锁基于Redisson，稳定！）。

使用场景：防重复点击。例如：
1. 防止同一用户在极短时间多次支付同一个订单。（第一次还没操作完毕，就开始了第二次操作）
2. 防止多用户同时注册同一个用户名。

## 2.快速使用

### 2.1 引入依赖
```xml
<dependency>
    <groupId>com.suchtool</groupId>
    <artifactId>nicelock-spring-boot-starter</artifactId>
    <version>{newest-version}</version>
</dependency>
```

### 2.2 配置缓存方式

本组件默认使用本地缓存。

如果要使用redis，需要引入redisson-spring-boot-starter依赖，并配置。

引入依赖：
```
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson-spring-boot-starter</artifactId>
    <version>3.16.6</version>
</dependency>
```

添加Redisson相关配置，比如：
```
spring:
  redis:
    host: 127.0.0.1
    port: 6379
    password: 222333
```

### 2.3使用

#### 2.3.1 示例

下边代码可以保证一个用户同时多次编辑同一个订单时，只有一个是成功的。

例1：对入参字段加锁

```
@NiceLock(keys = {"#user.id", "#orderNo"})
public String editOrder(User user, String orderNo) {
    System.out.println("修改订单");
    return "success";
}
```

例2：对静态方法字段加锁

如果字段是静态获取的，可以这样写：

```
@NiceLock(keys = {"T(com.example.common.util.user.util.UserUtil).currentUser.getId()", "#orderNo"})
public String editOrder(String orderNo) {
    System.out.println("修改订单");
    return "success";
}

```

#### 2.3.2 @NiceLock字段含义
| 字段名  | 含义  | 默认值  |
| ------------ | ------------ | ------------ |
| keys | 锁的key。（支持SpEL）  | 空数组 |
| acquireTimeout | 获取锁的超时时间 | 0（即：若有其他的正在执行，则立即返回） |
| message | 错误提示 | 正在处理，请稍候重试  |
| exception | 报错时抛出的异常 | NiceLockLockFailException  |

## 3.详细配置

### 1.执行顺序
默认情况下，本组件在@Transactional之前执行。你可以指定本组件的执行顺序，在SpringBoot的启动类上加如下注解即可：
```
@EnableNiceLock(order = 1)
```
比如：
```
package com.knife.example;

import com.suchtool.nicelock.annotation.EnableNiceLock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableNiceLock(order = 1)
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
```

### 2.配置大全
支持yml等配置方式。

| 配置                             | 描述               | 默认值       |
|--------------------------------|------------------|-----------|
| suchtool.nicelock.storage-type | 存储方式。可选值：local、redis | local（本地） |
| suchtool.nicelock.key-prefix   | 缓存的key的前缀        | nicelock  |

