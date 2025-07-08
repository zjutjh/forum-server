package org.jh.forum.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jh.forum.common.entity.Comment;

import java.util.List;

/**
 * @author qianqianzyk
 * @description 针对表【comment】的数据库操作Mapper
 * @createDate 2025-06-05 13:46:25
 * @Entity org.jh.forum.common.entity.Comment
 */
public interface CommentMapper extends BaseMapper<Comment> {
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
}
