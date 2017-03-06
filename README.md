# zbus-spring

一个用于支持zbus的spring插件, 用于简化zbus的配置, 支持client端和server端的配置.

## 配置demo

1. 为spring xml配置文件添加sxd schema
```xml
xmlns:zbus="http://www.izhuan365.com/schema/zbus"
xsi:schemaLocation="http://www.izhuan365.com/schema/zbus http://www.izhuan365.com/schema/zbus/1.0.xsd"
``` 
2. Server端
```xml

    <zbus:broker id="broker" serverAddr="${serverAddr}"/>

    <!-- 1、消息队列：消费者 -->
    <bean id="commonHandler" class="com.jingcai.apps.zbus.spring.mq.common.CommonHandler"/>
    <zbus:consume broker="broker" mq="DemoMQ" handler="commonHandler" disabled="false"/>

    <!-- 2、发布订阅：订阅者 -->
    <bean id="subLoginService" class="com.jingcai.apps.zbus.demo.server.subservice.SubLoginService"/>
    <zbus:subscribe broker="broker" mq="PubSubMQ" topic="sys-login" handler="subLoginService"/>
    <zbus:subscribe broker="broker" mq="PubSubMQ" topic="sys-regist" class="com.jingcai.apps.zbus.demo.server.subservice.SubRegistService"/>

    <!-- 3、远程RPC调用 -->
    <zbus:interfaces broker="broker" mq="DemoRpc">
        <bean class="com.jingcai.apps.zbus.demo.server.rpcservice.DemoServiceImpl"/>
    </zbus:interfaces>
```
3. Client端
```xml
    <zbus:broker id="broker" serverAddr="${serverAddr}"/>

    <!-- 1、消息队列：生产者 -->
    <zbus:produce  id="mqProducer" broker="broker" mq="DemoMQ"/>

    <!-- 2、发布订阅：发布者 -->
    <zbus:publish broker="broker" mq="PubSubMQ"/>

    <!-- 3、远程rpc -->
    <zbus:reference id="demoService" broker="broker" mq="DemoRpc" interface="com.jingcai.apps.zbus.demo.sdk.rpcservice.DemoService"/>
    <zbus:reference id="demoService2" broker="broker" mq="DemoRpc" interface="com.jingcai.apps.zbus.demo.sdk.rpcservice.DemoService"/>
```
