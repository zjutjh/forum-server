package org.jh.forum.common.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 管理员操作类型枚举
 *
 * @author SugarMGP
 */
@AllArgsConstructor
@Getter
public enum AdminOperationLogTypeEnum {
    DELETE_OR_RESTORE_COMMENT("del_or_restore_comment", "删除或恢复评论"),
    DELETE_POST("delete_post", "删除帖子"),
    RESTORE_POST("restore_post", "恢复帖子"),
    PIN_POST("pin_post", "置顶帖子"),
    CREATE_OR_UPDATE_FAQ("create_or_update_faq", "创建或更新FAQ"),
    DELETE_FAQ("delete_faq", "删除FAQ"),
    MUTE_USER("mute_user", "禁言用户"),
    CREATE_OR_UPDATE_ANNOUNCEMENT("create_or_update_announcement", "创建或更新公告"),
    DELETE_ANNOUNCEMENT("delete_announcement", "删除公告"),
    PIN_ANNOUNCEMENT("pin_announcement", "置顶公告");

    @EnumValue
    @JsonValue
    private final String value;
    private final String desc;
}
