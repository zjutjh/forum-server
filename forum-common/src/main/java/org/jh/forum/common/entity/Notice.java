package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import org.jh.forum.common.constants.NoticeTypeEnum;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("notice")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notice extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long receiverId;
    private Long senderId;

    private NoticeTypeEnum type;
    private NoticeTypeEnum positionType;
    private Long positionId;
    private Long commentId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    private Long createUid;
    private Long updateUid;

    @TableLogic
    private Boolean deleted;
    private String attribute;
}