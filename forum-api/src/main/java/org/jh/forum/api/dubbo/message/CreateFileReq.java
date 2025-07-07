package org.jh.forum.api.dubbo.message;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateFileReq {
    private String objectKey;
    private String blake3;
}