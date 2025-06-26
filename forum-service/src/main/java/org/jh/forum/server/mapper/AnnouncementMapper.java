package org.jh.forum.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jh.forum.common.entity.Announcement;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.time.LocalDateTime;
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
        boolean checkExistsByTitleAndIdNot(@Param("title") String title, @Param("excludeId") Long excludeId);

        /**
         * 根据ID检查是否存在公告（自动过滤软删除）
         */
        @Select("SELECT COUNT(*) > 0 FROM announcement WHERE id = #{id} AND deleted = false")
        boolean checkExist(@Param("id") Long id);

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
        int countStickyAnnouncementsExcludeId(@Param("excludeId") Long excludeId);

        /**
         * 删除公告由MyBatisPlus处理，标记软删除
         */

        /**
         * 根据id查询具体公告
         */
        @Select("SELECT * FROM announcement WHERE id = #{id} AND deleted = false")
        Announcement findByID(@Param("id") Long id);

        /**
         * 可以分类查询公告
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
                        @Param("limit") Integer limit);

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
                        @Param("type") Integer type);

        /**
         * 统计指定状态的公告总数（管理员查询用，支持查询已删除数据）
         */
        @Select("<script>" +
                        "SELECT COUNT(*) FROM announcement " +
                        "WHERE status = #{status} " +
                        "<if test='includeDeleted == false'> AND deleted = false </if>" +
                        "</script>")
        Long countAnnouncementsByStatusForAdmin(
                        @Param("status") Integer status,
                        @Param("includeDeleted") Boolean includeDeleted);

        /**
         * 查询已发布的公告并支持升序/降序排序
         * 按 updated_at 字段排序，默认升序
         * 
         * @param orderType 排序方向：0=升序(ASC)，1=降序(DESC)，null=默认升序
         * @param offset 分页偏移量
         * @param limit 每页记录数
         * @return 已发布的公告列表
         */
        @Select("<script>" +
                        "SELECT * FROM announcement " +
                        "WHERE status = 1 AND deleted = false " +
                        "ORDER BY updated_at " +
                        "<choose>" +
                        "  <when test='orderType != null and orderType == 1'>DESC</when>" +
                        "  <otherwise>ASC</otherwise>" +
                        "</choose> " +
                        "LIMIT #{offset}, #{limit}" +
                        "</script>")
        List<Announcement> findPublishedAnnouncements(
                        @Param("orderType") Integer orderType,
                        @Param("offset") Integer offset,
                        @Param("limit") Integer limit);


         /**
         * 查询待定时发布的公告并支持升序/降序排序
         * 按 scheduled_at 字段排序，默认升序
         * 
         * @param orderType 排序方向：0=升序(ASC)，1=降序(DESC)，null=默认升序
         * @param offset 分页偏移量
         * @param limit 每页记录数
         * @return 待发布的公告列表
         */
        @Select("<script>" +
                        "SELECT * FROM announcement " +
                        "WHERE status = 2 AND deleted = false " +
                        "ORDER BY scheduled_at " +
                        "<choose>" +
                        "  <when test='orderType != null and orderType == 1'>DESC</when>" +
                        "  <otherwise>ASC</otherwise>" +
                        "</choose> " +
                        "LIMIT #{offset}, #{limit}" +
                        "</script>")
        List<Announcement> findPScheduledAnnouncements(
                        @Param("orderType") Integer orderType,
                        @Param("offset") Integer offset,
                        @Param("limit") Integer limit);

        
         /**
         * 查询草稿公告并支持升序/降序排序
         * 按 updated_at 字段排序，默认升序
         * 
         * @param orderType 排序方向：0=升序(ASC)，1=降序(DESC)，null=默认升序
         * @param offset 分页偏移量
         * @param limit 每页记录数
         * @return 草稿公告列表
         */
        @Select("<script>" +
                        "SELECT * FROM announcement " +
                        "WHERE status = 0 AND deleted = false " +
                        "ORDER BY updated_at " +
                        "<choose>" +
                        "  <when test='orderType != null and orderType == 1'>DESC</when>" +
                        "  <otherwise>ASC</otherwise>" +
                        "</choose> " +
                        "LIMIT #{offset}, #{limit}" +
                        "</script>")
        List<Announcement> findDraftAnnouncements(
                        @Param("orderType") Integer orderType,
                        @Param("offset") Integer offset,
                        @Param("limit") Integer limit);                
        


        /**
         * 查询到期的待发布公告
         * 条件：status=2 AND scheduled_at <= 当前时间 AND deleted=false
         */
        @Select("SELECT * FROM announcement " +
                        "WHERE status = 2 AND scheduled_at <= #{currentTime} AND deleted = false " +
                        "ORDER BY scheduled_at ASC")
        List<Announcement> findExpiredScheduledAnnouncements(@Param("currentTime") LocalDateTime currentTime);

        /**
         * 手动发布公告（绕过AutoFill机制）
         * 只更新status字段，不触发AutoFillHandler
         */
        @Update("UPDATE announcement SET status = 1 WHERE id = #{id} AND status = 2")
        int publishAnnouncementManually(@Param("id") Long id);

        /**
         * 查询已发布的公告并支持升序/降序排序（管理员版，支持查询已删除）
         */
        @Select("<script>" +
                        "SELECT * FROM announcement " +
                        "WHERE status = 1 " +
                        "<if test='includeDeleted == false'> AND deleted = false </if>" +
                        "ORDER BY updated_at " +
                        "<choose>" +
                        "  <when test='orderType != null and orderType == 1'>DESC</when>" +
                        "  <otherwise>ASC</otherwise>" +
                        "</choose> " +
                        "LIMIT #{offset}, #{limit}" +
                        "</script>")
        List<Announcement> findPublishedAnnouncementsForAdmin(
                        @Param("orderType") Integer orderType,
                        @Param("includeDeleted") Boolean includeDeleted,
                        @Param("offset") Integer offset,
                        @Param("limit") Integer limit);

        /**
         * 查询待发布的公告并支持升序/降序排序（管理员版，支持查询已删除）
         */
        @Select("<script>" +
                        "SELECT * FROM announcement " +
                        "WHERE status = 2 " +
                        "<if test='includeDeleted == false'> AND deleted = false </if>" +
                        "ORDER BY scheduled_at " +
                        "<choose>" +
                        "  <when test='orderType != null and orderType == 1'>DESC</when>" +
                        "  <otherwise>ASC</otherwise>" +
                        "</choose> " +
                        "LIMIT #{offset}, #{limit}" +
                        "</script>")
        List<Announcement> findScheduledAnnouncementsForAdmin(
                        @Param("orderType") Integer orderType,
                        @Param("includeDeleted") Boolean includeDeleted,
                        @Param("offset") Integer offset,
                        @Param("limit") Integer limit);

        /**
         * 查询草稿公告并支持升序/降序排序（管理员版，支持查询已删除）
         */
        @Select("<script>" +
                        "SELECT * FROM announcement " +
                        "WHERE status = 0 " +
                        "<if test='includeDeleted == false'> AND deleted = false </if>" +
                        "ORDER BY updated_at " +
                        "<choose>" +
                        "  <when test='orderType != null and orderType == 1'>DESC</when>" +
                        "  <otherwise>ASC</otherwise>" +
                        "</choose> " +
                        "LIMIT #{offset}, #{limit}" +
                        "</script>")
        List<Announcement> findDraftAnnouncementsForAdmin(
                        @Param("orderType") Integer orderType,
                        @Param("includeDeleted") Boolean includeDeleted,
                        @Param("offset") Integer offset,
                        @Param("limit") Integer limit);
}
