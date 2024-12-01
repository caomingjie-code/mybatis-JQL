package com.javaoffers.brief.modelhelper.router.strategy;

import com.javaoffers.brief.modelhelper.context.DeriveProcess;
import com.javaoffers.brief.modelhelper.exception.ShardingConfigExecException;
import com.javaoffers.brief.modelhelper.exception.StopExecException;
import com.javaoffers.brief.modelhelper.parser.SqlParserProcessor;
import com.javaoffers.brief.modelhelper.router.ShardingDeriveFlag;
import com.javaoffers.brief.modelhelper.router.ShardingTableProcess;
import com.javaoffers.brief.modelhelper.router.anno.ShardingStrategy;
import com.javaoffers.brief.modelhelper.utils.TableInfo;
import com.javaoffers.brief.modelhelper.utils.Utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ShardingStrategyParser implements DeriveProcess {
    static Map<Class, ShardingTableStrategy> shardingStrategyMap = new HashMap<>();
    @Override
    public synchronized void processDerive(TableInfo tableInfo, Field colF, String colName) {
        if(colName.contains(")")){
            throw new ShardingConfigExecException(colName + ", function is not supported");
        }
        ShardingStrategy strategy = colF.getDeclaredAnnotation(ShardingStrategy.class);
        if(strategy != null){
            Class<? extends ShardingTableStrategy> tableStrategyClass = strategy.shardingTableStrategy();
            ShardingTableStrategy shardingTableStrategy = shardingStrategyMap.get(tableStrategyClass);

            try {
                if(shardingTableStrategy == null){
                    shardingTableStrategy = tableStrategyClass.newInstance();
                    shardingStrategyMap.put(tableStrategyClass, shardingTableStrategy);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            ShardingTableProcess shardingTableProcess = new ShardingTableProcess();
            shardingTableProcess.setShardingTableStrategy(shardingTableStrategy);
            SqlParserProcessor.SqlParserProcessorBuild build = SqlParserProcessor.builder()
                    .addProcessor(tableInfo.getTableName(), shardingTableProcess)
                    .addColName(tableInfo.getTableName(), colName);
            SqlParserProcessor sqlParserProcessor = build.build();
            ShardingTableColumInfo shardingTableColumInfo = new ShardingTableColumInfo(colName, colF, sqlParserProcessor);
            shardingTableProcess.setShardingTableColumInfo(shardingTableColumInfo);
            tableInfo.putDeriveColName(ShardingDeriveFlag.SHARDING_TABLE, shardingTableColumInfo);
        }
    }
}
