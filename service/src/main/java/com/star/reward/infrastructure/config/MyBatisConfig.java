package com.star.reward.infrastructure.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis 配置
 */
@Configuration
@MapperScan("com.star.reward.infrastructure.persistence.dao.mapper")
public class MyBatisConfig {
}
