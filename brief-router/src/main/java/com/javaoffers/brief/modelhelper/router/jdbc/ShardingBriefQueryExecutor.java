package com.javaoffers.brief.modelhelper.router.jdbc;

import com.javaoffers.brief.modelhelper.core.BaseSQLInfo;
import com.javaoffers.brief.modelhelper.core.Limit;
import com.javaoffers.brief.modelhelper.exception.ParseResultSetException;
import com.javaoffers.brief.modelhelper.exception.SqlParseException;
import com.javaoffers.brief.modelhelper.exception.StopExecException;
import com.javaoffers.brief.modelhelper.jdbc.BriefResultSetExecutor;
import com.javaoffers.brief.modelhelper.jdbc.JdbcExecutor;
import com.javaoffers.brief.modelhelper.jdbc.JdbcExecutorMetadata;
import com.javaoffers.brief.modelhelper.jdbc.QueryExecutor;
import com.javaoffers.brief.modelhelper.log.JqlLogger;
import com.javaoffers.brief.modelhelper.parse.ModelParseUtils;
import com.javaoffers.brief.modelhelper.router.ShardingDeriveFlag;
import com.javaoffers.brief.modelhelper.router.strategy.ShardingTableColumInfo;
import com.javaoffers.brief.modelhelper.utils.Lists;
import com.javaoffers.brief.modelhelper.utils.TableHelper;
import com.javaoffers.brief.modelhelper.utils.TableInfo;
import org.apache.commons.collections4.CollectionUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description:
 * @author: create by cmj on 2023/7/29 18:48
 */
public class ShardingBriefQueryExecutor<T> implements QueryExecutor<T> {

    JdbcExecutor jdbcExecutor;
    JdbcExecutorMetadata metadata;
    TableInfo tableInfo;
    ShardingTableColumInfo stc ;
    boolean sharding = false;

    public ShardingBriefQueryExecutor(JdbcExecutor jdbcExecutor) {
        this.jdbcExecutor = jdbcExecutor;
        this.metadata = this.jdbcExecutor.getMetadata();
        this.tableInfo = TableHelper.getTableInfo(metadata.getModelClass());
        this.stc = (ShardingTableColumInfo)tableInfo.getDeriveColName(ShardingDeriveFlag.SHARDING_TABLE);
        this.sharding = this.stc != null;
    }

    @Override
    public T query(BaseSQLInfo sql) {
        List<T> ts = this.queryList(sql);
        if (CollectionUtils.isEmpty(ts)) {
            return null;
        }
        return ts.get(0);
    }

    @Override
    public List<T> queryList(BaseSQLInfo sql) {
        List<T> ts = Lists.newArrayList();
        Limit limit = sql.limit();
        int startIndexInt = limit==null ? 0: limit.startIndex();
        int lenInt = limit == null ? Integer.MAX_VALUE : limit.len();
        AtomicInteger startIndex = new AtomicInteger(startIndexInt);
        AtomicInteger len = new AtomicInteger(lenInt);
        sql.setStreaming(data-> {
            if(startIndex.get() ==0 && len.get() > 0){
                ts.add((T)data);
                len.getAndDecrement();
                return;
            }else if(len.get() == 0){
                throw new StopExecException("LIMIT DONE");
            }
            startIndex.getAndDecrement();
        });
        queryStream(sql);
        return ts;
    }

    @Override
    public void queryStream(BaseSQLInfo sql) {

        try {
            if(stc != null) {
                List<BaseSQLInfo> baseSQLInfos = stc.shardingParse(sql);
                if(CollectionUtils.isEmpty(baseSQLInfos)){
                    normal(sql);
                } else {
                    for(BaseSQLInfo baseSQLInfo : baseSQLInfos){
                        normal(baseSQLInfo);
                    }
                }
            }else{
                normal(sql);
            }
        }catch (StopExecException e){
            //ignore
        }
    }

    private void normal(BaseSQLInfo sql) {
        JqlLogger.infoSql("SHARDING SQL: {}", sql.getSql());
        JqlLogger.infoSql("SHARDING PAM: {}", sql.getParams());

    }


    @Override
    public Connection getConnection() {
        return null;
    }
}
