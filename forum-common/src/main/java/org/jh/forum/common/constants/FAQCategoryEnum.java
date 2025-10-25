package org.jh.forum.common.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * FAQ分类枚举
 *
 * @author ZeroHzzzz
 */
@Getter
@AllArgsConstructor
public enum FAQCategoryEnum {
    ACCOUNT("account", "账号相关问题"),
    REPORT("report", "举报相关问题"),
    POST("post", "帖子相关问题"),
    OTHER("other", "其他问题");

    @JsonValue
    @EnumValue
    private final String value;

    private final String desc;
}
