package com.javaoffers.brief.modelhelper.sqlserver;

import com.javaoffers.brief.modelhelper.fun.condition.where.LimitWordCondition;

/**
 *
 *
 */
public class SqlServerLimitWordCondition extends LimitWordCondition {

    public SqlServerLimitWordCondition(int pageNum, int pageSize) {
        super(pageNum, pageSize);
    }

    @Override
    public String getSql() {
        String startIndexTag = getNextTag();
        String lenTag = getNextTag();
        this.getParams().put(startIndexTag, super.startIndex);
        this.getParams().put(lenTag, super.len);
        // A 是表的别名
        return " OFFSET  #{" + startIndexTag + "} ROWS FETCH NEXT #{" + lenTag + "} ROWS ONLY";
    }

    @Override
    public String cleanLimit(String limitSql) {
        String token = " OFFSET  ? ROWS FETCH NEXT ? ROWS ONLY";
        return limitSql.substring(0,limitSql.length() - token.length());
    }
}
