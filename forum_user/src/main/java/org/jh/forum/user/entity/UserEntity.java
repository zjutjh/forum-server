package org.jh.forum.user.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name = "user")
@Data
public class UserEntity {

    /**
     * 用户主键 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户头像 URL,默认精小红
     */
    private String avatarUrl;

    /**
     * 用户昵称
     * 格式：精小弘+6位随机数字序列，限制10字符
     */
    private String nickname;

    /**
     * 用户真实姓名
     * 可隐藏，展示时根据 realNameVisible 决定是否显示
     */
    private String realName;

    /**
     * 用户学号
     * 可隐藏，展示时根据 studentIdVisible 决定是否显示
     */
    private String studentId;

    /**
     * 性别字段
     * 0-隐藏 1-男 2-女
     */
    private Integer gender;

    /**
     * 用户生日
     * 可隐藏，展示时根据 birthdayVisible 决定是否显示
     */
    private Date birthday;

    /**
     * 个性签名
     * 限制20个字符
     */
    private String signature;

    /**
     * 所属学院
     */
    private String college;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 个人主页背景图 URL
     * 默认主题色
     */
    private String backgroundUrl;

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