package com.javaoffers.brief.modelhelper.router.strategy;

import com.javaoffers.brief.modelhelper.fun.ConditionTag;

import java.util.List;

/**
 * 分表策略.
 * T: 值的类型
 */
public interface ShardingTableStrategy<T> {
    public List<String> shardingTable(String tableName, String colName, ConditionTag conditionTag, List<T> args);
}
