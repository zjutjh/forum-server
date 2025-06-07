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
         * 根据标题检查是否存在公告（用于创建时查重，自动过滤软删除）
         */
        @Select("SELECT COUNT(*) > 0 FROM announcement WHERE title = #{title} AND deleted = false")
        boolean checkExistsByTitle(@Param("title") String title);

        /**
         * 根据标题检查是否存在公告，排除指定ID（用于编辑时查重）
         */
        @Select("SELECT COUNT(*) > 0 FROM announcement WHERE title = #{title} AND id != #{excludeId} AND deleted = false")
        boolean checkExistsByTitleAndIdNot(@Param("title") String title, @Param("excludeId") Integer excludeId);

        /**
         * 根据ID检查是否存在公告（自动过滤软删除）
         */
        @Select("SELECT COUNT(*) > 0 FROM announcement WHERE id = #{id} AND deleted = false")
        boolean checkExist(@Param("id") Integer id);

        /**
         * 编辑公告相关的内容因为AutoFill直接用MyBatis-Plus搞定
         */
        /**
         * 查询现有的已经被置顶的公告数量
         */
        @Select("SELECT COUNT(*) FROM announcement WHERE sticky = true AND deleted = false")
        int countStickyAnnouncements();

        /**
         * 查询现有的已经被置顶的公告数量（排除指定ID）
         */
        @Select("SELECT COUNT(*) FROM announcement WHERE sticky = true AND id != #{excludeId} AND deleted = false")
        int countStickyAnnouncementsExcludeId(@Param("excludeId") Integer excludeId);

        /**
         * 根据id查询具体公告
         */
        @Select("SELECT * FROM announcement WHERE id = #{id} AND deleted = false")
        Announcement findByID(@Param("id") Integer id);
}
