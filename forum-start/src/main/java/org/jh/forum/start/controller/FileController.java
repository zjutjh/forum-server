package org.jh.forum.start.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.google.protobuf.InvalidProtocolBufferException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jh.cube.CubeService;
import org.jh.forum.api.dubbo.*;
import org.jh.forum.common.constants.AttachmentTypeEnum;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.dto.response.UploadPictureResponse;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.start.models.AjaxResult;
import org.jh.forum.start.utils.BlakeUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import java.io.IOException;
import java.time.LocalDate;

/**
 * @author SugarMGP
 */
@Slf4j
@RequestMapping("/file")
@RestController
@Tag(name = "文件", description = "文件相关接口")
@SaCheckLogin
public class FileController {
    @Resource
    private CubeService cubeService;

    @Resource
    private FileService fileService;

    @Operation(summary = "上传图片")
    @PostMapping(path = "/picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult<UploadPictureResponse> uploadPicture(@RequestParam("picture") MultipartFile picture) {
        Long attachmentId = uploadFile(picture, AttachmentTypeEnum.PICTURE);
        return AjaxResult.success(new UploadPictureResponse(attachmentId));
    }

    public Long uploadFile(MultipartFile file, AttachmentTypeEnum type) {
        try {
            String hash = BlakeUtils.computeHash(file);
            CheckBlake3Req checkBlake3Req = CheckBlake3Req.newBuilder().setBlake3(hash).build();
            FileIdResp resp = fileService.checkBlake3(checkBlake3Req).getData().unpack(FileIdResp.class);

            // 如果文件不存在
            if (resp.getFileId() == -1) {
                LocalDate currentDate = LocalDate.now();
                String location = String.format("%d%02d", currentDate.getYear(), currentDate.getMonthValue());
                String objectKey = cubeService.uploadFile(
                        file,
                        location,
                        type == AttachmentTypeEnum.PICTURE,
                        true
                );
                CreateFileReq createFileReq = CreateFileReq.newBuilder()
                        .setBlake3(hash)
                        .setObjectKey(objectKey)
                        .build();
                resp = fileService.createFile(createFileReq).getData().unpack(FileIdResp.class);
            }
            CreateAttachmentReq createAttachmentReq = CreateAttachmentReq.newBuilder()
                    .setFileId(resp.getFileId())
                    .setFilename(file.getOriginalFilename())
                    .setType(type.getValue())
                    .build();
            AttachmentIdResp attachmentIdResp = fileService.createAttachment(createAttachmentReq)
                    .getData()
                    .unpack(AttachmentIdResp.class);
            return attachmentIdResp.getAttachmentId();
        } catch (InvalidProtocolBufferException e) {
            throw new ApiException(ExceptionEnum.UNKNOWN_ERROR);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new ApiException(ExceptionEnum.FILE_UPLOAD_ERROR);
        }
    }
}
