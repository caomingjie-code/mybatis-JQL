package com.javaoffers.brief.modelhelper.router.jdbc;

import com.javaoffers.brief.modelhelper.convert.Serializable2IdConvert;
import com.javaoffers.brief.modelhelper.core.BaseSQLInfo;
import com.javaoffers.brief.modelhelper.core.Id;
import com.javaoffers.brief.modelhelper.exception.SqlParseException;
import com.javaoffers.brief.modelhelper.jdbc.JdbcExecutor;
import com.javaoffers.brief.modelhelper.jdbc.SaveExecutor;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: create by cmj on 2023/7/29 18:16
 */
public class ShardingBriefSaveExecutor implements SaveExecutor {

    private JdbcExecutor jdbcExecutor;

    public ShardingBriefSaveExecutor(JdbcExecutor jdbcExecutor) {
        this.jdbcExecutor = jdbcExecutor;
    }

    @Override
    public Id save(BaseSQLInfo sql) {

    }

    @Override
    public List<Id> batchSave(BaseSQLInfo sql)  {

    }

    @Override
    public Connection getConnection() {
        return null;
    }
}
