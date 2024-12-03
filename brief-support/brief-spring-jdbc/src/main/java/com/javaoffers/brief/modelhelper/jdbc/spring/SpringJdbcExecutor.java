package com.javaoffers.brief.modelhelper.jdbc.spring;

import com.javaoffers.brief.modelhelper.core.BaseSQLInfo;
import com.javaoffers.brief.modelhelper.core.Id;
import com.javaoffers.brief.modelhelper.jdbc.*;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author mingJie
 */
public class SpringJdbcExecutor<T> implements JdbcExecutor<T> {
    DataSource dataSource;

    Class<T> modelClass;

    JdbcExecutorMetadata jdbcExecutorMetadata;

    public SpringJdbcExecutor(DataSource dataSource, Class modelClass) {
        this.dataSource = dataSource;
        this.modelClass = modelClass;
        this.jdbcExecutorMetadata = new JdbcExecutorMetadata(dataSource, modelClass);
    }

    @Override
    public Id save(BaseSQLInfo sql) {
        return new SpringSaveExecutor(dataSource).save(sql);
    }

    @Override
    public List<Id> batchSave(BaseSQLInfo sql) {
        return new SpringSaveExecutor(dataSource).batchSave(sql);
    }

    @Override
    public int modify(BaseSQLInfo sql) {
        return new SpringModifyExecutor(dataSource).modify(sql);
    }

    @Override
    public int batchModify(BaseSQLInfo sql) {
        return new SpringModifyExecutor(dataSource).batchModify(sql);
    }

    @Override
    public T query(BaseSQLInfo sql) {
        return new SpringQueryExecutor<T>(dataSource, modelClass).query(sql);
    }

    @Override
    public List<T> queryList(BaseSQLInfo sql) {
        return new SpringQueryExecutor<T>(dataSource, modelClass).queryList(sql);
    }

    @Override
    public void queryStream(BaseSQLInfo sql) {
        new SpringQueryExecutor<T>(dataSource, modelClass).queryStream(sql);
    }

    @Override
    public JdbcExecutorMetadata getMetadata() {
        return this.jdbcExecutorMetadata;
    }


}
