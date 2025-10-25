package org.jh.forum.common.method;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlInjectionUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 自定义 InsertIgnore 方法
 * 由 MybatisPlus 原有 Insert 方法修改而来
 *
 * @author SugarMGP
 */
public class InsertIgnore extends AbstractMethod {
    private static final String INSERT_IGNORE = "insertIgnore";
    private final boolean ignoreAutoIncrementColumn;

    public InsertIgnore() {
        this(INSERT_IGNORE, false);
    }

    public InsertIgnore(boolean ignoreAutoIncrementColumn) {
        this(INSERT_IGNORE, ignoreAutoIncrementColumn);
    }

    public InsertIgnore(String name) {
        this(name, false);
    }

    public InsertIgnore(String name, boolean ignoreAutoIncrementColumn) {
        super(name);
        this.ignoreAutoIncrementColumn = ignoreAutoIncrementColumn;
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass,
                                                 Class<?> modelClass,
                                                 TableInfo tableInfo) {

        KeyGenerator keyGenerator = NoKeyGenerator.INSTANCE;
        String keyProperty = null;
        String keyColumn = null;

        // 处理主键生成策略
        if (StringUtils.isNotBlank(tableInfo.getKeyProperty())) {
            if (tableInfo.getIdType() == IdType.AUTO) {
                keyGenerator = Jdbc3KeyGenerator.INSTANCE;
                keyProperty = tableInfo.getKeyProperty();
                keyColumn = SqlInjectionUtils.removeEscapeCharacter(tableInfo.getKeyColumn());
            } else if (tableInfo.getKeySequence() != null) {
                // 兼容 Oracle/PG 序列
                keyGenerator = TableInfoHelper.genKeyGenerator(this.methodName, tableInfo, this.builderAssistant);
                keyProperty = tableInfo.getKeyProperty();
                keyColumn = tableInfo.getKeyColumn();
            }
        }

        // 拼接列脚本
        String columnScript = SqlScriptUtils.convertTrim(
                tableInfo.getAllInsertSqlColumnMaybeIf(null, this.ignoreAutoIncrementColumn),
                "(",
                ")",
                null,
                ","
        );

        // 拼接值脚本
        String valuesScript = "(" +
                SqlScriptUtils.convertTrim(
                        tableInfo.getAllInsertSqlPropertyMaybeIf(null, this.ignoreAutoIncrementColumn),
                        null,
                        null,
                        null,
                        ","
                ) +
                ")";

        String sql = String.format("<script>INSERT IGNORE INTO %s %s VALUES %s</script>",
                tableInfo.getTableName(), columnScript, valuesScript);

        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);

        return this.addInsertMappedStatement(
                mapperClass,
                modelClass,
                this.methodName,
                sqlSource,
                keyGenerator,
                keyProperty,
                keyColumn
        );
    }
}