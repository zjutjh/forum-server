package org.jh.forum.user.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserProfileResponse {
    private Long id;
    private String username;
    private String avatarUrl;
    private LocalDateTime joinDate;

    // 隐私控制字段
    private String email;
    private String bio;

    // 帖子信息
    private List<PostInfo> posts;

    // 粉丝信息
    private List<FollowerInfo> followers;

    @Data
    public static class PostInfo {
        private Long id;
        private String title;
        private String preview;
        private LocalDateTime createdAt;
    }

    @Data
    public static class FollowerInfo {
        private Long id;
        private String username;
        private String avatarUrl;
    }
}