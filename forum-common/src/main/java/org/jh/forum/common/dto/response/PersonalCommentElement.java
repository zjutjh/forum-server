package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.dto.PictureInfoDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author qianqianzyk
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PersonalCommentElement {
    @Schema(description = "帖子ID")
    private Long postId;

    @Schema(description = "帖子标题")
    private String title;

    @Schema(description = "帖子正文（截取50字）")
    private String content;

    @Schema(description = "帖子附件列表")
    private List<PictureInfoDTO> pictures;

    @Schema(description = "帖子创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "帖子更新时间")
    private LocalDateTime updatedAt;

    @Schema(description = "个人评论列表")
    private List<PersonalCommentListElement> personalCommentList;
}