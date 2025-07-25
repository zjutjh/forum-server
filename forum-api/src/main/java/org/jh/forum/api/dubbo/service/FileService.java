package org.jh.forum.api.dubbo.service;

import org.jh.forum.common.constants.AttachmentTypeEnum;

/**
 * @author SugarMGP
 */
public interface FileService {
    String checkBlake3(String blake3);

    void createFile(String objectKey, String blake3);

    Long createAttachment(String objectKey, AttachmentTypeEnum type, String filename);
}