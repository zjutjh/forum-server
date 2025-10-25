package org.jh.forum.start.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.jh.cube.CubeService;
import org.jh.forum.api.dubbo.service.FileService;
import org.jh.forum.common.constants.AttachmentTypeEnum;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.dto.response.UploadResponse;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.common.exceptions.ModerationException;
import org.jh.forum.server.client.AliyunGreenClient;
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
    private AliyunGreenClient aliyunGreenClient;

    @DubboReference
    private FileService fileService;

    @Operation(summary = "上传图片")
    @PostMapping(path = "/picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult<UploadResponse> uploadPicture(@RequestParam("picture") MultipartFile picture) {
        return AjaxResult.success(uploadFile(picture, AttachmentTypeEnum.PICTURE));
    }

    private UploadResponse uploadFile(MultipartFile file, AttachmentTypeEnum type) {
        try {
            String hash = BlakeUtils.computeHash(file);
            String objectKey = fileService.checkBlake3(hash);

            if (objectKey == null) {
                objectKey = uploadAndAudit(file, type, hash);
            }

            Long id = fileService.createAttachment(objectKey, type, truncateFilename(file.getOriginalFilename()));
            String url = cubeService.getFileUrl(objectKey, false) + "&attachment_id=" + id;
            return new UploadResponse(true, url);
        } catch (ModerationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(ExceptionEnum.FILE_UPLOAD_ERROR, e);
        }
    }

    private String uploadAndAudit(MultipartFile file, AttachmentTypeEnum type, String hash) throws IOException {
        LocalDate currentDate = LocalDate.now();
        String location = String.format("%d%02d", currentDate.getYear(), currentDate.getMonthValue());

        String objectKey = cubeService.uploadFile(
                file,
                location,
                type == AttachmentTypeEnum.PICTURE,
                true
        );

        // 仅对图片进行内容审核
        if (type == AttachmentTypeEnum.PICTURE) {
            aliyunGreenClient.checkImage(cubeService.getFileUrl(objectKey, false));
        }

        fileService.createFile(objectKey, hash);
        return objectKey;
    }

    private String truncateFilename(String filename) {
        String baseName = FilenameUtils.getBaseName(StringUtils.defaultString(filename));
        return StringUtils.left(baseName, 20);
    }
}
