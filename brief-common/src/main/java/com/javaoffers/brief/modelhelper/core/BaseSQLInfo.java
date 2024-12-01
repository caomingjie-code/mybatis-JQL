package com.javaoffers.brief.modelhelper.core;

import com.javaoffers.brief.modelhelper.utils.DBType;
import com.javaoffers.brief.modelhelper.utils.SQLType;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface BaseSQLInfo {
    public String getSql();
    public List<Map<String, Object>> getParams();
    public void resetSql(String sql);
    public int getBatchSize();
    public List<Object[]> getArgsParam();
    public SQLType getSqlType();
    public DBType getDbType();
    public Consumer getStreaming();
    public void setStreaming(Consumer consumer);
    public BaseSQLInfo clone();
    public Limit limit();
}
