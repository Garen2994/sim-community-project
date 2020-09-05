# Part 4 **Redis** 一站式高性能存储方案

## **Redis 实现点赞**

##### 点赞

- 支持对帖子、评论点赞。
- 第一次点赞，第二次取消点赞

##### 首页点赞数量

- 统计帖子的点赞数量

- 详情页点赞数量
  
  - 统计点赞数量
  - 显示点赞状态
  
  表现层：异步请求 不刷新界面



## **我收到的赞**

##### 重构点赞功能

- 以用户为key，记录点赞数量
- increment(key),decrement(key)

##### 开发个人主页

- 以用户为key，查询点赞数量

<img src="https://img.garenhou.com/%E6%88%AA%E5%B1%8F2020-08-28%20%E4%B8%8B%E5%8D%882.29.39.png" style="zoom:87%;" />

## **关注、取消关注**

##### 需求

- 开发关注、取消关注功能
- 统计用户的关注数、粉丝数

##### 关键

- 若A关注了B，则A是B的Follower，B是A的Followee。
- 关注的目标可以是用户、帖子、题目等，在实现将这些目标抽象为实体。
- Redis存储（先确定key）
  - ==Redis 编程式事务==：multi()开启事务，加入到任务队列后，return exec()统一执行。



## **关注列表、粉丝列表查看**

##### 业务层

- 查询某个用户关注的人，支持**分页**
- 查询某个用户的粉丝，支持分页

##### 表现层

- 处理“查询关注的人”、“查询粉丝”请求
- 编写“查询关注的人”、“查询粉丝”模板
- （按时间倒序，Redis存的zset中score倒序）

<img src="/Users/garen_hou/Desktop/drawMyimage/截屏2020-08-29 上午1.09.22.png" style="zoom:87%;" />

## **Redis优化登陆模块**

##### 使用Redis存储验证码

- 验证码需要频繁的访问与刷新，对性能要求较高
- 验证码不需永久保存，通常在很短的时间后就会失效
- ==分布式部署时，存在Session共享的问题==，都存到Redis中，所有服务器可以访问

##### 使用Redis存储登陆凭证

- 处理每次请求时，都要查询用户的登陆凭证，访问的频率非常高

##### 使用Redis==缓存用户信息==

- 处理每次请求时，都要根据凭证查询用户信息，访问的频率非常高

  - findbyUserId 查询时，==优先==尝试从缓存中取值
    - 取到就用
    - 取不到，就初始化缓存数据

  - 数据更新的话，一般是直接删除缓存

优化验证码（60s失效）：

<img src="/Users/garen_hou/Desktop/drawMyimage/截屏2020-08-29 下午3.09.31.png" style="zoom:100%;" />

![](/Users/garen_hou/Desktop/drawMyimage/截屏2020-08-29 下午3.10.45.png)

优化登陆凭证的存储

<img src="/Users/garen_hou/Desktop/drawMyimage/截屏2020-08-29 下午3.40.30.png" style="zoom:100%;" />

缓存用户数据(userxxx 走了缓存)

<img src="/Users/garen_hou/Desktop/drawMyimage/截屏2020-08-29 下午4.34.51.png" style="zoom:80%;" />

