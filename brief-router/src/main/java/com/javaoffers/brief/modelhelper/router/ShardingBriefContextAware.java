package com.javaoffers.brief.modelhelper.router;

import com.javaoffers.brief.modelhelper.context.BriefContext;
import com.javaoffers.brief.modelhelper.context.BriefContextAware;
import com.javaoffers.brief.modelhelper.context.BriefContextPostProcess;
import com.javaoffers.brief.modelhelper.jdbc.JdbcExecutorFactory;
import com.javaoffers.brief.modelhelper.router.jdbc.ShardingBriefJdbcExecutorFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 该类主要用于修改BriefContext组件. 在finish之前.
 */
public class ShardingBriefContextAware implements BriefContextPostProcess {

    @Override
    public void postProcess(BriefContext briefContext) {
        JdbcExecutorFactory jdbcExecutorFactory = briefContext.getJdbcExecutorFactory();
        briefContext.setJdbcExecutorFactory(new ShardingBriefJdbcExecutorFactory(jdbcExecutorFactory));
    }
}
