package com.javaoffers.brief.modelhelper.jdbc;

import javax.sql.DataSource;

/**
 * 执行元数据
 */
public class JdbcExecutorMetadata {

    private DataSource dataSource;

    private Class modelClass;

    public JdbcExecutorMetadata() {}

    public JdbcExecutorMetadata(DataSource dataSource, Class modelClass) {
        this.dataSource = dataSource;
        this.modelClass = modelClass;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Class getModelClass() {
        return modelClass;
    }

    public void setModelClass(Class modelClass) {
        this.modelClass = modelClass;
    }
}
