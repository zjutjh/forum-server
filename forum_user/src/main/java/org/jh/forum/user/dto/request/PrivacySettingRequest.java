package org.jh.forum.user.dto.request;

import lombok.Data;

@Data
public class PrivacySettingRequest {
    private Boolean showEmail = true;
    private Boolean showPosts = true;
    private Boolean showFavorites = true;
    private Boolean showElitePosts = true;
    private Boolean showFollowers = true;
}