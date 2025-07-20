package org.jh.forum.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zzb
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserHistoryStatsResponse {
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
    public static class StatDetail {
        @Schema(description = "被举报次数")
        @JsonProperty("report_count")
        private Integer reportCount;

        @Schema(description = "成立次数")
        @JsonProperty("established_count")
        private Integer establishedCount;

        @Schema(description = "近60天内成立次数")
        @JsonProperty("recent_established_count")
        private Integer recentEstablishedCount;
    }

}
