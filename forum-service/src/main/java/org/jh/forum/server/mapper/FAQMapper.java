package org.jh.forum.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.jh.forum.common.entity.FAQ;

/**
 * FAQ数据访问层
 *
 * @author ZeroHzzzz
 */
public interface FAQMapper extends BaseMapper<FAQ> {
    @Update("UPDATE faq SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(@Param("id") Long id);
}