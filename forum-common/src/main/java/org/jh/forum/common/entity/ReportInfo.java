package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.jh.forum.common.constants.ReportTypeEnum;

/**
 * 举报信息实体类
 *
 * @author zzb
 * @TableName report_info
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "report_info")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ReportInfo extends BaseEntity {
    private Long reportId;

    private Long userId;

    private ReportTypeEnum type;

    private String reason;
}
