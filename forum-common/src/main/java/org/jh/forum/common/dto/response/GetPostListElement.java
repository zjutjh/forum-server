package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.PostCategoryEnum;
import org.jh.forum.common.dto.PictureInfoDTO;
import org.jh.forum.common.dto.UserInfoDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 获取帖子列表元素
 *
 * @author SugarMGP
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetPostListElement {
    @Schema(description = "帖子ID")
    private Long id;

    @Schema(description = "发帖人信息")
    private UserInfoDTO publisherInfo;

    @Schema(description = "帖子板块")
    private PostCategoryEnum category;

    @Schema(description = "帖子话题列表")
    private List<String> topics;

    @Schema(description = "帖子标题")
    private String title;

    @Schema(description = "帖子内容（截200字）")
    private String content;

    @Schema(description = "帖子点赞数")
    private Integer likeCount;

    @Schema(description = "帖子评论数")
    private Integer commentCount;

    @Schema(description = "发帖时间")
    private LocalDateTime createdAt;

    @Schema(description = "是否置顶")
    private Boolean isPinned;

    @Schema(description = "帖子图片列表（前三张）")
    private List<PictureInfoDTO> pictures;

    @Schema(description = "帖子图片总数")
    private Integer totalPictures;

    @Schema(description = "是否已点赞")
    private Boolean isLiked;
}
