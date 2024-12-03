package com.javaoffers.base.modelhelper.sample;

import com.javaoffers.brief.modelhelper.core.BaseSQLInfo;
import com.javaoffers.brief.modelhelper.core.Id;
import com.javaoffers.brief.modelhelper.jdbc.JdbcExecutor;
import com.javaoffers.brief.modelhelper.jdbc.JdbcExecutorMetadata;

import java.util.Collections;
import java.util.List;

public class MockBriefJdbcExecutor implements JdbcExecutor {

    @Override
    public Id save(BaseSQLInfo sql) {
        return null;
    }

    @Override
    public List<Id> batchSave(BaseSQLInfo sql) {
        return Collections.emptyList();
    }

    @Override
    public int modify(BaseSQLInfo sql) {
        return 0;
    }

    @Override
    public int batchModify(BaseSQLInfo sql) {
        return 0;
    }

    @Override
    public Object query(BaseSQLInfo sql) {
        return null;
    }

    @Override
    public List queryList(BaseSQLInfo sql) {
        return Collections.emptyList();
    }

    @Override
    public void queryStream(BaseSQLInfo sql) {
       //NONE
    }

    @Override
    public JdbcExecutorMetadata getMetadata() {
        return null;
    }
}
