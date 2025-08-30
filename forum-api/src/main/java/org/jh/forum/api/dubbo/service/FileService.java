package org.jh.forum.api.dubbo.service;

import org.jh.forum.common.constants.AttachmentTypeEnum;

/**
 * 文件服务接口
 *
 * @author SugarMGP
 */
public interface FileService {

    /**
     * 检查文件是否存在
     *
     * @param blake3 文件的 Blake3 哈希值
     * @return 文件的 ObjectKey
     */
    String checkBlake3(String blake3);

    /**
     * 创建文件
     *
     * @param objectKey 文件的 ObjectKey
     * @param blake3    文件的 Blake3 哈希值
     */
    void createFile(String objectKey, String blake3);

    /**
     * 创建附件
     *
     * @param objectKey 附件的 ObjectKey
     * @param type      附件类型
     * @param filename  附件名
     * @return 附件的 ID
     */
    Long createAttachment(String objectKey, AttachmentTypeEnum type, String filename);
}