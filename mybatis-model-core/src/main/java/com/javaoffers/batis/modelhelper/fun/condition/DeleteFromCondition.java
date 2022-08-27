package com.javaoffers.batis.modelhelper.fun.condition;

import com.javaoffers.batis.modelhelper.fun.Condition;
import com.javaoffers.batis.modelhelper.fun.ConditionTag;
import com.javaoffers.batis.modelhelper.utils.TableHelper;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Map;

/**
 * @Description: delete from table
 * @Auther: create by cmj on 2022/7/10 00:32
 */
public class DeleteFromCondition implements Condition {

    private Class modelClass;

    private String tableName;

    @Override
    public ConditionTag getConditionTag() {
        return ConditionTag.DELETE_FROM;
    }

    @Override
    public String getSql() {
        return getConditionTag().getTag() + tableName ;
    }

    @Override
    public Map<String, Object> getParams() {
        return Collections.emptyMap();
    }

    public DeleteFromCondition(Class tableModelClass) {
        this.modelClass = tableModelClass;
        Assert.isTrue(tableModelClass != null , "table Model is null ");
        String tableName = TableHelper.getTableName(tableModelClass);
        this.tableName = tableName ;
    }

    public Class getModelClass() {
        return modelClass;
    }
}
