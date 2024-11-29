package com.javaoffers.brief.modelhelper.router.strategy;

import com.javaoffers.brief.modelhelper.fun.ConditionTag;

import java.util.Collections;
import java.util.List;

public class ShardingTableEqStrategy<T> implements ShardingTableStrategy<T>{

    @Override
    public List<String> shardingTable(String tableName, String colName, ConditionTag conditionTag, List<T> value) {
        return Collections.emptyList();
    }
}
