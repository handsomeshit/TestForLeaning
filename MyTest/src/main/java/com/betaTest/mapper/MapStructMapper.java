package com.betaTest.mapper;

import com.betaTest.domain.entity.User;
import com.betaTest.domain.po.UserPo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * <p>
 *
 * </p>
 *
 * @author LMX
 * @date 2024/03/26/17:49
 */
@Mapper
public interface MapStructMapper {

    MapStructMapper INSTANCT = Mappers.getMapper(MapStructMapper.class);

    UserPo userToUserPo(User user);

    User userPoToUser(UserPo userPo);
}
