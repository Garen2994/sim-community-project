# sim-community-project
<p align="left">
 <img src="https://img.shields.io/badge/Language-Java-blue" alt="Java" style="vertical-align:top; margin:4px">
 <img src="https://img.shields.io/badge/Framework-Spring-9cf" alt="Spring" style="vertical-align:top; margin:4px">
   <img src="https://img.shields.io/badge/Database-MySQL-orange" alt="MYSQL" style="vertical-align:top; margin:4px">
 <img src="https://img.shields.io/badge/Author-Garen_Hou-green" alt="me" style="vertical-align:top; margin:4px">
</p>

本项目是一个基于SpringBoot的社区平台，实现了仿nowcoder社区的功能。实现了邮箱注册、验证码登录、发帖、评论、私信、点赞、关注、统计网站访问次数等功能，数据库使用MySQL、Redis。另外，使用Kafka构建系统通知，使用Elasticsearch构建全文搜索功能。同时实现生成pdf、长图文件并上传云服务器，并采用Spring Security进行权限控制、使用Quartz实现多线程轮询等功能，最后采用JMeter工具对项目进行了压力测试。最终将项目部署到了Aliyun ECS服务器上（ubuntu 18.04.3）。

完成项目后，进行了详细的总结以及笔记整理，方便日后回顾。

#### 参考笔记：

[1.初识Spring Boot，开发社区首页](./note/Part-1.md) 

[2.Spring Boot实践，开发社区登录模块](./note/Part-2.md)

[3.Spring Boot实践，开发社区核心功能](./note/Part-3.md) 

[4.Redis，一站式高性能存储方案](./note/Part-4.md) 

[5.Kafka，构建TB级异步消息系统](./note/Part-5.md)

[6.Elasticsearch，分布式搜索引擎](./note/Part-6.md) 

[7.项目进阶，构建安全高效的企业服务](./note/Part-7.md) 

[8.项目发布与总结](./note/Finish-part.md)

#### 本项目的技术与业务框图如下：

![](https://img.garenhou.com/%E6%8A%80%E6%9C%AF%E5%92%8C%E4%B8%9A%E5%8A%A1.png)

* Spring Boot
* **Spring**
* Spring MVC、Spring Mybatis、**Spring Security**
* 权限@会话管理
  * 注册、登录、退出、状态、设置、授权
  * Spring Email、**Interceptor**
* 核心@敏感词、@事务
  * 首页、帖子、评论、私信、异常、日志
  * Advice、**AOP**、**Transaction**
* 性能@数据结构
  * 点赞、关注、统计、缓存
  * **Redis**
* 通知@模式
  * 系统通知
  * Kafka
* 搜索@索引
  * 全文搜索
  * Elasticsearch
* 其他@线程池、@缓存
  * 排行、上传、服务器缓存
  * Quartz、**Caffeine**

#### 项目完整的网站架构如下：

![](https://img.garenhou.com/%E7%BD%91%E7%AB%99%E6%9E%B6%E6%9E%84%E5%9B%BE.png)