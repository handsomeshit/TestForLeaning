package com.betaTest.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 数据变更日志
 * </p>
 *
 * @author LMX
 * @since 2023-08-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("modify_log")
public class ModifyLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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
     * 是否删除 0-未删除 null为已删除
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    /**
     * 更新人
     */
    private String updatedBy;

    /**
     * 数据变更日志明细列表
     */
    @TableField(exist = false)
    private List<ModifyLogDetail> modifyLogDetailList;
}
