package org.jh.forum.user.dto;

import lombok.Data;

@Data
public class UserDetailDTO {
    /**
     * 用户ID
     *用于唯一标识用户
     */
    private Long id;

    /**
     * 用户头像 URL
     * 默认为精小弘形象
     */
    private String avatarUrl;

    /**
     * 个人主页背景图 URL
     * 默认为主题色
     */
    private String backgroundUrl;

    /**
     * 用户唯一昵称
     * 格式：精小弘+6位随机数字序列，限制10个字符
     */
    private String nickname;

    /**
     * 用户真实姓名
     * 可隐藏，展示时根据 realNameVisible 决定是否显示
     * 若不可见则返回 null
     */
    private String realName;

    /**
     * 用户学号
     * 可隐藏，展示时根据 studentIdVisible 决定是否显示
     * 若不可见则返回 null
     */
    private String studentId;

    /**
     * 性别字段
     * 0-隐藏 1-男 2-女
     */
    private Integer gender;

    /**
     * 个性签名
     * 限制20个字符
     */
    private String signature;

    //所属学院
    private String college;
}