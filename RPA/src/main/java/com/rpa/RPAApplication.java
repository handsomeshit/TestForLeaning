package com.rpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * <p>
 *  spring启动类
 * </p>
 *
 * @author LMX
 * @date 2023/04/24/10:31
 */
@SpringBootApplication
@EnableScheduling
public class RPAApplication {
    public static void main(String[] args) {
        SpringApplication.run(RPAApplication.class, args);
    }
}
