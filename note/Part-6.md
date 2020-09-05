# Elasticsearch 分布式搜索引擎

## Elasticsearch入门

#### Elasticsearch 简介

- 一个分布式的、Restful风格的搜索引擎

  （Restful是一种设计风格，对请求标准的描述）

- 支持对各种类型的数据的检索
- 搜索速度快，可以提供实时的搜索服务
- 便于水平扩展（集群），每秒可以处理PB级海量数据

> 要使用ES来搜索，前提是将数据在ES中再转存一份，所以ES也可以看做一个特殊的数据库

#### Elasticsearch术语

- 索引、类型、文档、字段
  - 索引对应表，文档对应行，字段对应列，类型逐渐废弃
- 集群、节点、分片、副本
  - 集群中的每个服务器为节点，分片对索引的进一步划分（提高并发能力），副本是对分片的备份（提高可用性）

## Spring整合Elasticsearch

#### 引入依赖

- Spring-boot-starter-data-elasticsearch

#### 配置Elasticsearch

- cluster-name、cluster-nodes

#### Spring Data Elasticsearch

- ElasticsearchTemplate

- ElasticsearchRepository（接口,底层依赖ElasticsearchTemplate，比较方便，但是有时不得不用Elasticsearch）

  （搜帖子）

  - 配合Postman使用
  - ik中文分词器的使用 `ik_max_word` --细化分词     `ik_smart`--粗化分词

  插入：`discussPostRepository.save(discussPostMapper.selectDiscussPostById(241));`

  ​          `discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(101, 0, 100));`

  搜索 : `GET localhost:9200/dicusspost/_search`

  结果：

![](/Users/garen_hou/Desktop/drawMyimage/截屏2020-09-01 上午9.49.42.png)

![](/Users/garen_hou/Desktop/drawMyimage/截屏2020-09-01 下午2.54.15.png)

## 开发社区搜索功能

#### 搜索业务（业务层）

- 将帖子保存至Elasticsearch服务器
- 从Elasticsearch服务器删除帖子
- 从Elasticsearch服务器搜索帖子

#### 发布事件（表现层）

- 发布帖子时，将帖子异步的提交到Elasticsearch服务器
- 增加评论时,将帖子异步提交到Elasticsearch服务器
- 在消费组件中增加一个方法，消费帖子发布事件

#### 显示结果

- 在控制器中处理搜索请求，在HTML上显示搜索结果

![](/Users/garen_hou/Desktop/drawMyimage/截屏2020-09-01 下午4.26.58.png)