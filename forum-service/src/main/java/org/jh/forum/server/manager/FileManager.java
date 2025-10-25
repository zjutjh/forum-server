package org.jh.forum.server.manager;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jh.cube.CubeException;
import org.jh.cube.CubeService;
import org.jh.forum.common.constants.AttachmentTypeEnum;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.constants.TargetTypeEnum;
import org.jh.forum.common.dto.PictureInfoDTO;
import org.jh.forum.common.entity.Attachment;
import org.jh.forum.common.entity.File;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.server.mapper.AttachmentMapper;
import org.jh.forum.server.mapper.FileMapper;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Objects;

/**
 * @author SugarMGP
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FileManager {
    private static final Long EMPTY_TARGET_ID = -1L;

    private final AttachmentMapper attachmentMapper;
    private final FileMapper fileMapper;
    private final CubeService cubeService;

    public void deleteAttachment(Long attachmentId) {
        Attachment attachment = attachmentMapper.selectById(attachmentId);
        if (Objects.isNull(attachment)) {
            return;
        }
        Long fileId = attachment.getFileId();
        attachmentMapper.deleteById(attachmentId);
        Long fileCount = attachmentMapper.selectCount(new LambdaQueryWrapper<Attachment>().eq(Attachment::getFileId, fileId));
        if (fileCount == 0) {
            deleteFile(fileId);
        }
    }

    private void deleteFile(Long fileId) {
        File file = fileMapper.selectById(fileId);
        if (Objects.isNull(file)) {
            return;
        }
        try {
            cubeService.deleteFile(file.getObjectKey());
            fileMapper.deleteById(fileId);
        } catch (CubeException e) {
            log.warn("Failed to delete file: {}", file.getObjectKey(), e);
        }
    }

    public Long getAttachmentIdFromUrl(String url) {
        try {
            String query = new URL(url).getQuery();
            for (String param : query.split("&")) {
                if (param.startsWith("attachment_id=")) {
                    return Long.parseLong(param.substring("attachment_id=".length()));
                }
            }
        } catch (Exception ignore) {
        }
        return null;
    }

    public void bindAttachment(String url, TargetTypeEnum targetType, Long targetId) {
        Long attachmentId = getAttachmentIdFromUrl(url);
        if (attachmentId == null) {
            throw new ApiException(ExceptionEnum.INVALID_URL);
        }
        Attachment attachment = attachmentMapper.selectById(attachmentId);
        if (attachment == null) {
            throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }
        if (!Objects.equals(attachment.getTargetId(), EMPTY_TARGET_ID) || attachment.getUserId() != StpUtil.getLoginIdAsLong()) {
            throw new ApiException(ExceptionEnum.PERMISSION_NOT_ALLOWED);
        }
        attachment.setTargetType(targetType);
        attachment.setTargetId(targetId);
        attachmentMapper.updateById(attachment);
    }

    public PictureInfoDTO buildPictureInfoDTO(Attachment attachment) {
        File file = fileMapper.selectById(attachment.getFileId());
        if (file == null) {
            return null;
        }
        return PictureInfoDTO.builder()
                .url(cubeService.getFileUrl(file.getObjectKey(), false))
                .thumbnailUrl(cubeService.getFileUrl(file.getObjectKey(), true))
                .build();
    }

    public String checkBlake3(String blake3) {
        File file = fileMapper.selectOne(new LambdaQueryWrapper<File>().eq(File::getBlake3, blake3).last("LIMIT 1"));
        return file == null ? null : file.getObjectKey();
    }

    public void createFile(String objectKey, String blake3) {
        File file = File.builder()
                .blake3(blake3)
                .objectKey(objectKey)
                .build();
        fileMapper.insert(file);
    }

    public Long createAttachment(String objectKey, AttachmentTypeEnum type, String filename) {
        File file = fileMapper.selectOne(new LambdaQueryWrapper<File>().eq(File::getObjectKey, objectKey));
        if (file == null) {
            throw new ApiException(ExceptionEnum.SERVER_ERROR);
        }
        Attachment attachment = Attachment.builder()
                .userId(StpUtil.getLoginIdAsLong())
                .fileId(file.getId())
                .targetType(TargetTypeEnum.USER)
                .targetId(EMPTY_TARGET_ID)
                .type(type)
                .filename(filename)
                .build();
        attachmentMapper.insert(attachment);
        return attachment.getId();
    }
}
