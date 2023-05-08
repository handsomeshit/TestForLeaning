package com.betaTest;

import com.betaTest.service.impl.EmailService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
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
public class BetaTestApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(BetaTestApplication.class, args);
        EmailService emailService = applicationContext.getBean(EmailService.class);
        emailService.sendEmail("2609360954@qq.com", "测试");
    }
}
