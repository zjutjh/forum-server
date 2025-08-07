package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.jh.forum.common.constants.HandleReportEnum;
import org.jh.forum.common.constants.ReportStatusEnum;
import org.jh.forum.common.constants.ReportTypeEnum;
import org.jh.forum.common.constants.TargetTypeEnum;

/**
 * @author zzb
 * @TableName report
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "report")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Report extends BaseEntity {
    private Long userId;

    private Long targetUserId;

    private TargetTypeEnum targetType;

    private Long targetId;

    private ReportTypeEnum type;

    private String reason;

    private ReportStatusEnum status;

    private String result;

    private Long reviewerId;

    private Boolean shouldDelete;

    private HandleReportEnum punishmentType;

    private Integer muteDays;
}
