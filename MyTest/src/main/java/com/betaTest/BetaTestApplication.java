package com.betaTest;

import com.betaTest.domain.ModifyLogPageDTO;
import com.betaTest.service.impl.EmailService;
import com.betaTest.service.impl.ModifyLogServiceImpl;
import com.github.pagehelper.PageInfo;
import org.mybatis.spring.annotation.MapperScan;
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
@MapperScan("com.betaTest.mapper")
public class BetaTestApplication {
    public static void main(String[] args) {
        sendEmail(args);
//        testModifyLog(args);
    }

    public static void sendEmail(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(BetaTestApplication.class, args);
        EmailService emailService = applicationContext.getBean(EmailService.class);
        emailService.sendEmail("2609360954@qq.com", "测试");
    }

    public static void testModifyLog(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(BetaTestApplication.class, args);
        ModifyLogServiceImpl modifyLogService = applicationContext.getBean(ModifyLogServiceImpl.class);
        ModifyLogPageDTO modifyLogPageDTO = new ModifyLogPageDTO();
        modifyLogPageDTO.setUniqueFlag("48");
        PageInfo pageInfo = modifyLogService.selectModifyLogWithDetailList(modifyLogPageDTO);
        System.out.println(pageInfo);
    }
}
