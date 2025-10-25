package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 前五帖子列表响应
 *
 * @author SugarMGP
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TopFivePostList implements Serializable {

    @Schema(description = "前五帖子列表")
    private List<TopFivePostListElement> posts;

    @AllArgsConstructor
    @Data
    public static class TopFivePostListElement implements Serializable {
        @Schema(description = "帖子ID")
        private Long id;

        @Schema(description = "帖子标题")
        private String title;
    }
}
