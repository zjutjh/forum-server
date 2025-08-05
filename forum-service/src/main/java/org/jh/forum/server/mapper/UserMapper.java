package org.jh.forum.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.jh.forum.common.entity.User;

/**
 * @author MangoGovo
 */
public interface UserMapper extends BaseMapper<User> {
    @Update("UPDATE user SET report_count = report_count + 1 WHERE id = #{userId}")
    void incrementReportCount(@Param("userId") Long userId);

    @Update("UPDATE user SET resolved_report_count = resolved_report_count + 1 WHERE id = #{userId}")
    void incrementResolvedReportCount(@Param("userId") Long userId);
}
