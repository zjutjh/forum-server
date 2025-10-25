package org.jh.forum.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.yitter.idgen.YitIdHelper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.jh.forum.common.entity.Upvote;

/**
 * @author qianqianzyk
 * @description 针对表【upvote】的数据库操作Mapper
 * @createDate 2025-06-07 22:43:32
 * @Entity org.jh.forum.common.entity.Upvote
 */
public interface UpvoteMapper extends BaseMapper<Upvote> {
    /**
     * 不推荐直接使用该方法，建议使用 updatePostUpvote 或 updateCommentUpvote
     *
     * @see UpvoteMapper#updatePostUpvote
     * @see UpvoteMapper#updateCommentUpvote
     */
    @Update("""
            INSERT INTO upvote(id, user_id, post_id, comment_id, status, created_at, updated_at, create_uid, update_uid, deleted)
            VALUES(#{id}, #{userId}, #{postId}, #{commentId}, TRUE, CURRENT_TIMESTAMP(3), CURRENT_TIMESTAMP(3), #{userId}, #{userId}, FALSE)
            ON DUPLICATE KEY UPDATE
                status = NOT status,
                updated_at = CURRENT_TIMESTAMP(3)
            """)
    void updateUpvote(@Param("id") Long id, @Param("userId") Long userId, @Param("postId") Long postId, @Param("commentId") Long commentId);

    default void updatePostUpvote(Long userId, Long postId) {
        updateUpvote(YitIdHelper.nextId(), userId, postId, null);
    }

    default void updateCommentUpvote(Long userId, Long commentId) {
        updateUpvote(YitIdHelper.nextId(), userId, null, commentId);
    }
}
