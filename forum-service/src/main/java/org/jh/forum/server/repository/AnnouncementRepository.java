package org.jh.forum.server.repository;

import java.util.List;
import java.util.Optional;

import org.jh.forum.server.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 公告数据访问接口（JPA Repository）
 *
 * @author SituChengxiang
 */
@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Integer> {

    // 保存自带save()方法了，纯新增的时候就这么用了

    // 查询所有未软删除的公告（自动通过 @Where 过滤）
    @Override
    List<Announcement> findAll();

    // 根据 ID 查询未软删除的公告（自动通过 @Where 过滤）
    Optional<Announcement> findById(Integer id);

    // 根据状态查询公告（自动过滤软删除）
    List<Announcement> findByStatus(Integer status);

    // 根据创建人 ID 查询公告（自动过滤软删除）
    List<Announcement> findByCreatorId(Integer creatorId);

    // 强制查询所有公告（包括软删除）
    @Query("SELECT a FROM Announcement a")
    List<Announcement> findAllIncludingDeleted();

    // 强制删除（硬删除）
    @Modifying
    @Query("DELETE FROM Announcement WHERE id = ?1")
    void hardDeleteById(Integer id);
}