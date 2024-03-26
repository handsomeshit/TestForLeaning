package com.betaTest.domain.entity;

import lombok.Data;

import java.util.Date;

/**
 * <p>
 *  测试mapstruct
 * </p>
 *
 * @author LMX
 * @date 2024/03/26/17:46
 */
@Data
public class User {
    private Long id;
    private Date gmtCreate;
    private Date createTime;
    private Long buyerId;
    private Long age;
    private String userNick;
    private String userVerified;
}
