package org.jh.forum.common.entity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jh.forum.common.entity.Announcement;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

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
         * 删除公告由MyBatisPlus处理，标记软删除
         */

        /**
         * 根据id查询具体公告
         */
        @Select("SELECT * FROM announcement WHERE id = #{id} AND deleted = false")
        Announcement findByID(@Param("id") Integer id);        /** 
        *   可以分类查询公告
        */
        @Select("SELECT * FROM announcement WHERE type = #{type} AND deleted = false")
        List<Announcement> findByType(@Param("type") Integer type);

        /**
         * 分页查询公告列表（支持类型筛选）
         * 按 updated_at 降序排列，置顶公告优先显示
         */
        @Select("<script>" +
                "SELECT * FROM announcement " +
                "WHERE status = #{status} AND deleted = false " +
                "<if test='type != null'> AND type = #{type} </if>" +
                "ORDER BY sticky DESC, updated_at DESC " +
                "LIMIT #{offset}, #{limit}" +
                "</script>")
        List<Announcement> findAnnouncementsByPage(
                @Param("status") Integer status,
                @Param("type") Integer type,
                @Param("offset") Integer offset,
                @Param("limit") Integer limit
        );

        /**
         * 统计公告总数（支持类型筛选）
         */
        @Select("<script>" +
                "SELECT COUNT(*) FROM announcement " +
                "WHERE status = #{status} AND deleted = false " +
                "<if test='type != null'> AND type = #{type} </if>" +
                "</script>")
        Long countAnnouncements(
                @Param("status") Integer status,
                @Param("type") Integer type
        );

        /**
         * 调试：查询所有公告的状态分布
         */
        @Select("SELECT status, COUNT(*) as count FROM announcement WHERE deleted = false GROUP BY status")
        List<Map<String, Object>> getStatusDistribution();

        /**
         * 调试：查询所有公告的类型分布
         */
        @Select("SELECT type, COUNT(*) as count FROM announcement WHERE deleted = false GROUP BY type")
        List<Map<String, Object>> getTypeDistribution();

        /**
         * 调试：查询最近的10条公告（不限状态）
         */
        @Select("SELECT id, title, status, type, deleted, created_at FROM announcement ORDER BY created_at DESC LIMIT 10")
        List<Map<String, Object>> getRecentAnnouncements();

        /**
         * 调试：统计所有公告数量（按删除状态分组）
         */
        @Select("SELECT deleted, COUNT(*) as count FROM announcement GROUP BY deleted")
        List<Map<String, Object>> getDeletedDistribution();
}
