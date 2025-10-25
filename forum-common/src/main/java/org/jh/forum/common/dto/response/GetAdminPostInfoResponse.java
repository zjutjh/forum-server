package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.PostCategoryEnum;
import org.jh.forum.common.constants.PostStatusEnum;
import org.jh.forum.common.dto.PictureInfoDTO;
import org.jh.forum.common.dto.UserInfoDTO;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理员获取帖子信息
 *
 * @author SugarMGP
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetAdminPostInfoResponse implements Serializable {
    @Schema(description = "发帖人信息")
    private UserInfoDTO publisherInfo;

    @Schema(description = "帖子板块")
    private PostCategoryEnum category;

    @Schema(description = "帖子话题列表")
    private List<String> topics;

    @Schema(description = "帖子标题")
    private String title;

    @Schema(description = "帖子内容")
    private String content;

    @Schema(description = "帖子点赞数")
    private Integer likeCount;

    @Schema(description = "帖子评论数")
    private Integer commentCount;

    @Schema(description = "帖子浏览数")
    private Integer viewCount;

    @Schema(description = "发帖时间")
    private LocalDateTime createdAt;

    @Schema(description = "帖子状态")
    private PostStatusEnum status;

    @Schema(description = "是否置顶（管理员）")
    private Boolean isPinned;

    @Schema(description = "帖子图片列表")
    private List<PictureInfoDTO> pictures;
}
