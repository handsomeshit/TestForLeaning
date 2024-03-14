package com.betaTest.service;

import com.betaTest.contant.enums.ModifyLogEnums;
import com.betaTest.domain.ModifyLog;
import com.betaTest.domain.ModifyLogPageDTO;
import com.betaTest.handler.ModifyLogHandler;
import com.github.pagehelper.PageInfo;

/**
 * <p>
 *  变更日志服务接口
 * </p>
 *
 * @author LMX
 * @date 2023/08/31/11:16
 */
public interface IModifyLogService<T> {

    void doModifyLog(T oldObj, T newObj, ModifyLogEnums.OperationType operationType, String loginUser);

    void doModifyLog(T oldObj, T newObj, ModifyLogHandler modifyLogHandler);

    PageInfo<ModifyLog> selectModifyLogWithDetailList(ModifyLogPageDTO modifyLogPageDTO);
}
