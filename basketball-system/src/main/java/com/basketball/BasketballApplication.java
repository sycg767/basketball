package com.basketball;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 篮球馆管理系统启动类
 *
 * @author Basketball Team
 * @date 2025-09-30
 */
@SpringBootApplication
@MapperScan("com.basketball.mapper")
@ComponentScan("com.basketball")
@EnableScheduling
public class BasketballApplication {

    public static void main(String[] args) {
        SpringApplication.run(BasketballApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("  篮球馆管理系统启动成功！");
        System.out.println("  Swagger文档地址: http://localhost:8080/swagger-ui.html");
        System.out.println("========================================\n");
    }
}