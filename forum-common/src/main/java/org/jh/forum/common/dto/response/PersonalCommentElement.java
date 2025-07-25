package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.dto.PictureInfoDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    private Long postId;

    @Schema(description = "帖子标题")
    @NotBlank
    private String title;

    @Schema(description = "帖子正文（截取50字）")
    @NotBlank
    private String content;

    @Schema(description = "帖子附件列表")
    @NotBlank
    private List<PictureInfoDTO> pictures;

    @Schema(description = "帖子创建时间")
    @NotBlank
    private LocalDateTime createdAt;

    @Schema(description = "帖子更新时间")
    @NotBlank
    private LocalDateTime updatedAt;

    @Schema(description = "个人评论列表")
    @NotNull
    private List<PersonalCommentListElement> personalCommentList;
}