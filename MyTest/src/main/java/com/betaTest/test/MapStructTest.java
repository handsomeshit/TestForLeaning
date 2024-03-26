package com.betaTest.test;

import com.betaTest.domain.entity.User;
import com.betaTest.domain.po.UserPo;
import com.betaTest.mapper.MapStructMapper;

import java.util.Date;

/**
 * <p>
 *  效率更高的编译时期生成对象转换方法
 * </p>
 *
 * @author LMX
 * @date 2024/03/26/17:53
 */
public class MapStructTest {

    public static void main(String[] args) {
        User user = new User();
        user.setAge(20L);
        user.setCreateTime(new Date());
        user.setGmtCreate(new Date());
        user.setUserNick("handsomeboy");
        user.setId(1L);
        UserPo userPo = MapStructMapper.INSTANCT.userToUserPo(user);
        System.out.println(userPo);
    }
}
