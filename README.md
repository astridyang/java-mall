## 分布式微服务电商系统
使用Spring Boot和Spring Cloud
## Fetures
+ 使用Spring Initializr构建Spring Boot项目，Maven作为包管理工具，Git做版本控制
+ 使用Docker部署Nginx、MySQL、Redis、Nacos、ElasticSearch、RabbitMQ、Minio等服务
+ 使用Nginx作为项目网关和静态资源服务器
+ 使用Nacos作为微服务注册和配置中心
+ 使用SpringCache缓存商品分类和详情数据到Redis，加快页面加载和减少数据库查询
+ 使用SpringSession在微服务之间共享session
+ 使用openfeign实现微服务之间接口互相调用
+ 使用RabbitMQ死信队列关闭超时订单、解锁库存
+ 使用Scheduled定时任务定时上架秒杀商品
+ 使用Sentinel实现高并发流量控制
+ 使用Sleuth+Zipkin做调用链路追踪和调用链可视化分析
+ 接入Alipay支付服务
+ 使用Minio存储商品图片
## 微服务
+ gateway 网关
+ product 商品
+ search 搜索
+ member 会员
+ cart 购物车
+ order 订单
+ coupon 优惠券
+ seckill 秒杀
+ ware 库存