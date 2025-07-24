package org.jh.forum.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.jh.forum.common.entity.Notice;

import java.util.List;

/**
 * @author lyyzzz
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {
    // 批量更新通知为已读（仅更新未读的）
    @Update("<script>" +
            "UPDATE notice " +
            "SET is_read = 1, updated_at = NOW() " +
            "WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach> " +
            "AND is_read = 0 " +
            "AND deleted = 0" +
            "</script>")
    void batchMarkAsRead(@Param("ids") List<Long> noticeIds);
}

