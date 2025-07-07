package org.jh.forum.api.dubbo.message;

import lombok.Builder;
import lombok.Data;
import org.jh.forum.common.constants.CategoryEnum;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PostListElement {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private Integer likeCount;
    private Integer commentCount;
    private Integer viewCount;
    private CategoryEnum category;
    private List<String> topics;
    private Boolean isTopped;
    private Boolean isPinned;
    private UserInfo userInfo;
    private String status;
}