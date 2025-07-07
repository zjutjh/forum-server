package org.jh.forum.api.dubbo.service;

import org.jh.forum.api.dubbo.message.CreateAttachmentReq;
import org.jh.forum.api.dubbo.message.CreateFileReq;
import org.jh.forum.common.dto.response.GetAttachmentInfoResponse;

/**
 * @author SugarMGP
 */
public interface FileService {
    Long checkBlake3(String blake3);

    Long createFile(CreateFileReq request);

    Long createAttachment(CreateAttachmentReq request);

    GetAttachmentInfoResponse getAttachmentInfo(Long attachmentId);
}