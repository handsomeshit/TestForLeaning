package com.betaTest.service.impl;

import com.betaTest.envet.BlackListEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *  发送邮件
 * </p>
 *
 * @author LMX
 * @date 2023/04/24/10:31
 */
@Service
public class EmailService implements ApplicationEventPublisherAware {

    private List<String> blackList = Arrays.asList("2609360954@qq.com");

    private ApplicationEventPublisher publisher;

    public void setBlackList(List<String> blackList) {
        this.blackList = blackList;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void sendEmail(String address, String content) {
        if(blackList.contains(address)) {
            //发布黑名单事件
            publisher.publishEvent(new BlackListEvent(this, address, content));
            return;
        }
        //发送邮件
        System.out.println("发送邮件");
    }
}
