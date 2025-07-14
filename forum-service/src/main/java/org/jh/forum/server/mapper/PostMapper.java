package org.jh.forum.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.jh.forum.common.entity.Post;

/**
 * @author SugarMGP
 * @description 针对表【post】的数据库操作Mapper
 * @createDate 2025-06-01 17:19:15
 * @Entity org.jh.forum.common.entity.Post
 */
public interface PostMapper extends BaseMapper<Post> {
    @Update("UPDATE post SET view_count = view_count + 1 WHERE id = #{postId}")
    void incrementViewCount(@Param("postId") Long postId);
}




