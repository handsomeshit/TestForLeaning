package com.betaTest.envet;

import org.springframework.context.ApplicationEvent;

/**
 * <p>
 *  事件源
 * </p>
 *
 * @author LMX
 * @date 2023/04/24/10:11
 */
public class BlackListEvent extends ApplicationEvent {

    private final String address;

    private final String content;

    public BlackListEvent(Object source, String address, String content) {
        super(source);
        this.address = address;
        this.content = content;
    }
}
