package org.jh.forum.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jh.forum.common.entity.Announcement;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 公告数据访问接口（MyBatis-Plus Mapper）
 *
 * @author SituChengxiang
 */
public interface AnnouncementMapper extends BaseMapper<Announcement> {

        /**
         * 删除公告由MyBatisPlus处理，标记软删除
         * 用户可见公告查询已迁移到 Manager 层使用 MyBatis-Plus QueryWrapper 实现
         * 管理员公告查询也是
         */

        /**
         * 手动发布公告（绕过AutoFill机制）
         * 只更新status字段和published字段，不触发AutoFillHandler
         */
        @Update({
                "<script>",
                "UPDATE announcement",
                "SET published_at = scheduled_at,",
                "status = 'published'",
                "WHERE id = #{id} AND status = 'scheduled'",
                "</script>"
        })
        int publishAnnouncementManually(@Param("id") Long id);

}
