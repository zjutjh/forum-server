package org.jh.forum.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.jh.forum.common.entity.Comment;

import java.util.List;

/**
 * @author qianqianzyk
 * @description 针对表【comment】的数据库操作Mapper
 * @createDate 2025-06-05 13:46:25
 * @Entity org.jh.forum.common.entity.Comment
 */
public interface CommentMapper extends BaseMapper<Comment> {
    // 根据回复评论 ID，递归获取其所有未被删除的子回复 ID
    @Select("""
                WITH RECURSIVE comment_tree AS (
                    SELECT #{targetId} AS id
                    UNION ALL
                    SELECT c.id FROM comment c
                    INNER JOIN comment_tree ct ON c.target_id = ct.id
                    WHERE c.deleted = false
                )
                SELECT id FROM comment_tree
            """)
    List<Long> getCommentIdsByTargetId(@Param("targetId") Long rootId);

    // 根据回复评论 ID，递归获取其所有子回复 ID（包括已删除的）
    @Select("""
                WITH RECURSIVE comment_tree AS (
                    SELECT #{targetId} AS id
                    UNION ALL
                    SELECT c.id FROM comment c
                    INNER JOIN comment_tree ct ON c.target_id = ct.id
                )
                SELECT id FROM comment_tree
            """)
    List<Long> getAllCommentIdsByTargetId(@Param("targetId") Long rootId);

    @Update("""
                <script>
                    UPDATE comment
                    SET deleted = false
                    WHERE id IN
                    <foreach collection="commentIds" item="id" open="(" separator="," close=")">
                        #{id}
                    </foreach>
                    AND deleted = true
                </script>
            """)
    void restoreComments(@Param("commentIds") List<Long> commentIds);

    @Update("UPDATE comment SET upvote_count = upvote_count + 1 WHERE id = #{commentId}")
    void incrementUpvoteCount(@Param("commentId") Long commentId);

    @Update("UPDATE comment SET upvote_count = upvote_count - 1 WHERE id = #{commentId} AND upvote_count > 0")
    void decrementUpvoteCount(@Param("commentId") Long commentId);

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
}
