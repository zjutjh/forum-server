package org.jh.forum.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文本审核服务枚举
 *
 * @author SugarMGP
 */
@Getter
@AllArgsConstructor
public enum TextModerationServiceEnum {
    UGC_LLM("ugc_moderation_byllm", "UGC场景文本审核大模型服务"),
    COMMENT("comment_detection_pro", "公聊评论内容检测_专业版"),
    NICKNAME("nickname_detection_pro", "用户昵称检测_专业版"),
    CHAT("chat_detection_pro", "私聊互动内容检测_专业版");

    private final String serviceName;
    private final String desc;
}
