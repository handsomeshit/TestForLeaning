package com.betaTest.listener;

import com.betaTest.envet.BlackListEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * <p>
 *  黑名单监听器处理
 * </p>
 *
 * @author LMX
 * @date 2023/04/24/10:46
 */
@Component
public class BlackListListener {

    @EventListener(BlackListEvent.class)
    public void onApplicationEvent(BlackListEvent blackListEvent) {
        System.out.println(blackListEvent);
        //黑名单逻辑处理
        System.out.println("黑名单逻辑处理");
    }
}
