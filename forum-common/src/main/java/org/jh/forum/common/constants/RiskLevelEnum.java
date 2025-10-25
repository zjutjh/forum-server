package org.jh.forum.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 审核风险等级枚举
 *
 * @author SugarMGP
 */
@AllArgsConstructor
@Getter
public enum RiskLevelEnum {
    HIGH("high", "高风险"),
    MEDIUM("medium", "中风险"),
    LOW("low", "低风险"),
    NONE("none", "未检测到风险");

    private final String value;
    private final String desc;
}
