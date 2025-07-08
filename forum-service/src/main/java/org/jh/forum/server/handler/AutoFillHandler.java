package org.jh.forum.server.handler;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author SugarMGP
 */
@Component
public class AutoFillHandler implements MetaObjectHandler {

    /**
     * 插入填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createUid", Long.class, getCurrentUserId());
        this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, LocalDateTime.now());

        this.strictInsertFill(metaObject, "updateUid", Long.class, getCurrentUserId());
        this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());

        this.strictInsertFill(metaObject, "deleted", Boolean.class, false);
    }

    /**
     * 更新填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateUid", Long.class, getCurrentUserId());
        this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        return StpUtil.isLogin() ? StpUtil.getLoginIdAsLong() : -1L;
    }
}
