package org.jh.forum.user.repository;

import org.jh.forum.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long userId);
}

@ApiModel(description = "用户资料响应DTO") // 模型描述[6,9](@ref)
public class UserProfileResponse {
    @ApiModelProperty(value = "用户ID", example = "123")
    private Long id;

    @ApiModelProperty(value = "用户名", example = "john_doe")
    private String username;

    @ApiModelProperty(value = "邮箱", example = "user@example.com")
    private String email;
}