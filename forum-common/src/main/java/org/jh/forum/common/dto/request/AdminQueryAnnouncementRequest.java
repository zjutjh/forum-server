package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 管理员公告列表查询请求DTO
 * 
 * @author SituChengxiang
 */
@Data
@Schema(description = "管理员公告列表查询请求")
public class AdminQueryAnnouncementRequest {

    @Min(value = 1, message = "页码不能小于1")
    @Schema(description = "分页页码", example = "1", defaultValue = "1")
    private Integer page = 1;

    @Min(value = 1, message = "每页数量不能小于1")
    @Max(value = 100, message = "每页数量不能超过100")
    @Schema(description = "每页数量", example = "8", defaultValue = "8")
    private Integer size = 8;

    /**
     * 公告状态筛选：1=已发布，2=未发布，3=全部
     */
    @Schema(description = "状态筛选（1=已发布，2=未发布，3=全部）", example = "1", defaultValue = "3", allowableValues = {"1", "2", "3"})
    private AnnouncementStatus status = AnnouncementStatus.ALL;

    public enum AnnouncementStatus {
        PUBLISHED(1),
        UNPUBLISHED(2),
        ALL(3);

        private final int value;
        AnnouncementStatus(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }

    @Pattern(regexp = "^(id|created_at|updated_at)$", message = "排序字段只能是id、created_at或updated_at")
    @Schema(description = "排序字段", example = "id", defaultValue = "id", allowableValues = {"id", "created_at", "updated_at"})
    private String orderField = "id";

    /**
     * 排序方向：0=正序，1=逆序
     */
    @Schema(description = "排序方向（0=正序，1=逆序）", example = "0", defaultValue = "0", allowableValues = {"0", "1"})
    private OrderType orderType = OrderType.ASC;

    public enum OrderType {
        ASC(0),
        DESC(1);
        private final int value;
        OrderType(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }

    @Schema(description = "是否查询已删除数据", example = "false", defaultValue = "false")
    private Boolean deleted = false;

    public String orderField() {
        return orderField;
    }

    public int orderType() {
        return orderType.getValue();
    }
}