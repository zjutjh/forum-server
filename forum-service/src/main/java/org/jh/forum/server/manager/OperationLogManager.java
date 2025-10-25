package org.jh.forum.server.manager;

import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.constants.AdminOperationLogTypeEnum;
import org.jh.forum.common.entity.AdminOperationLog;
import org.jh.forum.server.mapper.AdminOperationLogMapper;
import org.springframework.stereotype.Service;

/**
 * @author SugarMGP
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OperationLogManager {
    private final AdminOperationLogMapper adminOperationLogMapper;

    public void addOperationLog(
            AdminOperationLogTypeEnum type,
            String beforeContent,
            String afterContent,
            Long targetId
    ) {
        adminOperationLogMapper.insert(AdminOperationLog.builder()
                .type(type)
                .afterContent(afterContent)
                .beforeContent(beforeContent)
                .userId(StpUtil.getLoginIdAsLong())
                .targetId(targetId)
                .build());
    }
}
