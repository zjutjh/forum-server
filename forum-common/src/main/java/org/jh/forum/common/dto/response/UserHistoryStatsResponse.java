package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户历史统计响应
 *
 * @author zzb
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserHistoryStatsResponse implements Serializable {
    @Schema(description = "帖子统计")
    private StatDetail post;

    @Schema(description = "评论统计")
    private StatDetail comment;

    @Schema(description = "用户统计")
    private StatDetail user;

    @Schema(description = "总计")
    private StatDetail total;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class StatDetail implements Serializable {
        @Schema(description = "被举报次数")
        private Integer reportCount;

        @Schema(description = "成立次数")
        private Integer establishedCount;

        @Schema(description = "近60天内成立次数")
        private Integer recentEstablishedCount;
    }
}
