package org.jh.forum.common.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jh.forum.common.constants.CategoryEnum;

import java.time.LocalDate;

/**
 * @author SugarMGP
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetAdminPostListRequest extends BaseListRequest {
    @Schema(description = "帖子板块")
    private CategoryEnum category;

    @Schema(description = "帖子状态")
    private String status;

    @Schema(description = "帖子标题")
    private String title;

    @Schema(description = "发帖人")
    private String publisher;

    @Schema(description = "发帖日")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("created_day")
    private LocalDate createdDay;
}
