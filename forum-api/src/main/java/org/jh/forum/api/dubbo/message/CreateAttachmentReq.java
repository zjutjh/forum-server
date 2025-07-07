package org.jh.forum.api.dubbo.message;

import lombok.Builder;
import lombok.Data;
import org.jh.forum.common.constants.AttachmentTypeEnum;

/**
 * @author SugarMGP
 */
@Data
@Builder
public class CreateAttachmentReq {
    private Long fileId;
    private AttachmentTypeEnum type;
    private String filename;
}