package com.javaoffers.brief.modelhelper.router.jdbc;

import com.javaoffers.brief.modelhelper.core.BaseSQLInfo;
import com.javaoffers.brief.modelhelper.core.Id;
import com.javaoffers.brief.modelhelper.jdbc.JdbcExecutor;
import com.javaoffers.brief.modelhelper.jdbc.JdbcExecutorMetadata;

import javax.sql.DataSource;
import java.util.List;

/**
 * @description: 执行jdbc操作
 * @author: create by cmj on 2023/7/29 17:47
 */
public class ShardingBriefJdbcExecutor<T> implements JdbcExecutor<T> {

    private JdbcExecutor jdbcExecutor;

    public ShardingBriefJdbcExecutor(JdbcExecutor jdbcExecutor) {
        this.jdbcExecutor = jdbcExecutor;
    }

    @Override
    public Id save(BaseSQLInfo sql) {
        return new ShardingBriefSaveExecutor(this.jdbcExecutor).save(sql);
    }

    @Override
    public List<Id> batchSave(BaseSQLInfo sql) {
        return new ShardingBriefSaveExecutor(this.jdbcExecutor).batchSave(sql);
    }

    @Override
    public int modify(BaseSQLInfo sql) {
        return new ShardingBriefModifyExecutor(this.jdbcExecutor).modify(sql);
    }

    @Override
    public int batchModify(BaseSQLInfo sql) {
        return new ShardingBriefModifyExecutor(this.jdbcExecutor).batchModify(sql);
    }

    @Override
    public T query(BaseSQLInfo sql) {
        return new ShardingBriefQueryExecutor<T>(this.jdbcExecutor).query(sql);
    }

    @Override
    public List<T> queryList(BaseSQLInfo sql) {
        return new ShardingBriefQueryExecutor<T>(this.jdbcExecutor).queryList(sql);
    }

    @Override
    public void queryStream(BaseSQLInfo sql) {
        new ShardingBriefQueryExecutor<T>(this.jdbcExecutor).queryStream(sql);
    }

    @Override
    public JdbcExecutorMetadata getMetadata() {
        return jdbcExecutor.getMetadata();
    }
}
