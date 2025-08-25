package org.jh.forum.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jh.forum.common.entity.PostTopicRelation;

/**
 * @author SugarMGP
 * @description 针对表【post_topic_relation】的数据库操作Mapper
 * @createDate 2025-06-01 17:19:15
 * @Entity org.jh.forum.common.entity.PostTopicRelation
 */
public interface PostTopicRelationMapper extends BaseMapper<PostTopicRelation> {
    int insertIgnore(PostTopicRelation entity);
}




