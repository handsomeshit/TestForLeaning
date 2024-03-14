package com.betaTest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.betaTest.domain.ModifyLog;
import com.betaTest.domain.ModifyLogPageDTO;


import java.util.List;

/**
 * <p>
 * 数据变更日志 Mapper 接口
 * </p>
 *
 * @author LMX
 * @since 2023-08-31
 */
public interface ModifyLogMapper extends BaseMapper<ModifyLog> {

    List<ModifyLog> selectModifyLogWithDetailList(ModifyLogPageDTO modifyLogPageDTO);
}
