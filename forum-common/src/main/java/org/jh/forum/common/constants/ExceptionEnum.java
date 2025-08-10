package org.jh.forum.common.constants;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 统一错误码枚举
 *
 * @author Patrick_Star
 * @date 2025/4/6
 */
@Getter
public enum ExceptionEnum {
    INVALID_PARAMETER(200000, "参数错误"),
    RESOURCE_NOT_FOUND(200001, "资源不存在"),
    NOT_LOGIN(200002, "当前未登录或登录过期, 请重新登录"),
    WRONG_USERNAME_OR_PASSWORD(200003, "用户名或密码错误"),
    PERMISSION_NOT_ALLOWED(200004, "权限不足"),
    FILE_NOT_PICTURE(200005, "该文件无法解析为图片"),
    FILE_UPLOAD_ERROR(200006, "文件上传失败"),
    ANNOUNCEMENT_STICKY_LIMIT_REACHED(200007, "公告置顶达到上限"),
    ANNOUNCEMENT_NOT_PUBLISHED(200008, "该公告还未发布"),
    CANNOT_REPORT_YOURSELF(200009, "不能举报自己"),
    POST_PINNED_LIMIT_REACHED(200010, "帖子置顶达到上限"),
    POST_TOPPED_LIMIT_REACHED(200011, "个人主页只能置顶一个帖子"),
    INVALID_URL(200012, "不合法的URL"),
    FILE_SIZE_EXCEEDED(200013, "文件大小超出限制"),
    COMMENT_PINNED_LIMIT_REACHED(200014, "只能置顶一个评论"),
    PARENT_COMMENT_DELETED(200015, "父评论已被删除"),
    REPORT_ALREADY_HANDLED(200016, "该举报已被处理"),
    REPORT_ALREADY_EXISTS(200017, "您已举报过该内容"),
    USER_MUTED(200018, "您已被禁言"),
    OAUTH_CLOSED(200019,"统一身份认证夜间不对外开放"),
    USER_EXISTED(200020,"用户已经存在"),
    OAUTH_NOT_ACTIVATED(200021,"统一账号未激活"),
    NOT_FOUND_ERROR(200404, HttpStatus.NOT_FOUND.getReasonPhrase()),
    SERVER_ERROR(200500, "系统错误, 请稍后重试"),
    ;

    private final Integer errorCode;
    private final String errorMsg;

    ExceptionEnum(Integer errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
}
