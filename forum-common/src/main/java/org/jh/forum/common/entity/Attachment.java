package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.jh.forum.common.constants.AttachmentTypeEnum;
import org.jh.forum.common.constants.TargetTypeEnum;
import lombok.experimental.SuperBuilder;

/**
 * @author SugarMGP
 * @TableName attachment
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "attachment")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Attachment extends BaseEntity {
    private Long userId;

    private Long fileId;

    private TargetTypeEnum targetType;

    private Long targetId;

    private AttachmentTypeEnum type;

    private String filename;
}