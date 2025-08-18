package org.jh.forum.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.jh.forum.common.entity.Comment;

import java.io.Serializable;
import java.util.List;

/**
 * @author qianqianzyk
 * @description 针对表【comment】的数据库操作Mapper
 * @createDate 2025-06-05 13:46:25
 * @Entity org.jh.forum.common.entity.Comment
 */
public interface CommentMapper extends BaseMapper<Comment> {
    @Update("UPDATE comment SET upvote_count = upvote_count + 1 WHERE id = #{commentId}")
    void incrementUpvoteCount(@Param("commentId") Long commentId);

    @Update("UPDATE comment SET upvote_count = upvote_count - 1 WHERE id = #{commentId} AND upvote_count > 0")
    void decrementUpvoteCount(@Param("commentId") Long commentId);

    @Update("UPDATE comment SET reply_count = reply_count + 1 WHERE id = #{commentId}")
    void incrementReplyCount(@Param("commentId") Long commentId);

    @Update("UPDATE comment SET reply_count = reply_count - 1 WHERE id = #{commentId} AND reply_count > 0")
    void decrementReplyCount(@Param("commentId") Long commentId);

    // 获取被删除的评论 ID 或有被删除的子回复的评论 ID
    @Select("""
            SELECT id FROM comment
            WHERE post_id = #{postId}
              AND parent_id = 0
              AND deleted = true
            
            UNION
            
            SELECT id FROM comment
            WHERE post_id = #{postId}
              AND parent_id = 0
              AND deleted = false
              AND id IN (
                  SELECT DISTINCT parent_id FROM comment
                  WHERE deleted = true AND parent_id != 0
              )
            """)
    List<Long> getDeletedOrHasDeletedReplyCommentIds(@Param("postId") Long postId);

    @Update("UPDATE comment SET deleted = false WHERE id = #{id}")
    void restoreComment(@Param("id") Long id);

    @Override
    @Update("UPDATE comment SET deleted = true, is_pinned = false WHERE id = #{id}")
    int deleteById(Serializable id);

    @Select("""
            SELECT COUNT(*)
            FROM comment c
            LEFT JOIN comment p ON c.parent_id = p.id
            WHERE c.post_id = #{postId}
              AND c.deleted = false
              AND (c.parent_id = 0 OR p.deleted = false)
            """)
    Integer selectCommentCount(@Param("postId") Long postId);
}
