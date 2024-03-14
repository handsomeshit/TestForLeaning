package com.betaTest.domain;

import lombok.Data;

/**
 * <p>
 *  变更日志查询对象
 * </p>
 *
 * @author LMX
 * @date 2023/09/04/17:07
 */
@Data
public class ModifyLogPageDTO {

    /**
     * 操作的表
     */
    private String operationTable;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 关联记录的唯一标识
     */
    private String uniqueFlag;

    private Integer pageNum;

    private Integer pageSize;
}
