# -
采用springBoot+Mybatis+Mysql开发的电商系统，实现了用户登录注册、商品增加展示、用户下单以及简单的秒杀功能

采用前后端分离的方式，在前端的UI使用了css、html、jquery、图片、以及Metronic对应的前端框架来完成了用户注册、
登录以及商品展示、下单交易、秒杀倒计时等基本的前端功能。然后在接入层使用了springMVC的controller，定义了对
应的viewObject，返回了通用的对象，并且在controller层通过通用的一个异常处理的方式，结合通用的返回对象返回了
对应的前后端分离的jason的datastatus的模型。之后，在业务层当中使用了对应的myBatis的接入以及model层领域模型
的概念完成了一个对应的用户服务、商品服务、交易服务、活动服务相关核心服务的业务层。并且在数据层使用了@Transactional
的标签来完成事务的切面，使用数据库mybatis的dao来完成对事务相关的操作。

下面几张图给出了对应包以及其内部类的功能

![](https://github.com/overridehuhao/light-miaosha-Project/tree/master/images/controller.png)
