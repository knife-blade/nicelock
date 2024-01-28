# nice-lock

#### 介绍
一个注解，即可使用Java的分布式锁。（基于Redisson，稳定！）

## 快速使用

### 1.引入依赖
```xml
<dependency>
    <groupId>com.suchtool</groupId>
    <artifactId>nice-lock-spring-boot-starter</artifactId>
    <version>{newest-version}</version>
</dependency>
```
### 2.使用
```
@NiceLock(keys = {"#user.id", "#orderNo"})
public String createOrder(User user, String orderNo) {
    System.out.println("创建订单");
    return "success";
}
```
