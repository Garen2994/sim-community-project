# Part 5 **Kafka** 构建TB级异步消息系统

消息：服务器自动给某用户发系统的消息(通知)--- 系统级消息

## 阻塞队列

#### Blocking Queue

- 解决线程通信的问题
- 阻塞方法：put、take

<img src="/Users/garen_hou/Desktop/drawMyimage/a50f4bfbfbedab647481069a4117e6c578311ea4.jpeg"  />

#### 生产者消费者模式

- 生产者：产生数据的线程
- 消费者：使用数据的线程

#### 实现类

- ArrayBlockingQueue
- LinkedBlockingQueue
- PriorityBlocking、SynchronousQueue、DelayQueue等

**ArrayBlockingQueue实现生产者消费者模式** :apple:

```java
public class BlockingQueueTests {
    public static void main(String[] args) {
        BlockingQueue queue = new ArrayBlockingQueue(10);
        new Thread(new Producer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
    }
}
class Producer implements Runnable{
    private BlockingQueue<Integer> queue;
    
    public Producer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }
    @Override
    public void run() {
        try{
            for (int i = 0; i < 100; i++) {
                Thread.sleep(20);
                queue.put(i);
                System.out.println(Thread.currentThread().getName() + " is Producting 队列有："+ queue.size());
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
}
class Consumer implements Runnable{
    private BlockingQueue<Integer> queue;
    
    public Consumer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }
    @Override
    public void run() {
        try{
            while(true){
                Thread.sleep(new Random().nextInt(1000));
                queue.take();
                System.out.println(Thread.currentThread().getName()+" is consuming 队列有："+ queue.size());
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
}
```

## Kafka入门

#### kafka简介

- Kafka是一个分布式的==流媒体平台==（发展->功能扩充了）
- 应用：消息系统、日志收集、用户行为跟踪、流失处理

#### Kafka特点

- 性能最好的消息队列：高吞吐量、消息持久化、高可靠性(分布式)、高扩展性

#### Kafka术语

- Broker（Kafka的服务器--集群中的每一台服务器）、Zookeeper（独立的，集群管理，Kafka内置）
- Topic、Partition、Offset
- Leader Replica、Follower Replica

> 流平台具有三个关键功能：
>
> 1. **消息队列**：发布和订阅消息流，这个功能类似于消息队列，这也是 Kafka 也被归类为消息队列的原因。
> 2. **容错的持久方式存储记录消息流**： Kafka 会把消息持久化到磁盘，有效避免了消息丢失的风险·。
> 3. **流式处理平台：** 在消息发布的时候进行处理，Kafka 提供了一个完整的流式处理类库。
>
> Kafka 主要有两大应用场景：
>
> 1. **消息队列** ：建立实时流数据管道，以可靠地在系统或应用程序之间获取数据。
> 2. **数据处理** : 构建实时的流数据处理程序来转换或处理数据流。
>
> 关于 Kafka 几个非常重要的概念：
>
> 1. Kafka 将记录流（流数据）存储在 `topic` 中。
> 2. 每个记录由一个键、一个值、一个时间戳组成。

![](/Users/garen_hou/Desktop/drawMyimage/Kafka.png)

#### 命令

###### **启动**

先启动zookeeper  ` ./zookeeper-server-start.sh /usr/local/kafka_2.12-2.5.0/config/zookeeper.properties &`

再启动Kafka `./kafka-server-start.sh /usr/local/kafka_2.12-2.5.0/config/server.properties &  `

二、启动生产者和消费者

1. 首先要创建一个topic

`./bin/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test`

  也可以查看创建的topic

`./bin/kafka-topics --list --zookeeper localhost:2181`

2. 启动生产者

`./bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test`

这时会出现命令行模式，可以直接输入消息数据，回车即可发送出去了

3. 启动消费者

`./bin/kafka-console-consumer --bootstrap-server localhost:9092 --topic test --from-beginning`

消费者服务启动后，当生产者发送了信息后，消费者这边即可收到。

![](/Users/garen_hou/Desktop/drawMyimage/截屏2020-08-30 下午4.43.51.png)

## Spring整合Kafka

#### 引入依赖

- spring-kafka

#### 配置Kafka

- 配置server、consumer

#### 访问Kafka

- 生产者

  - `kafkaTemplate.send(topic,data)`

- 消费者

  - ```java
    - `@KafkaListener(topics = {"test"})`
    - public void handleMessage(ConsumerRecord record)
    ```

![](/Users/garen_hou/Desktop/drawMyimage/截屏2020-08-30 下午5.15.09.png)

## 发送系统通知（Kafka技术和基于事件封装）

#### 触发事件

- 评论后，发布通知
- 点赞后，发布通知
- 关注后，发布通知

#### 处理事件

- 封装事件对象
- 开发事件的生产者
- 开发事件的消费者

![](/Users/garen_hou/Desktop/drawMyimage/kafka-message.png)

<img src="/Users/garen_hou/Desktop/drawMyimage/截屏2020-08-31 下午4.03.19.png" style="zoom:200%;" />

## 显示系统通知

#### 通知列表

- 显示评论、点赞、关注三种类型的通知（显示最后一条）

通知详情

- 分页显示某一类主题所包含的通知

未读消息

- 在页面头部显示所有的未读消息数量
- 首页的未读消息提醒由==拦截器==计算）