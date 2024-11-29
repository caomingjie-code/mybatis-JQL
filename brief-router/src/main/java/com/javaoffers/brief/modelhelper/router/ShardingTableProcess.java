package com.javaoffers.brief.modelhelper.router;

import com.javaoffers.brief.modelhelper.core.BaseSQLInfo;
import com.javaoffers.brief.modelhelper.fun.ConditionTag;
import com.javaoffers.brief.modelhelper.parser.ColNameProcessorInfo;
import com.javaoffers.brief.modelhelper.parser.ConditionName;
import com.javaoffers.brief.modelhelper.router.strategy.ShardingTableColumInfo;
import com.javaoffers.brief.modelhelper.router.strategy.ShardingTableStrategy;
import com.javaoffers.thrid.jsqlparser.parser.CCJSqlParserConstants;
import com.javaoffers.thrid.jsqlparser.parser.Token;
import com.javaoffers.thrid.jsqlparser.schema.Column;
import com.javaoffers.thrid.jsqlparser.schema.Table;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * create by cmj on 2024-08-09
 */
public class ShardingTableProcess implements Consumer<ColNameProcessorInfo> {

    ShardingTableStrategy shardingTableStrategy;
    ShardingTableColumInfo shardingTableColumInfo;

    @Override
    public void accept(ColNameProcessorInfo colNameProcessorInfo) {
        if (colNameProcessorInfo.getColumnIndex() < 0) {
            return;
        }
        ConditionTag[] values = ConditionTag.values();
        Map<String, ConditionTag> conditionTagMap = new HashMap<>(16);
        for (ConditionTag conditionTag : values) {
            conditionTagMap.put(conditionTag.getTag().replaceAll(" ", "").toLowerCase(), conditionTag);
        }

        BaseSQLInfo sourceSqlInfo = shardingTableColumInfo.getSourceSqlInfo();
        Column column = colNameProcessorInfo.getColumn();
        ConditionTag conditionTag = null;
        if (ConditionName.isWhereOnName(colNameProcessorInfo.getConditionName())) {
            Token token = column.getASTNode().jjtGetLastToken().next;
            StringBuilder condition = new StringBuilder();
            while (token != null) {
                condition.append(token.toString().trim());
                conditionTag = conditionTagMap.get(condition.toString());
                if (conditionTag != null) {
                    break;
                }
                token = token.next;
            }
        }

        int columnIndex = colNameProcessorInfo.getColumnIndex();
        ConditionName conditionName = colNameProcessorInfo.getConditionName();
        String columnName = column.getColumnName();
        List<Object[]> argsParam = sourceSqlInfo.getArgsParam();
        List<Map<String, Object>> params = sourceSqlInfo.getParams();

        for (Object[] arg : argsParam) {
            if (ConditionName.isWhereOnName(conditionName)) {
//            if(column.getTable() != null && StringUtils.isNotBlank(column.getTable().getName())){
//                column.setColumnName("解密("+column.getTable().getName()+"."+columnName.toUpperCase()+")");
//                column.setTable(new Table(""));
//            }else{
//                column.setColumnName("解密("+columnName.toUpperCase()+")");
//            }
                ArrayList<Object> value = new ArrayList<>();
                value.add(arg[columnIndex]);
                shardingTableStrategy.shardingTable(colNameProcessorInfo.getTableName(),
                        columnName, conditionTag, value);


            } else if (ConditionName.VALUES == conditionName) {
                column.setColumnName("加密(" + columnName + ")");
            } else if (ConditionName.UPDATE_SET == conditionName) {
                column.setColumnName("加密(" + columnName + ")");
            }
        }


    }

    public void setShardingTableColumInfo(ShardingTableColumInfo shardingTableColumInfo) {
        this.shardingTableColumInfo = shardingTableColumInfo;
    }

    public void setShardingTableStrategy(ShardingTableStrategy shardingTableStrategy) {
        this.shardingTableStrategy = shardingTableStrategy;
    }
}
