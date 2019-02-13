# nettyServer

#### 介绍
{**以下是码云平台说明，您可以替换此简介**
码云是开源中国推出的基于 Git 的代码托管平台（同时支持 SVN）。专为开发者提供稳定、高效、安全的云端软件开发协作平台
无论是个人、团队、或是企业，都能够用码云实现代码托管、项目管理、协作开发。企业项目请看 [https://gitee.com/enterprises](https://gitee.com/enterprises)}

#### 软件架构
springboot、netty、rabbitmq、redis


#### 安装工具

1. jdk1.8
2. rabbitmq
3. redis
4. mysql

#### 使用说明

1. com.faceword.nio.listener.NettyServerListener 为netty的主配置文件，配置了netty参数、心跳机制、读取客户端消息handle
2. com.faceword.nio.business.NettyServerHandler.channelRead0 为消息接收统一逻辑（采用自定义消息策略，包头+包体），包头参数主要包括消息体的总长度，消息的命令号，通过命令号触发对应的业务逻辑，当读取到消息长度等于接收到包头的长度时，标识该消息接收完毕
3. com.faceword.nio.dispatcher.NioMessageDispatcher 业务分发器，netty服务不做业务处理，当接收完所有消息后，通过线程池转发到业务handle，根据实际的并发数，调整线程池连接数，更好的提高性能

#### 参与贡献




#### 码云特技

