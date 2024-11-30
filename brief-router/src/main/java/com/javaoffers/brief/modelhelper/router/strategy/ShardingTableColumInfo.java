package com.javaoffers.brief.modelhelper.router.strategy;

import com.javaoffers.brief.modelhelper.anno.derive.flag.DeriveInfo;
import com.javaoffers.brief.modelhelper.core.BaseSQLInfo;
import com.javaoffers.brief.modelhelper.parser.SqlParserProcessor;
import com.javaoffers.brief.modelhelper.utils.Lists;
import com.javaoffers.thrid.jsqlparser.JSQLParserException;

import java.lang.reflect.Field;
import java.util.List;

public class ShardingTableColumInfo extends DeriveInfo {

    private SqlParserProcessor sqlParserProcessor;
    private ThreadLocal task = new ThreadLocal();

    public ShardingTableColumInfo(String colName, Field field, SqlParserProcessor sqlParserProcessor) {
        super(colName, field);
        this.sqlParserProcessor = sqlParserProcessor;

    }

    public List<BaseSQLInfo> shardingParse(BaseSQLInfo sqlInfo){
        String sql = sqlInfo.getSql();
        try {
            task.set(sqlInfo);
            sqlParserProcessor.parseSql(sql);
            List<BaseSQLInfo> sharding = (List)task.get();
            if(sharding == null || sharding.size() == 0){
                return Lists.newArrayList(sqlInfo);
            }
            return sharding;
        } catch (JSQLParserException e) {
            throw new RuntimeException(e);
        }finally {
            task.remove();
        }
    }

    public BaseSQLInfo getSourceSqlInfo(){
        return (BaseSQLInfo)task.get();
    }

    public void setShardingSqlInfo(List<BaseSQLInfo> sqlInfos){
        task.set(sqlInfos);
    }
}
