package org.jh.forum.common.entity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jh.forum.common.entity.Announcement;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 公告数据访问接口（MyBatis-Plus Mapper）
 *
 * @author SituChengxiang
 */
public interface AnnouncementMapper extends BaseMapper<Announcement> {

    /**
     * 根据标题检查是否存在公告（用于查重，自动过滤软删除）
     */
    @Select("SELECT COUNT(*) > 0 FROM announcement WHERE title = #{title} AND deleted = false")
    boolean existsByTitle(@Param("title") String title);

    /**
     * 根据标题检查是否存在公告，排除指定ID（用于编辑时查重）
     */
    @Select("SELECT COUNT(*) > 0 FROM announcement WHERE title = #{title} AND id != #{excludeId} AND deleted = false")
    boolean existsByTitleAndIdNot(@Param("title") String title, @Param("excludeId") Integer excludeId);
}
