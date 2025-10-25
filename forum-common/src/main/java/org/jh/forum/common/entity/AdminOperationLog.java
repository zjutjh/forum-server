package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.jh.forum.common.constants.AdminOperationLogTypeEnum;

/**
 * 管理员操作记录实体类
 *
 * @author SugarMGP
 * @TableName admin_operation_log
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "admin_operation_log")
@Data
@SuperBuilder
public class AdminOperationLog extends BaseEntity {
    private AdminOperationLogTypeEnum type;

    private Long targetId;

    private Long userId;

    private String beforeContent;

    private String afterContent;
}