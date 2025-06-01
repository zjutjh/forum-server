package org.jh.forum.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Date;

@Data
public class EditUserDTO {
    /**
     * 昵称字段
     * 限制10个字符，仅允许中文+数字
     */
    @Size(max = 10, message = "昵称不能超过10个字符")
    private String nickname;

    /**
     * 性别
     * 0-隐藏 1-男 2-女
     */
    private Integer gender;

    //生日
    private Date birthday;

    /**
     * 个性签名
     * 限制20个字符
     */
    private String signature;

    //邮箱
    private String email;

    /**
     * 实名可见性标志
     * true-可见 false-隐藏
     */
    private Boolean realNameVisible;

    /**
     * 学号可见性标志
     * true-可见 false-隐藏
     */
    private Boolean studentIdVisible;

    /**
     * 生日可见性标志
     * true-可见 false-隐藏
     */
    private Boolean birthdayVisible;

    /**
     * 邮箱可见性标志
     * true-可见 false-隐藏
     */
    private Boolean emailVisible;
}