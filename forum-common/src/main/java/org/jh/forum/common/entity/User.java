package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import jakarta.validation.constraints.Pattern;


/**
 * @author O v O
 * @TableName user
 */
@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
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
    private Integer collegeId;

    /**
     * 性别(男,女,保密)
     */
    @Pattern(regexp = "^(男|女|保密)$", message = "性别不合法")
    private String gender;

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
    private Integer upvoteNotice;

    /**
     * 评论消息开关
     */
    private Integer commentNotice;

    /**
     * 用户角色
     */
    private String role;

}