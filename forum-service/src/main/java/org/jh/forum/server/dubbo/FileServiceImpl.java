package org.jh.forum.server.dubbo;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.protobuf.Any;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.dubbo.*;
import org.jh.forum.common.constants.AttachmentTypeEnum;
import org.jh.forum.common.constants.TargetTypeEnum;
import org.jh.forum.common.entity.Attachment;
import org.jh.forum.common.entity.File;
import org.jh.forum.server.mapper.AttachmentMapper;
import org.jh.forum.server.mapper.FileMapper;
import org.jh.forum.server.mapper.PostMapper;
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
    private PostMapper postMapper;

    @Override
    public ServiceResult checkBlake3(CheckBlake3Req request) {
        String blake3 = request.getBlake3();
        File file = fileMapper.selectOne(new LambdaQueryWrapper<File>().eq(File::getBlake3, blake3));
        FileIdResp resp = FileIdResp.newBuilder()
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
        FileIdResp resp = FileIdResp.newBuilder()
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
        AttachmentIdResp resp = AttachmentIdResp.newBuilder()
                .setAttachmentId(attachment.getId())
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
}
