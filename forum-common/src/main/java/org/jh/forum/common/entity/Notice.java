package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.jh.forum.common.constants.NoticePositionTypeEnum;
import org.jh.forum.common.constants.NoticeTypeEnum;


/**
 * @author lyyzzz
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("notice")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Notice extends BaseEntity {
    private Long receiverId;
    private Long senderId;
    private NoticeTypeEnum type;
    private NoticePositionTypeEnum positionType;
    private Long postId;
    private Long commentId;
    private Long replyId;
    private Long newCommentId;
}