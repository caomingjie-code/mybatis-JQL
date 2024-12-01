package com.javaoffers.brief.modelhelper.router.jdbc;

import com.javaoffers.brief.modelhelper.core.BaseSQLInfo;
import com.javaoffers.brief.modelhelper.core.Limit;
import com.javaoffers.brief.modelhelper.exception.ParseResultSetException;
import com.javaoffers.brief.modelhelper.exception.SqlParseException;
import com.javaoffers.brief.modelhelper.exception.StopExecException;
import com.javaoffers.brief.modelhelper.jdbc.BriefResultSetExecutor;
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

    DataSource dataSource;

    Class<T> modelClass;

    public ShardingBriefQueryExecutor(DataSource dataSource, Class modelClass) {
        this.dataSource = dataSource;
        this.modelClass = modelClass;
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
        int lenInt = limit == null ? Integer.MIN_VALUE : limit.len();
        AtomicInteger startIndex = new AtomicInteger(startIndexInt);
        AtomicInteger len = new AtomicInteger(lenInt);
        sql.setStreaming(data->{
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
        TableInfo tableInfo = TableHelper.getTableInfo(this.modelClass);
        ShardingTableColumInfo stc = (ShardingTableColumInfo)tableInfo.getDeriveColName(ShardingDeriveFlag.SHARDING_TABLE);
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
        boolean oldAutoCommitStatus = false;
        Connection connection = null;
        try {
            connection = getConnection();
            oldAutoCommitStatus = connection.getAutoCommit();
            PreparedStatement ps = connection.prepareStatement(sql.getSql());
            List<Object[]> argsParam = sql.getArgsParam();
            if (argsParam != null && argsParam.size() == 1) {
                Object[] ov = argsParam.get(0);
                for (int i = 0; i < ov.length; ) {
                    Object o = ov[i];
                    ps.setObject(++i, o);
                }
            }
            switch (sql.getSqlType()) {
                case JOIN_SELECT:
                    ModelParseUtils.converterResultSet2ModelForJoinSelectStream(this.modelClass,
                            new BriefResultSetExecutor(ps.executeQuery()), sql.getStreaming());
                    break;
                case NORMAL_SELECT:
                    ModelParseUtils.converterResultSet2ModelForNormalSelectStream(this.modelClass,
                            new BriefResultSetExecutor(ps.executeQuery()), sql.getStreaming());
                    break;
                default:
                    throw new ParseResultSetException("sql type does not exist for streaming process");
            }
        } catch (Exception e) {
            if(e instanceof StopExecException){
                throw (StopExecException)e;
            }
            e.printStackTrace();
            throw new SqlParseException(e.getMessage());
        } finally {
            closeConnection(connection, oldAutoCommitStatus);
        }
    }

    @Override
    public Connection getConnection() {
        try {
            return this.dataSource.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SqlParseException(e.getMessage());
        }

    }
}
