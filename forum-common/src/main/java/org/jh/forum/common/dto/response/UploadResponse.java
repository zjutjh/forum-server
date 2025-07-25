package org.jh.forum.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author SugarMGP
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UploadResponse {
    private String url;
}
