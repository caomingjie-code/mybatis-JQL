package com.javaoffers.brief.modelhelper.sqlite;

import com.javaoffers.brief.modelhelper.fun.condition.where.LimitWordCondition;

/**
 * cmj
 */
public class SqliteLimitWordCondition extends LimitWordCondition {

    public SqliteLimitWordCondition(int pageNum, int pageSize) {
        super(pageNum, pageSize);
    }

    @Override
    public String getSql() {
        String startIndexTag = getNextTag();
        String lenTag = getNextTag();
        this.getParams().put(startIndexTag, super.startIndex);
        this.getParams().put(lenTag, super.len);
        return getTag().getTag() +" #{"+lenTag+"} offset #{"+startIndexTag+"}";
    }

    @Override
    public String cleanLimit(String limitSql) {
        String token = getTag().getTag() +" ? offset ?";
        return limitSql = limitSql.substring(0, limitSql.length() - token.length());
    }
}
