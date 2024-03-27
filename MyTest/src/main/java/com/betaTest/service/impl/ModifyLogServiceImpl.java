package com.betaTest.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.betaTest.contant.enums.ModifyLogEnums;
import com.betaTest.domain.ModifyLog;
import com.betaTest.domain.ModifyLogDetail;
import com.betaTest.domain.ModifyLogPageDTO;
import com.betaTest.handler.ModifyLogHandler;
import com.betaTest.mapper.ModifyLogDetailMapper;
import com.betaTest.mapper.ModifyLogMapper;
import com.betaTest.service.IModifyLogService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  变更日志服务实现
 * </p>
 *
 * @author LMX
 * @date 2023/08/31/11:17
 */
@Service
@Slf4j
public class ModifyLogServiceImpl<T> implements IModifyLogService<T> {

    @Resource
    private ModifyLogMapper modifyLogMapper;

    @Resource
    private ModifyLogDetailMapper modifyLogDetailMapper;

    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void doModifyLog(T oldObj, T newObj, ModifyLogEnums.OperationType operationType, String loginUser) {
        log.info("收到一条日志记录, 旧记录:{}, 新记录:{}", oldObj, newObj);
        ModifyLogEnums.OperationObject operationObject = ModifyLogEnums.OperationObject.getEnumByClass(oldObj.getClass());
        ModifyLogHandler modifyLogHandler = new ModifyLogHandler<>(operationObject.getTableName(), operationType.getOperationType());
        List<ModifyLogDetail> logDetailList = modifyLogHandler.compareAndGetResultList(oldObj, newObj);
        if(CollectionUtils.isEmpty(logDetailList)) {
            log.info("此次操作没有数据变更");
            return;
        }
        ModifyLog modifyLog = modifyLogHandler.generateModifyLog(null == loginUser ? "admin" : loginUser);
        modifyLogMapper.insert(modifyLog);
        for (ModifyLogDetail logDetail : logDetailList) {
            logDetail.setModifyLogId(modifyLog.getId());
            modifyLogDetailMapper.insert(logDetail);
        }
    }

    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void doModifyLog(T oldObj, T newObj, ModifyLogHandler modifyLogHandler) {
        log.info("收到一条日志记录, 旧记录:{}, 新记录:{}", oldObj, newObj);
        List<ModifyLogDetail> logDetailList = modifyLogHandler.compareAndGetResultList(oldObj, newObj);
        if(CollectionUtils.isEmpty(logDetailList)) {
            log.info("此次操作没有数据变更");
            return;
        }
        ModifyLog modifyLog = modifyLogHandler.generateModifyLog("admin");
        modifyLogMapper.insert(modifyLog);
        for (ModifyLogDetail logDetail : logDetailList) {
            logDetail.setModifyLogId(modifyLog.getId());
            modifyLogDetailMapper.insert(logDetail);
        }
    }

    @Override
    public PageInfo<ModifyLog> selectModifyLogWithDetailList(ModifyLogPageDTO modifyLogPageDTO) {
        Integer pageNum = null == modifyLogPageDTO.getPageNum() ? 1:modifyLogPageDTO.getPageNum();
        Integer pageSize = null == modifyLogPageDTO.getPageSize() ? 10:modifyLogPageDTO.getPageSize();
        List<ModifyLog> modifyLogs = modifyLogMapper.selectList(Wrappers.lambdaQuery(new ModifyLog())
                .eq(StringUtils.isNotBlank(modifyLogPageDTO.getOperationTable()), ModifyLog::getOperationTable, modifyLogPageDTO.getOperationTable())
                .eq(StringUtils.isNotBlank(modifyLogPageDTO.getOperationType()), ModifyLog::getOperationType, modifyLogPageDTO.getOperationType())
                .eq(ModifyLog::getUniqueFlag, modifyLogPageDTO.getUniqueFlag())
                .orderByDesc(ModifyLog::getCreatedAt)
                .last(String.format("limit %d, %d", (pageNum-1)*pageSize, pageSize)));
        for (ModifyLog each : modifyLogs) {
            List<ModifyLogDetail> logDetailList = modifyLogDetailMapper.selectList(Wrappers.lambdaQuery(new ModifyLogDetail()).eq(ModifyLogDetail::getModifyLogId, each.getId()));
            each.setModifyLogDetailList(logDetailList);
        }
        Integer total = modifyLogMapper.selectCount(Wrappers.lambdaQuery(new ModifyLog()).eq(ModifyLog::getUniqueFlag, modifyLogPageDTO.getUniqueFlag()));
        PageInfo<ModifyLog> pageInfo = new PageInfo<>(modifyLogs);
        pageInfo.setTotal(total);
        return pageInfo;
    }
}
