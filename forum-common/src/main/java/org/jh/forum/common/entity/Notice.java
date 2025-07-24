package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import org.jh.forum.common.constants.NoticeTypeEnum;


/**
 * @author lyyzzz
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("notice")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notice extends BaseEntity {
    private Long receiverId;
    private Long senderId;
    private NoticeTypeEnum type;
    private NoticeTypeEnum positionType;
    private Long positionId;
    private Long commentId;
    private Boolean isRead;
}