package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.jh.forum.common.constants.GenderEnum;
import org.jh.forum.common.constants.UserTypeEnum;

import java.time.LocalDateTime;


/**
 * @author O v O
 * @TableName user
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor(force = true)
@TableName(value = "user")
public class User extends BaseEntity {
    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 真实姓名
     */
    private String realname;

    /**
     * 学号
     */
    private String studentId;

    /**
     * 密码哈希
     */
    private String password;

    /**
     * 学院ID
     */
    private String collegeId;

    /**
     * 性别(男,女,保密)
     */
    private GenderEnum gender;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 点赞消息开关
     */
    private Boolean upvoteNotice;

    /**
     * 评论消息开关
     */
    private Boolean commentNotice;

    /**
     * 用户角色
     */
    private UserTypeEnum role;

    /**
     * 禁言截止时间
     */
    private LocalDateTime mutedUntil;

    /**
     * 被举报次数
     */
    private Integer reportCount;

    /**
     * 被处理的举报数
     */
    private Integer resolvedReportCount;
}