#todo 你的建表语句,包含索引
/**
    背景: 存量订单10亿, 日订单增长百万量级。
主查询场景如下:
1. 买家频繁查询我的订单, 高峰期并发100左右。实时性要求高。
2. 卖家频繁查询我的订单, 高峰期并发30左右。允许秒级延迟。
3. 平台客服频繁搜索客诉订单(半年之内订单, 订单尾号，买家姓名搜索)，高峰期并发10左右。允许分钟级延迟。
4. 平台运营进行订单数据分析，如买家订单排行榜, 卖家订单排行榜。
 */
/**
    基础设施选型：
    1.mysql+ShardingSphere分库分表+读写分离。
    2.redis缓存。当然redis也可以做集群，可采用哨兵+主从集群，如果存储容量不足的话，可以采用redis分片集群。
    3.消息队列。可以用来抗高并发流量。可以选用rocketmq。
    4.ElasticSearch。可以采用ES做搜索引擎，提高检索效率。
    5.数据分析可以采用大数据那套解决方案。目前个人会推荐Flink+Doris。
 */

/**
    针对场景一：
      采用mysql+ShardingSphere分库分表+读写分离。分库的分片键选择为user_id，分表的分片键选择为create_time。分库的分片算法为取模，
    分表的分片算法可按照一个月一张表或者每一季度一张表，比如表名为order_202301。因为实时性要求高，为了减少查询时间，可以将最近一小时的
    订单缓存到redis中。
 */
# 买家订单
CREATE TABLE `interview`.`t_order`  (
                                       `id` varchar(100) NOT NULL COMMENT '订单号，主键',
                                       `user_id` varchar(100) NOT NULL COMMENT '购买人',
                                       `buyer_name` varchar(100) DEFAULT NULL COMMENT '买家名字，冗余字段',
                                       `seller_id` varchar(100) DEFAULT NULL COMMENT '卖家id',
                                       `sku_id` varchar(100) NOT NULL COMMENT 'SkuId',
                                       `amount` int NOT NULL COMMENT '购买数量',
                                       `money` decimal(10, 2) NOT NULL COMMENT '购买金额',
                                       `pay_time` datetime NOT NULL COMMENT '购买时间',
                                       `pay_status` varchar(100) NOT NULL COMMENT '支付状态',
                                       `del_flag` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
                                       `create_by` varchar(100) NOT NULL COMMENT '创建人',
                                       `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `update_by` varchar(100) NOT NULL COMMENT '修改人',
                                       `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                       PRIMARY KEY (`id`),
                                       INDEX `idx_user_id_create_time`(`user_id`, `create_time`) COMMENT '用户id和创建时间索引',
                                       INDEX `idx_seller_id_create_time`(`seller_id`, `create_time`) COMMENT '卖家id和创建时间索引'
);

/**
  针对场景二：
    有两种做法：
  做法一：新建一张卖家与买家订单关联的索引表，查卖家订单的时候，根据索引表找到对应的买家id和订单创建时间，因为只有买家id和订单创建时间是分片键，
  根据分片键查数据，不用全库全表扫描。然后根据买家id和订单创建时间，回查买家订单表。
 */
 # 卖家订单索引表
 CREATE TABLE `interview`.`t_seller_order_index`  (
                                        `id` bigint not null auto_increment primary key,
                                       `order_id` varchar(100) NOT NULL COMMENT '订单号，主键',
                                       `user_id` varchar(100) NOT NULL COMMENT '购买人',
                                       `seller_id` varchar(100) NOT NULL COMMENT '卖家id',
                                       `order_create_time` datetime NOT NULL COMMENT '订单创建时间',
                                        INDEX `idx_seller_id_create_time`(`seller_id`, `create_time`) COMMENT '卖家id和创建时间索引'
 );

/**
  做法二：在创建买家订单的时候复制一个卖家订单，数据和买家订单一样，只是分片键为卖家id和订单创建时间。
  在买家订单数据发生变化的时候，将修改的数据也同步到卖家订单中。可采用订阅binlog的方式。技术可以用alibaba的canal。这种做法的好处是不用回查买家订单表。

  因为卖家订单允许秒级延迟，所以卖家订单可以从mysql从库中读取数据。
 */

 /**
    针对场景三：
   创建客诉订单的时候，把订单信息保存到rocketmq等消息队列中，然后订阅该消息队列，将数据保存到ES中。ES可以支持平台客服的搜索功能，
   ES保存半年的订单数据即可，可以用定时任务的方式清理ES里半年前的订单数据。
  */

/**
  针对场景四：
  平台运营进行订单数据分析，可以采用大数据那套解决方案。目前个人会推荐Flink+Doris。对于买家订单排行榜和卖家订单排行榜也可以结合redis的zset处理。
 */

/**
  未来展望：
  1.考虑冷热数据分离。将冷数据存到大数据生态的数据库中，如doris,hbase,clickhouse等。
  2.将ShardingSphere分库分表的方法换成直接使用分布式数据库，如tidb。
 */