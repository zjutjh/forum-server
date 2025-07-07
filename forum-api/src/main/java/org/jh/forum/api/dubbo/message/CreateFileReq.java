package org.jh.forum.api.dubbo.message;

import lombok.Builder;
import lombok.Data;

/**
 * @author SugarMGP
 */
@Data
@Builder
public class CreateFileReq {
    private String objectKey;
    private String blake3;
}