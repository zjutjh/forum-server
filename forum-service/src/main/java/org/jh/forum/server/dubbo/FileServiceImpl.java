package org.jh.forum.server.dubbo;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.dubbo.service.FileService;
import org.jh.forum.common.constants.AttachmentTypeEnum;
import org.jh.forum.server.manager.FileManager;

import jakarta.annotation.Resource;

/**
 * @author SugarMGP
 */
@DubboService
@Slf4j
public class FileServiceImpl implements FileService {
    @Resource
    private FileManager fileManager;

    @Override
    public String checkBlake3(String blake3) {
        return fileManager.checkBlake3(blake3);
    }

    @Override
    public void createFile(String objectKey, String blake3) {
        fileManager.createFile(objectKey, blake3);
    }

    @Override
    public Long createAttachment(String objectKey, AttachmentTypeEnum type, String filename) {
        return fileManager.createAttachment(objectKey, type, filename);
    }
}
