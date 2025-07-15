package org.jh.forum.server.manger;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jh.cube.CubeException;
import org.jh.cube.CubeService;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.constants.TargetTypeEnum;
import org.jh.forum.common.entity.Attachment;
import org.jh.forum.common.entity.File;
import org.jh.forum.common.entity.Post;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.server.mapper.AttachmentMapper;
import org.jh.forum.server.mapper.FileMapper;
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
    private final FileMapper fileMapper;
    private final CubeService cubeService;

    public void deleteAttachment(Long attachmentId) {
        Attachment attachment = attachmentMapper.selectById(attachmentId);
        if (attachment != null) {
            Long fileId = attachment.getFileId();
            attachmentMapper.deleteById(attachmentId);
            Long fileCount = attachmentMapper.selectCount(new LambdaQueryWrapper<Attachment>().eq(Attachment::getFileId, fileId));
            if (fileCount == 0) {
                deleteFile(fileId);
            }
        }
    }

    private void deleteFile(Long fileId) {
        File file = fileMapper.selectById(fileId);
        if (file != null) {
            try {
                cubeService.deleteFile(file.getObjectKey());
                fileMapper.deleteById(fileId);
            } catch (CubeException e) {
                log.warn("Failed to delete file: {}", file.getObjectKey(), e);
            }
        }
    }

    public void bindAttachment(Long attachmentId, TargetTypeEnum targetType, Long targetId) {
        Attachment attachment = attachmentMapper.selectById(attachmentId);
        if (attachment == null) {
            throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }
        if (attachment.getTargetId() != -1L || attachment.getUserId() != StpUtil.getLoginIdAsLong()) {
            throw new ApiException(ExceptionEnum.PERMISSION_NOT_ALLOWED);
        }
        if (targetType == TargetTypeEnum.POST) {
            Post post = postMapper.selectById(targetId);
            if (post == null) {
                throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
            }
            if (!post.getUserId().equals(attachment.getUserId())) {
                throw new ApiException(ExceptionEnum.PERMISSION_NOT_ALLOWED);
            }
        } else if (targetType == TargetTypeEnum.COMMENT) {
            // TODO: 评论绑定
        }

        attachment.setTargetType(targetType);
        attachment.setTargetId(targetId);
        attachmentMapper.updateById(attachment);
    }

    public String getFileUrl(Long fileId) {
        File file = fileMapper.selectById(fileId);
        if (file == null) {
            throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }
        return cubeService.getFileUrl(file.getObjectKey());
    }
}
