package com.javaoffers.brief.modelhelper.router.jdbc;

import com.javaoffers.brief.modelhelper.jdbc.JdbcExecutor;
import com.javaoffers.brief.modelhelper.jdbc.JdbcExecutorFactory;

import javax.sql.DataSource;

/**
 * @author mingJie
 */
public class ShardingBriefJdbcExecutorFactory implements JdbcExecutorFactory {

    private JdbcExecutorFactory jdbcExecutorFactory;
    public ShardingBriefJdbcExecutorFactory(JdbcExecutorFactory jdbcExecutorFactory) {
        this.jdbcExecutorFactory = jdbcExecutorFactory;
    }
    @Override
    public <T> JdbcExecutor<T> createJdbcExecutor(DataSource dataSource, Class<T> modelClass) {
        return new ShardingBriefJdbcExecutor<T>(jdbcExecutorFactory.createJdbcExecutor(dataSource, modelClass),modelClass);
    }
}
