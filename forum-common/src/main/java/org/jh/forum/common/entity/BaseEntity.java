package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author SugarMGP
 */
@Data
public class BaseEntity implements Serializable {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.UPDATE)
    private LocalDateTime updatedAt;

    @TableField(value = "create_uid", fill = FieldFill.INSERT)
    private Long createUid;

    @TableField(value = "update_uid", fill = FieldFill.UPDATE)
    private Long updateUid;

    @TableLogic
    private Boolean deleted;

    private String attribute;
}
