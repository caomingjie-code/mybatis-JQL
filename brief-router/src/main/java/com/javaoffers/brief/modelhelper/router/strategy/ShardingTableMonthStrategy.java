package com.javaoffers.brief.modelhelper.router.strategy;

import com.javaoffers.brief.modelhelper.fun.ConditionTag;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 月份分表. 格式: yyyy-MM .
 * 支持 等值分表.
 * create by cmj
 */
public class ShardingTableMonthStrategy implements ShardingTableStrategy<Date> {

    @Override
    public List<String> shardingTable(String tableName, String colName, ConditionTag conditionTag, List<Date> args) {
        Set<String> set = new TreeSet<>();
        String originTableName = tableName;
        switch (conditionTag) {
            case BETWEEN:
                Date left = args.get(0);
                Date right = args.get(1);
                Calendar instance = Calendar.getInstance();
                instance.setTime(left);
                int leftMonth = instance.get(Calendar.MONTH);
                instance.setTime(right);
                int rightMonth = instance.get(Calendar.MONTH);
                int idx = 0;
                for(int i = leftMonth; i <= rightMonth; i++, idx++) {
                    set.add(originTableName + "_" + DateFormatUtils.format(DateUtils.addMonths(left,idx), "yyyyMM"));
                }
                break;
            case EQ:
                Date t = args.get(0);
                set.add(originTableName + "_" + DateFormatUtils.format(t, "yyyyMM"));
                break;
            case IN:
                for (Date in : args) {
                    set.add(originTableName + "_" + DateFormatUtils.format(in, "yyyyMM"));
                }
                break;
        }
        return new ArrayList<>(set);
    }
}
