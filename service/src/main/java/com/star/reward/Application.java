package com.star.reward;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 应用启动类
 */
@SpringBootApplication
@EnableFeignClients(basePackages = {"com.star.reward.infrastructure.external.feign.client", "com.star.sso.api"})
@MapperScan("com.star.reward.infrastructure.persistence.dao.mapper")
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
