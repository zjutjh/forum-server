package org.jh.forum.server.dubbo;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.dubbo.service.FileService;
import org.jh.forum.common.constants.AttachmentTypeEnum;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.constants.TargetTypeEnum;
import org.jh.forum.common.entity.Attachment;
import org.jh.forum.common.entity.File;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.server.mapper.AttachmentMapper;
import org.jh.forum.server.mapper.FileMapper;

import jakarta.annotation.Resource;

/**
 * @author SugarMGP
 */
@DubboService
@Slf4j
public class FileServiceImpl implements FileService {
    @Resource
    private FileMapper fileMapper;

    @Resource
    private AttachmentMapper attachmentMapper;

    @Override
    public String checkBlake3(String blake3) {
        File file = fileMapper.selectOne(new LambdaQueryWrapper<File>().eq(File::getBlake3, blake3));
        return file == null ? null : file.getObjectKey();
    }

    @Override
    public void createFile(String objectKey, String blake3) {
        File file = File.builder()
                .blake3(blake3)
                .objectKey(objectKey)
                .build();
        fileMapper.insert(file);
    }

    @Override
    public Long createAttachment(String objectKey, AttachmentTypeEnum type, String filename) {
        File file = fileMapper.selectOne(new LambdaQueryWrapper<File>().eq(File::getObjectKey, objectKey));
        if (file == null) {
            throw new ApiException(ExceptionEnum.SERVER_ERROR);
        }
        Attachment attachment = Attachment.builder()
                .userId(StpUtil.getLoginIdAsLong())
                .fileId(file.getId())
                .targetType(TargetTypeEnum.POST)
                .targetId(-1L)
                .type(type)
                .filename(filename)
                .build();
        attachmentMapper.insert(attachment);
        return attachment.getId();
    }
}
