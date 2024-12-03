package com.javaoffers.brief.modelhelper.jdbc;

import com.javaoffers.brief.modelhelper.core.Id;
import com.javaoffers.brief.modelhelper.core.BaseSQLInfo;

import javax.sql.DataSource;
import java.util.List;

/**
 * @description:
 * @author: create by cmj on 2023/7/29 17:14
 */
public interface JdbcExecutor<T> {

    /**
     * 保存
     * @param sql
     * @return
     */
    Id save(BaseSQLInfo sql);

    /**
     * 批量保存
     * @param sql
     * @return
     */
    List<Id> batchSave(BaseSQLInfo sql);

    /**
     * 更新
     * @param sql
     * @return
     */
    int modify(BaseSQLInfo sql);

    /**
     * 批量更新
     * @param sql
     * @return
     */
    int batchModify(BaseSQLInfo sql);

    /**
     * 查询
     * @param sql
     * @return
     */
    T query(BaseSQLInfo sql);

    /**
     * 查询列表
     * @param sql
     * @return
     */
    List<T> queryList(BaseSQLInfo sql);

    /**
     * 流查询
     * @param sql
     */
    void queryStream(BaseSQLInfo sql);

    /**
     * 获取元数据.
     * @return
     */
    JdbcExecutorMetadata getMetadata();
}
