package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 用户公告列表查询请求DTO
 * 
 * @author SituChengxiang
 */
@Data
@Schema(description = "管理员公告列表查询请求")
public class UserQueryAnnouncementRequest {

    @Min(value = 1, message = "页码不能小于1")
    @Schema(description = "分页页码", example = "1", defaultValue = "1")
    private Integer page = 1;

    @Min(value = 1, message = "每页数量不能小于1")
    @Max(value = 100, message = "每页数量不能超过100")
    @Schema(description = "每页数量", example = "8", defaultValue = "8")
    private Integer size = 8;

    /**
     * 公告类型筛选：1=系统公告，2=学校公告，3=全部
     */
    @Schema(description = "类型筛选（1=系统公告，2=学校公告，3=全部）", example = "1", defaultValue = "3", allowableValues = {"1", "2", "3"})
    private AnnouncementType type = AnnouncementType.ALL;

    public enum AnnouncementType {
        SYSTEM(1),
        SCHOOL(2),
        ALL(3);

        private final int value;
        AnnouncementType(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }
}