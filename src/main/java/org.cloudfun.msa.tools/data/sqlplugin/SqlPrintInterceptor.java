package org.cloudfun.msa.tools.data.sqlplugin;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @author sunzhongwen;
 * @Description： sql打印拦截器
 * @date 2018/9/3-15:23;
 */
@Intercepts
        ({
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
                        RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
                @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
        })
public class SqlPrintInterceptor implements Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlPrintInterceptor.class);


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object parameterObject = null;
        if (invocation.getArgs().length > 1) {
            parameterObject = invocation.getArgs()[NumberConstants.N_1];
        }
        BoundSql boundSql;
        int argsLen = NumberConstants.N_5;
        if (invocation.getArgs().length > argsLen) {
            boundSql = (BoundSql) invocation.getArgs()[NumberConstants.N_5];
        } else {
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            boundSql = mappedStatement.getBoundSql(parameterObject);
        }
        long start = System.currentTimeMillis();
        Object result = invocation.proceed();

        long end = System.currentTimeMillis();
        long timing = end - start;
        String sql = boundSql.getSql();
        if (sql != null) {
            sql = sql.replaceAll("\n", "");
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Sql:[{}ms] [{}],param:[{}]", timing,
                    sql, parameterObject == null ? null : parameterObject.toString());
        }

        return result;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
