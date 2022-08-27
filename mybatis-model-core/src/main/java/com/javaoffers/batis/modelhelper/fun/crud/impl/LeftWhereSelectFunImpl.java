package com.javaoffers.batis.modelhelper.fun.crud.impl;

import com.javaoffers.batis.modelhelper.fun.Condition;
import com.javaoffers.batis.modelhelper.fun.ConditionTag;
import com.javaoffers.batis.modelhelper.fun.GGetterFun;
import com.javaoffers.batis.modelhelper.fun.GetterFun;
import com.javaoffers.batis.modelhelper.fun.condition.*;
import com.javaoffers.batis.modelhelper.fun.crud.LeftWhereSelectFun;
import com.javaoffers.batis.modelhelper.utils.TableHelper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * @Description: 以字符串方式输入为字段名称, 在on 条件之后转为where时使用此类。
 * 换句话说就是 on ... where (LeftWhereSelectFunImpl)
 * @Auther: create by cmj on 2022/5/2 02:14
 * @param <M> 主表
 * @param <M2> 子表： 用于支持子表字段 group by , order by
 *
 */
public class LeftWhereSelectFunImpl<M, M2, V> implements LeftWhereSelectFun<M, M2,GetterFun<M,V>, GGetterFun<M2,V>,V> {

    /**
     * 静态
     **/
    private static JdbcTemplate jdbcTemplate;

    public LeftWhereSelectFunImpl(JdbcTemplate jdbcTemplate) {
        synchronized (LeftWhereSelectFunImpl.class) {
            if (jdbcTemplate == null) {
                LeftWhereSelectFunImpl.jdbcTemplate = jdbcTemplate;
            }
        }
    }

    private LinkedList<Condition> conditions;

    public LeftWhereSelectFunImpl(LinkedList<Condition> conditions) {
        this.conditions = conditions;
        this.conditions.add(new WhereConditionMark());
    }

    public LeftWhereSelectFunImpl(LinkedList<Condition> conditions, boolean isAddMark) {
        this.conditions = conditions;
        if (isAddMark) {
            this.conditions.add(new WhereConditionMark());
        }
    }

    public LeftWhereSelectFunImpl() {
    }


    @Override
    public LinkedList<Condition> getConditions() {
        return this.conditions;
    }

    @Override
    public M ex() {
        return exs().get(0);
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> or() {
        conditions.add(new OrCondition());
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> unite(
            Consumer<LeftWhereSelectFun<M, M2, GetterFun<M, V>, GGetterFun<M2, V>, V>> r) {
        conditions.add(new LFCondition( ConditionTag.LK));
        r.accept(this);
        conditions.add(new RFWordCondition( ConditionTag.RK));
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> unite(
            boolean condition, Consumer<LeftWhereSelectFun<M, M2, GetterFun<M, V>, GGetterFun<M2, V>, V>> r) {
        if(condition){
            unite(r);
        }
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2,V> condSQL(String sql) {
        conditions.add(new CondSQLCondition(sql));
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> condSQL(boolean condition, String sql) {
        if(condition){
            condSQL(sql);
        }
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> condSQL(String sql, Map<String, Object> params) {
        conditions.add(new CondSQLCondition(sql, params));
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> condSQL(boolean condition, String sql, Map<String, Object> params) {
        if(condition){
            condSQL(sql, params);
        }
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> orderA(GetterFun<M, V>... getterFuns) {
        List<String> clos = Arrays.stream(getterFuns).map(getterFun -> {
            String cloName = TableHelper.getColNameAndAliasName(getterFun).getLeft();
            return cloName;
        }).collect(Collectors.toList());
        conditions.add(new OrderWordCondition(ConditionTag.ORDER, clos,true));
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> orderA(boolean condition, GetterFun<M, V>... getterFuns) {
        if(condition){
            orderA(getterFuns);
        }
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> orderD(GetterFun<M, V>... getterFuns) {
        List<String> clos = Arrays.stream(getterFuns).map(getterFun -> {
            String cloName = TableHelper.getColNameAndAliasName(getterFun).getLeft();
            return cloName;
        }).collect(Collectors.toList());
        conditions.add(new OrderWordCondition(ConditionTag.ORDER, clos,false));
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> orderD(boolean condition, GetterFun<M, V>... getterFuns) {
        if(condition){
            orderD(getterFuns);
        }
        return this;
    }

    public LeftWhereSelectFunImpl<M, M2, V> orderA(GGetterFun<M2, V>... getterFuns) {
        List<String> clos = Arrays.stream(getterFuns).map(getterFun -> {
            String cloName = TableHelper.getColNameAndAliasName(getterFun).getLeft();
            return cloName;
        }).collect(Collectors.toList());
        conditions.add(new OrderWordCondition(ConditionTag.ORDER, clos,true));
        return this;
    }

    public LeftWhereSelectFunImpl<M, M2, V> orderA(boolean condition, GGetterFun<M2, V>... getterFuns) {
        if(condition){
            orderA(getterFuns);
        }
        return this;
    }

    public LeftWhereSelectFunImpl<M, M2, V> orderD(GGetterFun<M2, V>... getterFuns) {
        List<String> clos = Arrays.stream(getterFuns).map(getterFun -> {
            String cloName = TableHelper.getColNameAndAliasName(getterFun).getLeft();
            return cloName;
        }).collect(Collectors.toList());
        conditions.add(new OrderWordCondition(ConditionTag.ORDER, clos,false));
        return this;
    }

    public LeftWhereSelectFunImpl<M, M2, V> orderD(boolean condition, GGetterFun<M2, V>... getterFuns) {
        if(condition){
            orderD(getterFuns);
        }
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> eq(GetterFun<M, V> col, V value) {
        conditions.add(new WhereOnCondition(col, value, ConditionTag.EQ));
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> eq(boolean condition, GetterFun<M, V> col, V value) {
        if (condition) {
            eq(col,value);
        }
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> ueq(GetterFun<M, V> col, V value) {
        conditions.add(new WhereOnCondition(col, value, ConditionTag.UEQ));
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> ueq(boolean condition, GetterFun<M, V> col, V value) {
        if (condition) {
            ueq(col, value);
        }
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> gt(GetterFun<M, V> col, V value) {
        conditions.add(new WhereOnCondition(col, value, ConditionTag.GT));
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> gt(boolean condition, GetterFun<M, V> col, V value) {
        if (condition) {
            gt(col, value);
        }
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> lt(GetterFun<M, V> col, V value) {
        conditions.add(new WhereOnCondition(col, value, ConditionTag.LT));
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> lt(boolean condition, GetterFun<M, V> col, V value) {
        if (condition) {
            lt(col, value);
        }
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> gtEq(GetterFun<M, V> col, V value) {
        conditions.add(new WhereOnCondition(col, value, ConditionTag.GT_EQ));
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> gtEq(boolean condition, GetterFun<M, V> col, V value) {
        if (condition) {
            gtEq(col, value);
        }
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> ltEq(GetterFun<M, V> col, V value) {
        conditions.add(new WhereOnCondition(col, value, ConditionTag.LT_EQ));
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> ltEq(boolean condition, GetterFun<M, V> col, V value) {
        if (condition) {
            ltEq(col, value);
        }
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> between(GetterFun<M, V> col, V start, V end) {
        conditions.add(new BetweenCondition(col, start, end, ConditionTag.BETWEEN));
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> between(boolean condition, GetterFun<M, V> col, V start, V end) {
        if (condition) {
            between(col, start, end);
        }
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> notBetween(GetterFun<M, V> col, V start, V end) {
        conditions.add(new BetweenCondition(col, start, end, ConditionTag.NOT_BETWEEN));
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> notBetween(boolean condition, GetterFun<M, V> col, V start, V end) {
        if(condition){
            notBetween(col,start,end);
        }
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> like(GetterFun<M, V> col, V value) {
        conditions.add(new WhereOnCondition(col, value, ConditionTag.LIKE));
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> like(boolean condition, GetterFun<M, V> col, V value) {
        if (condition) {
            like(col, value);
        }
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> likeLeft(GetterFun<M, V> col, V value) {
        conditions.add(new WhereOnCondition(col, value, ConditionTag.LIKE_LEFT));
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> likeLeft(boolean condition, GetterFun<M, V> col, V value) {
        if (condition) {
            likeLeft(col, value);
        }
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> likeRight(GetterFun<M, V> col, V value) {
        conditions.add(new WhereOnCondition(col, value, ConditionTag.LIKE_RIGHT));
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> likeRight(boolean condition, GetterFun<M, V> col, V value) {
        if (condition) {
            likeRight(col, value);
        }
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> in(GetterFun<M, V> col, V... values) {
        conditions.add(new InCondition(col, values, ConditionTag.IN));
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> in(boolean condition, GetterFun<M, V> col, V... values) {
        if (condition) {
            in(col, values);
        }
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> in(GetterFun<M, V> col, Collection... values) {
        conditions.add(new InCondition(col, values, ConditionTag.IN));
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> in(boolean condition, GetterFun<M, V> col, Collection... values) {
        if (condition) {
            in(col, values);
        }
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> notIn(GetterFun<M, V> col, V... values) {
        conditions.add(new InCondition(col, values, ConditionTag.NOT_IN));
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> notIn(boolean condition, GetterFun<M, V> col, V... values) {
        if (condition) {
            notIn(col, values);
        }
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> notIn(GetterFun<M, V> col, Collection... values) {
        conditions.add(new InCondition(col, values, ConditionTag.NOT_IN));
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> notIn(boolean condition, GetterFun<M, V> col, Collection... values) {
        if (condition) {
            notIn(col, values);
        }
        return this;
    }

    @Override
    public LeftWhereSelectFun<M, M2, GetterFun<M, V>, GGetterFun<M2, V>, V> isNull(GetterFun<M, V>... cols) {
        for(GetterFun<M,V> col: cols){
            conditions.add(new IsNullOrCondition(col, ConditionTag.IS_NULL));
        }
        return this;
    }

    @Override
    public LeftWhereSelectFun<M, M2, GetterFun<M, V>, GGetterFun<M2, V>, V> isNull(boolean condition, GetterFun<M, V>... cols) {
        if(condition){
            isNull(cols);
        }
        return this;
    }

    @Override
    public LeftWhereSelectFun<M, M2, GetterFun<M, V>, GGetterFun<M2, V>, V> isNotNull(GetterFun<M, V>... cols) {
        for(GetterFun<M,V> col: cols){
            conditions.add(new IsNullOrCondition(col, ConditionTag.IS_NOT_NULL));
        }
        return this;
    }

    @Override
    public LeftWhereSelectFun<M, M2, GetterFun<M, V>, GGetterFun<M2, V>, V> isNotNull(boolean condition, GetterFun<M, V>... cols) {
        if(condition){
            isNull(cols);
        }
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> exists(String existsSql) {
        conditions.add(new ExistsCondition<V>(existsSql));
        return this;
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> exists(boolean condition, String existsSql) {
        if (condition) {
            exists(existsSql);
        }
        return this;
    }

    @Override
    public LeftHavingPendingFunImpl<M, M2, GetterFun<M, V>, GGetterFun<M2, V>, V, V> groupBy(GetterFun<M, V>... c) {
        conditions.add(new GroupByWordCondition(c,ConditionTag.GROUP_BY));
        return new LeftHavingPendingFunImpl<>(conditions);
    }

    @Override
    public LeftHavingPendingFunImpl<M, M2, GetterFun<M, V>, GGetterFun<M2, V>, V, V> groupBy(GGetterFun<M2, V>... c) {
        conditions.add(new GroupByWordCondition(c,ConditionTag.GROUP_BY));
        return new LeftHavingPendingFunImpl<>(conditions);
    }

    @Override
    public LeftWhereSelectFunImpl<M, M2, V> limitPage(int pageNum, int size) {
        this.conditions.add(new LimitWordCondition(pageNum, size));
        return this;
    }
}
