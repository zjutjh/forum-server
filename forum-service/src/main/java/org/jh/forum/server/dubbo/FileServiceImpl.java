package org.jh.forum.server.dubbo;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.cube.CubeService;
import org.jh.forum.api.dubbo.service.FileService;
import org.jh.forum.common.constants.AttachmentTypeEnum;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.constants.TargetTypeEnum;
import org.jh.forum.common.dto.response.GetAttachmentInfoResponse;
import org.jh.forum.common.entity.Attachment;
import org.jh.forum.common.entity.File;
import org.jh.forum.common.exceptions.ForumServiceException;
import org.jh.forum.server.mapper.AttachmentMapper;
import org.jh.forum.server.mapper.FileMapper;

import jakarta.annotation.Resource;

/**
 * @author SugarMGP
 */
@DubboService(version = "1.0.0")
@Slf4j
public class FileServiceImpl implements FileService {
    @Resource
    private FileMapper fileMapper;

    @Resource
    private AttachmentMapper attachmentMapper;

    @Resource
    private CubeService cubeService;

    @Override
    public Long checkBlake3(String blake3) {
        File file = fileMapper.selectOne(new LambdaQueryWrapper<File>().eq(File::getBlake3, blake3));
        return file == null ? -1 : file.getId();
    }

    @Override
    public Long createFile(String objectKey, String blake3) {
        File file = File.builder()
                .blake3(blake3)
                .objectKey(objectKey)
                .build();
        fileMapper.insert(file);
        return file.getId();
    }

    @Override
    public Long createAttachment(Long fileId, AttachmentTypeEnum type, String filename) {
        Attachment attachment = Attachment.builder()
                .userId(StpUtil.getLoginIdAsLong())
                .fileId(fileId)
                .targetType(TargetTypeEnum.POST)
                .targetId(-1L)
                .type(type)
                .filename(filename)
                .build();
        attachmentMapper.insert(attachment);
        return attachment.getId();
    }

    @Override
    public GetAttachmentInfoResponse getAttachmentInfo(Long attachmentId) {
        Attachment attachment = attachmentMapper.selectById(attachmentId);
        if (attachment == null) {
            throw new ForumServiceException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }
        File file = fileMapper.selectById(attachment.getFileId());
        return GetAttachmentInfoResponse.builder()
                .url(cubeService.getFileUrl(file.getObjectKey()))
                .type(attachment.getType())
                .filename(attachment.getFilename())
                .build();
    }
}
