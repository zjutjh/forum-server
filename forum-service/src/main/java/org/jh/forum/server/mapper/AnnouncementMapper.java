package org.jh.forum.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jh.forum.common.entity.Announcement;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;

/**
 * 公告数据访问接口（MyBatis-Plus Mapper）
 *
 * @author SituChengxiang
 */
public interface AnnouncementMapper extends BaseMapper<Announcement> {



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
        * 删除公告由MyBatisPlus处理，标记软删除
        */
        /**
         * 查询用户可见的公告列表（支持类型筛选和分页）
         * 按 sticky 字段降序（置顶优先），然后按 updated_at 字段降序
         * 只查询已发布状态（status=1）的公告，确保用户不会看到草稿或待发布公告
         * 
         * @param type 公告类型（可选筛选）
         * @param offset 分页偏移量
         * @param limit 每页记录数
         * @return 用户可见的公告列表
         */
        @Select("<script>" +
                        "SELECT * FROM announcement " +
                        "WHERE status = 1 AND deleted = false " +
                        "<if test='type != null'> AND type = #{type} </if>" +
                        "ORDER BY sticky DESC, updated_at DESC " +
                        "LIMIT #{offset}, #{limit}" +
                        "</script>")
        List<Announcement> findAnnouncementsForUser(
                        @Param("type") Integer type,
                        @Param("offset") Integer offset,
                        @Param("limit") Integer limit);

        /**
         * 统计用户可见的公告总数（支持类型筛选）
         * 只统计已发布状态（status=1）的公告
         * 
         * @param type 公告类型（可选筛选）
         * @return 符合条件的公告总数
         */
        @Select("<script>" +
                        "SELECT COUNT(*) FROM announcement " +
                        "WHERE status = 1 AND deleted = false " +
                        "<if test='type != null'> AND type = #{type} </if>" +
                        "</script>")
        Long countAnnouncementsForUser(
                        @Param("type") Integer type);

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
