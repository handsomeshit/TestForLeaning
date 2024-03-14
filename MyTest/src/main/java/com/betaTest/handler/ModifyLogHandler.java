package com.betaTest.handler;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.betaTest.contant.enums.ModifyLogEnums;
import com.betaTest.domain.ModifyLog;
import com.betaTest.domain.ModifyLogDetail;
import com.betaTest.domain.exception.CustomException;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  变更日志处理器
 *  该功能有个使用前提, 需要新记录中有id记录
 * </p>
 *
 * @author LMX
 * @date 2023/08/31/15:55
 */
@Getter
@Slf4j
public class ModifyLogHandler<T> {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 操作的表
     */
    private String operationTable;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 唯一标识
     */
    private String uniqueFlag;


    /**
     * 流程名称
     */
    private String flowName;

    /**
     * 流程编号
     */
    private String flowNumber;

    /**
     * 备注
     */
    private String remark;

    /**
     * 检查的字段
     */
    private List<Field> declaredFieldList;

    public ModifyLogHandler(String operationTable, String operationType) {
        this.operationTable = operationTable;
        this.operationType = operationType;
        this.flowName = "";
        this.flowNumber = "";
        this.remark = "";
    }

    public ModifyLogHandler(String operationTable, String operationType, String flowName, String flowNumber) {
        this.operationTable = operationTable;
        this.operationType = operationType;
        this.flowName = flowName;
        this.flowNumber = flowNumber;
        this.remark = "";
    }

    public ModifyLogHandler(String operationTable, String operationType, String flowName, String flowNumber, String remark) {
        this.operationTable = operationTable;
        this.operationType = operationType;
        this.flowName = flowName;
        this.flowNumber = flowNumber;
        this.remark = remark;
    }

    public ModifyLog generateModifyLog(String operator) {
        if(StringUtils.isBlank(this.uniqueFlag)) {
            log.info("变更日志处理器未获取到记录的唯一标识");
            throw new CustomException("变更日志处理器未获取到记录的唯一标识");
        }
        ModifyLog modifyLog = new ModifyLog();
        modifyLog.setOperationTable(this.operationTable);
        modifyLog.setOperationType(this.operationType);
        modifyLog.setUniqueFlag(this.uniqueFlag);
        modifyLog.setFlowName(this.flowName);
        modifyLog.setFlowNumber(this.flowNumber);
        modifyLog.setRemark(this.remark);
        modifyLog.setCreatedBy(operator);
        modifyLog.setUpdatedBy(operator);
        return modifyLog;
    }

    public void initHandler(Class compareClass) {
        this.uniqueFlag = "";
        this.declaredFieldList = Arrays.asList(compareClass.getDeclaredFields());
        for (Field field : declaredFieldList) {
            field.setAccessible(true);
        }
    }

    public List<ModifyLogDetail> compareAndGetResultList(T oldObj, T newObj) {
        if(CollectionUtils.isEmpty(this.declaredFieldList)) {
            initHandler(oldObj.getClass());
        }
        List<ModifyLogDetail> resultList = new ArrayList<>();
        for (Field field : this.declaredFieldList) {
            try {
                if("id".equals(field.getName())) {
                    this.uniqueFlag = field.get(newObj).toString();
                }
                ApiModelProperty annotation = field.getAnnotation(ApiModelProperty.class);
                if(null == annotation) {
                    continue;
                }
                if(ModifyLogEnums.OperationObject.getEnumByClass(oldObj.getClass()).getUnCheckedList().contains(annotation.value())) {
                    //如果该属性不需要检查就跳过
                    continue;
                }
                Object oldValue = field.get(oldObj);
                Object newValue = field.get(newObj);
                if(!checkIsModified(oldValue, newValue)) {
                    continue;
                }
                ModifyLogDetail detail = new ModifyLogDetail();
                detail.setUpdateColumn(annotation.value());
                detail.setUpdateColumnEn(getColumnEn(field.getName()));
                detail.setOldValue(getValueString(oldValue));
                detail.setNewValue(getValueString(newValue));
                resultList.add(detail);
            } catch (IllegalAccessException e) {
                log.info("变更日志处理器出错", e);
                e.printStackTrace();
            }
        }
        return resultList;
    }

    /**
     * 检查值是否发生变更
     * @param oldValue 原值
     * @param newValue 新值
     * @return
     */
    private boolean checkIsModified(Object oldValue, Object newValue) {
        if(null == newValue) {
            return false;
        }
        else if (oldValue instanceof Integer
                || oldValue instanceof Float
                || oldValue instanceof Double
                || oldValue instanceof Date
                || oldValue instanceof String) {
            return !oldValue.equals(newValue);
        }
        else {
            return !(oldValue == newValue);
        }
    }

    /**
     * 获取值的字符串形式
     * @param value
     * @return
     */
    private String getValueString(Object value) {
        if (null == value) {
            return "";
        }
        else if (value instanceof Date) {
            return this.simpleDateFormat.format(value);
        } else {
            return value.toString();
        }
    }

    /**
     * 参数转换成数据库字段形式 userName -> user_name
     *
     * @param paramName
     * @return
     */
    private String getColumnEn(String paramName) {
        String[] names = paramName.split("(?=\\p{Upper})");
        for (int i = 0; i < names.length; i++) {
            if (i != 0) {
                names[i] = "_".concat(names[i].toLowerCase());
            } else {
                names[i] = names[i].toLowerCase();
            }
        }
        return String.join("", names);
    }
}
