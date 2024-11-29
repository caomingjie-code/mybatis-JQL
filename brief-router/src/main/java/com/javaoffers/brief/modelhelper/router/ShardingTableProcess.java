package com.javaoffers.brief.modelhelper.router;

import com.javaoffers.brief.modelhelper.core.BaseSQLInfo;
import com.javaoffers.brief.modelhelper.parser.ColNameProcessorInfo;
import com.javaoffers.brief.modelhelper.parser.ConditionName;
import com.javaoffers.brief.modelhelper.router.strategy.ShardingTableColumInfo;
import com.javaoffers.brief.modelhelper.router.strategy.ShardingTableStrategy;
import com.javaoffers.thrid.jsqlparser.parser.CCJSqlParserConstants;
import com.javaoffers.thrid.jsqlparser.parser.Token;
import com.javaoffers.thrid.jsqlparser.schema.Column;
import com.javaoffers.thrid.jsqlparser.schema.Table;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * create by cmj on 2024-08-09
 */
public class ShardingTableProcess implements Consumer<ColNameProcessorInfo>{

    ShardingTableStrategy shardingTableStrategy;
    ShardingTableColumInfo shardingTableColumInfo;

    @Override
    public void accept(ColNameProcessorInfo colNameProcessorInfo) {
        if(colNameProcessorInfo.getColumnIndex() < 0){
            return;
        }

        BaseSQLInfo sourceSqlInfo = shardingTableColumInfo.getSourceSqlInfo();
        Column column = colNameProcessorInfo.getColumn();
        if(ConditionName.isWhereOnName(colNameProcessorInfo.getConditionName())){
            Token token = column.getASTNode().jjtGetLastToken().next;
            while (token.kind>0){

                System.out.println(CCJSqlParserConstants.tokenImage[token.kind] );
                token = token.next;
            }
        }


            ConditionName conditionName = colNameProcessorInfo.getConditionName();
            String columnName = column.getColumnName();
            List<Object[]> argsParam = sourceSqlInfo.getArgsParam();
            List<Map<String, Object>> params = sourceSqlInfo.getParams();

            for(Object[] arg : argsParam){
                if(ConditionName.isWhereOnName(conditionName)){
//            if(column.getTable() != null && StringUtils.isNotBlank(column.getTable().getName())){
//                column.setColumnName("解密("+column.getTable().getName()+"."+columnName.toUpperCase()+")");
//                column.setTable(new Table(""));
//            }else{
//                column.setColumnName("解密("+columnName.toUpperCase()+")");
//            }


                } else if(ConditionName.VALUES == conditionName) {
                    column.setColumnName("加密("+columnName+")");
                } else if(ConditionName.UPDATE_SET == conditionName){
                    column.setColumnName("加密("+columnName+")");
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
