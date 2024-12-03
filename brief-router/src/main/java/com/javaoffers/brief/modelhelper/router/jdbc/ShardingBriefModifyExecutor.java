package com.javaoffers.brief.modelhelper.router.jdbc;

import com.javaoffers.brief.modelhelper.core.BaseSQLInfo;
import com.javaoffers.brief.modelhelper.exception.SqlParseException;
import com.javaoffers.brief.modelhelper.jdbc.JdbcExecutor;
import com.javaoffers.brief.modelhelper.jdbc.ModifyExecutor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

/**
 * @description: modify
 * @author: create by cmj on 2023/7/29 18:46
 */
public class ShardingBriefModifyExecutor implements ModifyExecutor {

    JdbcExecutor jdbcExecutor;

    public ShardingBriefModifyExecutor(JdbcExecutor jdbcExecutor) {
        this.jdbcExecutor = jdbcExecutor;
    }

    @Override
    public int modify(BaseSQLInfo sql) {
        return batchModify(sql);
    }

    @Override
    public int batchModify(BaseSQLInfo sql) {
      return 0;
    }

    @Override
    public Connection getConnection() {
        return null;
    }
}
