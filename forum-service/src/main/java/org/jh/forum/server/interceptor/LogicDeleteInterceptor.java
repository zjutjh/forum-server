package org.jh.forum.server.interceptor;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.Properties;

/**
 * 拦截 MyBatisPlus 编译后的 SQL，临时忽略逻辑删除条件
 * 通过 ThreadLocal 控制作用范围，仅作用于当前线程。
 * 仅在 SELECT 中生效！！！
 *
 * @author SugarMGP
 */
@Slf4j
@Component
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class LogicDeleteInterceptor implements Interceptor {

    // 通过 ThreadLocal 给当前线程打上标识
    private static final ThreadLocal<Boolean> IGNORE_LOGIC_DELETE = new ThreadLocal<>();

    public static void ignore() {
        IGNORE_LOGIC_DELETE.set(Boolean.TRUE);
    }

    public static void clear() {
        IGNORE_LOGIC_DELETE.remove();
    }

    public static boolean isIgnore() {
        return Boolean.TRUE.equals(IGNORE_LOGIC_DELETE.get());
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 判断线程标识
        if (!isIgnore()) {
            return invocation.proceed();
        }

        StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");

        // 只处理 SELECT 语句
        if (!SqlCommandType.SELECT.equals(mappedStatement.getSqlCommandType())) {
            return invocation.proceed();
        }

        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        String originalSql = boundSql.getSql();
        if (originalSql == null || originalSql.isBlank()) {
            return invocation.proceed();
        }

        // 替换 deleted = 0 为 deleted > -1
        String modifiedSql = originalSql.replaceAll("(?i)(\\s|\\()([\\w.]*[`\"]?deleted[`\"]?)\\s*=\\s*0", " deleted>-1 ");
        log.info("[LogicDeleteInterceptor] 原始 SQL: {}", originalSql);
        log.info("[LogicDeleteInterceptor] 修改后 SQL: {}", modifiedSql);

        metaObject.setValue("delegate.boundSql.sql", modifiedSql);
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}

