package org.jh.forum.common.dto.response;

import java.util.List;

/**
 * @author SugarMGP
 */
public record TopFivePostList(List<TopFivePostListElement> posts) {
    public record TopFivePostListElement(Long id, String title) {
    }
}
