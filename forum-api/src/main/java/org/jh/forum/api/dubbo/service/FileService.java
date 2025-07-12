package org.jh.forum.api.dubbo.service;

import org.jh.forum.common.constants.AttachmentTypeEnum;
import org.jh.forum.common.dto.AttachmentInfoDTO;

/**
 * @author SugarMGP
 */
public interface FileService {
    Long checkBlake3(String blake3);

    Long createFile(String objectKey, String blake3);

    Long createAttachment(Long fileId, AttachmentTypeEnum type, String filename);

    AttachmentInfoDTO getAttachmentInfo(Long attachmentId);
}