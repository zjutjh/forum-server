package org.jh.forum.server.dubbo;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.protobuf.Any;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.cube.CubeService;
import org.jh.forum.api.dubbo.*;
import org.jh.forum.common.constants.AttachmentTypeEnum;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.constants.TargetTypeEnum;
import org.jh.forum.common.entity.Attachment;
import org.jh.forum.common.entity.File;
import org.jh.forum.common.exceptions.ForumServiceException;
import org.jh.forum.server.mapper.AttachmentMapper;
import org.jh.forum.server.mapper.FileMapper;
import org.jh.forum.server.utils.EnumUtil;

import jakarta.annotation.Resource;
import java.util.concurrent.CompletableFuture;

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
    public ServiceResult checkBlake3(CheckBlake3Req request) {
        String blake3 = request.getBlake3();
        File file = fileMapper.selectOne(new LambdaQueryWrapper<File>().eq(File::getBlake3, blake3));
        FileId resp = FileId.newBuilder()
                .setFileId(file == null ? -1 : file.getId())
                .build();
        return ServiceResult.newBuilder()
                .setIsSuccess(true)
                .setData(Any.pack(resp))
                .build();
    }

    @Override
    public ServiceResult createFile(CreateFileReq request) {
        File file = File.builder()
                .blake3(request.getBlake3())
                .objectKey(request.getObjectKey())
                .build();
        fileMapper.insert(file);
        FileId resp = FileId.newBuilder()
                .setFileId(file.getId())
                .build();
        return ServiceResult.newBuilder()
                .setIsSuccess(true)
                .setData(Any.pack(resp))
                .build();
    }

    @Override
    public ServiceResult createAttachment(CreateAttachmentReq request) {
        Attachment attachment = Attachment.builder()
                .userId(StpUtil.getLoginIdAsLong())
                .fileId(request.getFileId())
                .targetType(TargetTypeEnum.POST)
                .targetId(-1L)
                .type(EnumUtil.getEnumByField(AttachmentTypeEnum.class, AttachmentTypeEnum::getValue, request.getType()))
                .filename(request.getFilename())
                .build();
        attachmentMapper.insert(attachment);
        AttachmentId resp = AttachmentId.newBuilder()
                .setAttachmentId(attachment.getId())
                .build();
        return ServiceResult.newBuilder()
                .setIsSuccess(true)
                .setData(Any.pack(resp))
                .build();
    }

    @Override
    public ServiceResult getAttachmentInfo(AttachmentId request) {
        Attachment attachment = attachmentMapper.selectById(request.getAttachmentId());
        if (attachment == null) {
            throw new ForumServiceException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }
        File file = fileMapper.selectById(attachment.getFileId());
        GetAttachmentInfoResp resp = GetAttachmentInfoResp.newBuilder()
                .setUrl(cubeService.getFileUrl(file.getObjectKey()))
                .setType(attachment.getType().getValue())
                .setFilename(attachment.getFilename())
                .build();
        return ServiceResult.newBuilder()
                .setIsSuccess(true)
                .setData(Any.pack(resp))
                .build();
    }

    @Override
    public CompletableFuture<ServiceResult> checkBlake3Async(CheckBlake3Req request) {
        return CompletableFuture.supplyAsync(() -> checkBlake3(request));
    }

    @Override
    public CompletableFuture<ServiceResult> createFileAsync(CreateFileReq request) {
        return CompletableFuture.supplyAsync(() -> createFile(request));
    }

    @Override
    public CompletableFuture<ServiceResult> createAttachmentAsync(CreateAttachmentReq request) {
        return CompletableFuture.supplyAsync(() -> createAttachment(request));
    }

    @Override
    public CompletableFuture<ServiceResult> getAttachmentInfoAsync(AttachmentId request) {
        return CompletableFuture.supplyAsync(() -> getAttachmentInfo(request));
    }
}
