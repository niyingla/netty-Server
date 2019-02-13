package com.faceword.nio;


import com.alibaba.fastjson.JSON;
import com.faceword.nio.config.ApplicationBeanConfig;
import com.faceword.nio.listener.NettyServerListener;
import com.faceworld.base.redis.RedisClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import javax.annotation.Resource;
import java.util.Set;

@Slf4j
@EnableSwagger2 //用于配置在线restapi文档
@MapperScan( basePackages = {"com.faceword.nio.mybatis.mapper" ,"com.faceworld.base.mybatis.mapper" } )
@ComponentScan(basePackages = {"com.faceworld.base.config","com.faceword.nio", "com.faceworld.base.mq.config","com.faceworld.base.redis"})
@SpringBootApplication
public class NioApplication extends ApplicationBeanConfig implements CommandLineRunner {

    @Resource
    private NettyServerListener nettyServerListener;

    public static void main(String[] args) {

        SpringApplication.run(NioApplication.class, args);
        System.out.println("*************************************************************************************");
        System.out.println("*********************【NioApplication服务启动成功】**************************************");
        System.out.println("*********************【NIO链接指标请求：http://localhost:8080/nio/actuator/all】*********");
        System.out.println("*********************【连接池请求：http://localhost:8080/druid/sql.html】****************");
        System.out.println("【=================== 【在线api文档url：localhost:8080/swagger-ui.html】 =============】");
        System.out.println("*************************************************************************************");
        System.out.println("*************************************************************************************");
    }

    /**
     * 通过springboot启动命令来启动netty服务端
     * @param args
     */
    @Override
    public void run(String... args) {

       nettyServerListener.process();
       //服务器重启删除face前缀的列
        Set<String> keys = RedisClientUtil.keys("face:*");
        log.info("delete keys=【{}】" , JSON.toJSONString(keys));
        RedisClientUtil.delete(keys);
    }
}
