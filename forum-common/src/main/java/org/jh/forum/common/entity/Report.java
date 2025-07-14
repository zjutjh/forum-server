package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.jh.forum.common.constants.ReportTargetTypeEnum;
import org.jh.forum.common.constants.ReportTypeEnum;

/**
 * @author zzb
 * @TableName report
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "report")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Report extends BaseEntity {
    private Long userId;

    private ReportTargetTypeEnum targetType;

    private Long targetId;

    private ReportTypeEnum type;

    private String reason;

    private String status;

    private String result;
}
