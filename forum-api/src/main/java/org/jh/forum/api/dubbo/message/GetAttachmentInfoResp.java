package org.jh.forum.api.dubbo.message;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetAttachmentInfoResp {
    private String url;
    private String type;
    private String filename;
}