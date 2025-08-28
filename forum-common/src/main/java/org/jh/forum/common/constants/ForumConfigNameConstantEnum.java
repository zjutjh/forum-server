package org.jh.forum.common.constants;


import lombok.Getter;

/**
 * 项目内所有用 nacos 管理的配置名称常量集合
 *
 * @author Patrick_Star
 * @date 2025/04/04
 */
@Getter
public enum ForumConfigNameConstantEnum {
    FORUM_SWITCH("forum-switch", "DEFAULT_GROUP"),
    ADMIN_REGISTER_SWITCH("admin-register-switch", "DEFAULT_GROUP"),
    SUPER_LOGIN_SWITCH("super-login-switch", "DEFAULT_GROUP"),
    ;

    private final String name;

    private final String group;

    ForumConfigNameConstantEnum(String name, String group) {
        this.name = name;
        this.group = group;
    }
}
