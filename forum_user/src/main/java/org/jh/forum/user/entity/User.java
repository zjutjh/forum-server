package org.jh.forum.user.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "forum_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private String avatarUrl;
    private String bio;
    private LocalDateTime createdAt;

    // Getters and setters

    // 隐私设置应该作为单独实体实现，这里简化处理
}