package org.jh.forum.server.manger;

import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.constants.TargetTypeEnum;
import org.jh.forum.common.entity.Attachment;
import org.jh.forum.common.entity.Post;
import org.jh.forum.common.exceptions.ForumServiceException;
import org.jh.forum.server.mapper.AttachmentMapper;
import org.jh.forum.server.mapper.PostMapper;
import org.springframework.stereotype.Service;

/**
 * @author SugarMGP
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FileManager {
    private final AttachmentMapper attachmentMapper;
    private final PostMapper postMapper;

    public void bindAttachment(Long attachmentId, TargetTypeEnum targetType, Long targetId) {
        Attachment attachment = attachmentMapper.selectById(attachmentId);
        if (attachment == null) {
            throw new ForumServiceException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }
        if (attachment.getTargetId() != -1L || attachment.getUserId() != StpUtil.getLoginIdAsLong()) {
            throw new ForumServiceException(ExceptionEnum.PERMISSION_NOT_ALLOWED);
        }
        if (targetType == TargetTypeEnum.POST) {
            Post post = postMapper.selectById(targetId);
            if (post == null) {
                throw new ForumServiceException(ExceptionEnum.RESOURCE_NOT_FOUND);
            }
            if (!post.getUserId().equals(attachment.getUserId())) {
                throw new ForumServiceException(ExceptionEnum.PERMISSION_NOT_ALLOWED);
            }
        } else if (targetType == TargetTypeEnum.COMMENT) {
            // TODO: 评论绑定
        }

        attachment.setTargetType(targetType);
        attachment.setTargetId(targetId);
        attachmentMapper.updateById(attachment);
    }
}
