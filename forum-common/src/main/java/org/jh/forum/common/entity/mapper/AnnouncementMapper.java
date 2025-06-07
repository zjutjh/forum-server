package org.jh.forum.common.entity.mapper;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jh.forum.common.entity.Announcement;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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
    boolean checkExistsByTitleAndIdNot(@Param("title") String title, @Param("excludeId") Integer excludeId);    /**
     * 根据ID检查是否存在公告（自动过滤软删除）
     */
    @Select("SELECT COUNT(*) > 0 FROM announcement WHERE id = #{id} AND deleted = false")
    boolean checkExist(@Param("id") Integer id);/**
     * 编辑基础字段（已发布公告只能编辑这些字段）
     * 允许编辑：title, content, type, attribute, sticky
     * update_uid 和 updated_at 由 AutoFillHandler 自动填充
     */
    @Update("UPDATE announcement SET title = #{title}, content = #{content}, type = #{type}, " +
            "attribute = #{attribute}, sticky = #{sticky} " +
            "WHERE id = #{id} AND deleted = false")
    int updateBasicFields(@Param("id") Integer id, 
                         @Param("title") String title, 
                         @Param("content") String content,
                         @Param("type") Integer type,
                         @Param("attribute") String attribute, 
                         @Param("sticky") Boolean sticky);

    /**
     * 编辑所有字段（草稿和待发布公告可以编辑所有字段）
     * 允许编辑：title, content, type, status, scheduled_at, attribute, sticky
     * update_uid 和 updated_at 由 AutoFillHandler 自动填充
     */
    @Update("UPDATE announcement SET title = #{title}, content = #{content}, type = #{type}, " +
            "status = #{status}, scheduled_at = #{scheduledAt}, attribute = #{attribute}, sticky = #{sticky} " +
            "WHERE id = #{id} AND deleted = false")
    int updateAllFields(@Param("id") Integer id, 
                       @Param("title") String title, 
                       @Param("content") String content,
                       @Param("type") Integer type,
                       @Param("status") Integer status, 
                       @Param("scheduledAt") LocalDateTime scheduledAt,
                       @Param("attribute") String attribute, 
                       @Param("sticky") Boolean sticky);



}
