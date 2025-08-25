package org.jh.forum.common.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author SugarMGP
 */
@Getter
@AllArgsConstructor
public enum PostCategoryEnum {
    CAMPUS("campus", "校园日常"),
    EMOTION("emotion", "感情分享"),
    STUDY("study", "学业疑难"),
    CONTEST("contest", "竞赛信息"),
    HOBBY("hobby", "兴趣娱乐"),
    LOST("lost", "失物招领"),
    SECONDHAND("secondhand", "二手闲置");

    @EnumValue
    @JsonValue
    private final String value;

    private final String desc;
}