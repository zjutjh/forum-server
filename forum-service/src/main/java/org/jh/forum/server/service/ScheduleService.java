package org.jh.forum.server.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.jh.forum.common.entity.Announcement;
import org.jh.forum.server.manager.AnnouncementManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * 公告定时发布服务
 * 负责处理定时发布公告的任务
 * 
 * @author SituChengxiang
 */
@Slf4j
@Component
public class ScheduleService {

    @Resource
    private AnnouncementManager announcementManager;

    /**
     * 定时检查并发布到期的公告
     * 每小时执行一次
     */
    @Scheduled(fixedRate = 3600000)

    public void publishExpiredAnnouncements() {
        try {
            log.debug("开始执行定时发布公告任务，当前时间：{}", LocalDateTime.now());
            
            // 1. 查询到期的待发布公告
            List<Announcement> expiredAnnouncements = announcementManager.findExpiredScheduledAnnouncements();
            
            if (expiredAnnouncements.isEmpty()) {
                log.debug("没有到期的待发布公告");
                return;
            }
            
            // 2. 提取公告ID列表
            List<Long> announcementIds = expiredAnnouncements.stream()
                    .map(Announcement::getId)
                    .collect(Collectors.toList());
            
            log.info("发现{}个到期的待发布公告，准备发布，ID列表：{}", expiredAnnouncements.size(), announcementIds);
            
            // 3. 批量发布公告
            int publishedCount = announcementManager.batchPublishExpiredAnnouncements(announcementIds);
            
            if (publishedCount > 0) {
                log.info("定时发布任务完成，成功发布{}个公告", publishedCount);
            }
            
        } catch (Exception e) {
            log.error("定时发布公告任务执行失败", e);
        }
    }

    /**
     * 手动触发定时发布任务（用于测试）
     * 可以通过管理接口调用
     */
    @Deprecated
        public void manualTriggerPublish() {
            log.info("手动触发定时发布任务");
            publishExpiredAnnouncements();
        }
}
