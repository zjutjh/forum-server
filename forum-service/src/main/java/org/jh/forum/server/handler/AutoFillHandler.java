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
        this.setFieldValByName("createUid", getCurrentUserId(), metaObject);
        this.setFieldValByName("createdAt", LocalDateTime.now(), metaObject);

        this.setFieldValByName("updateUid", getCurrentUserId(), metaObject);
        this.setFieldValByName("updatedAt", LocalDateTime.now(), metaObject);

        this.setFieldValByName("deleted", Boolean.FALSE, metaObject);
    }

    /**
     * 更新填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateUid", getCurrentUserId(), metaObject);
        this.setFieldValByName("updatedAt", LocalDateTime.now(), metaObject);
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        return StpUtil.isLogin() ? StpUtil.getLoginIdAsLong() : -1L;
    }
}
