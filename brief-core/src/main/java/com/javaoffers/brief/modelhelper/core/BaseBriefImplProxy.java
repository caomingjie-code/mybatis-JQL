package com.javaoffers.brief.modelhelper.core;

import com.javaoffers.brief.modelhelper.context.BriefContext;
import com.javaoffers.brief.modelhelper.context.BriefContextAware;
import com.javaoffers.brief.modelhelper.context.SmartBriefContext;
import com.javaoffers.brief.modelhelper.filter.JqlExecutorChain;
import com.javaoffers.brief.modelhelper.filter.JqlExecutorFilter;
import com.javaoffers.brief.modelhelper.filter.JqlMetaInfo;
import com.javaoffers.brief.modelhelper.utils.SQLType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @description: 包装代理， 没有用jdk代理实现是因为性能问题. 通过该类来扩展额外的功能。
 * @author: create by cmj on 2023/5/28 20:31
 */
public class BaseBriefImplProxy<T, ID> implements BaseBrief<T> , BriefContextAware {

    private static List<JqlExecutorFilter> jqlExecutorChains = new ArrayList<>();

    private BaseBrief baseBrief;

    private Class modelClass;

    public BaseBriefImplProxy(BaseBrief baseBrief, Class modelClass) {
        this.baseBrief = baseBrief;
        this.modelClass = modelClass;
    }

    public BaseBriefImplProxy() {
    }

    private <R> R doProxy(JqlMetaInfo jqlMetaInfo, Function<JqlMetaInfo, R> supplier){
        JqlExecutorChain<R> jqlExecutorChain = new JqlExecutorChain(supplier, jqlExecutorChains, jqlMetaInfo);
        return jqlExecutorChain.doChain();
    }

    @Override
    public int saveData(String sql) {
       return doProxy(new JqlMetaInfo(sql,modelClass), (jmi)->{return baseBrief.saveData(jmi.getSql());});
    }

    @Override
    public int saveData(String sql, Map<String, Object> map) {
        ArrayList<Map<String, Object>> maps = new ArrayList<>();
        maps.add(map);
        return doProxy(new JqlMetaInfo(sql,maps, modelClass), (jmi)->{return baseBrief.saveData(jmi.getSql() , map);});
    }

    @Override
    public int deleteData(String sql) {
        return doProxy(new JqlMetaInfo(sql,modelClass), (jmi)->{return baseBrief.deleteData(jmi.getSql());});
    }

    @Override
    public int deleteData(String sql, Map<String, Object> map) {
        ArrayList<Map<String, Object>> maps = new ArrayList<>();
        maps.add(map);
        return doProxy(new JqlMetaInfo(sql,maps, modelClass), (jmi)->{return baseBrief.deleteData(jmi.getSql() , map);});
    }

    @Override
    public int updateData(String sql) {
        return doProxy(new JqlMetaInfo(sql,modelClass), (jmi)->{return baseBrief.updateData(jmi.getSql());});
    }

    @Override
    public int updateData(String sql, Map<String, Object> map) {
        ArrayList<Map<String, Object>> maps = new ArrayList<>();
        maps.add(map);
        return doProxy(new JqlMetaInfo(sql,maps, modelClass), (jmi)->{return baseBrief.updateData(jmi.getSql() , map);});
    }

    @Override
    public List<T> queryData(String sql) {
        return doProxy(new JqlMetaInfo(sql,modelClass), (jmi)->{return baseBrief.queryData(jmi.getSql());});
    }

    @Override
    public List<T> queryData(String sql, Map<String, Object> map) {
        ArrayList<Map<String, Object>> maps = new ArrayList<>();
        maps.add(map);
        return doProxy(new JqlMetaInfo(sql,maps, modelClass), (jmi)->{return baseBrief.queryData(jmi.getSql() , map);});
    }

    @Override
    public void queryStream(String sql, Map<String, Object> map, Consumer<T> consumer) {
        ArrayList<Map<String, Object>> maps = new ArrayList<>();
        maps.add(map);
        doProxy(new JqlMetaInfo(sql,maps, modelClass), (jmi)->{ baseBrief.queryStream(jmi.getSql() , map, consumer); return 0;});
    }

    @Override
    public List<String> nativeData(String sql, SQLType sqlType) {
        return doProxy(new JqlMetaInfo(sql,modelClass), (jmi)->{return baseBrief.nativeData(jmi.getSql(), sqlType);});
    }

    @Override
    public List<String> nativeData(String sql, Map<String, Object> map, SQLType sqlType) {
        ArrayList<Map<String, Object>> maps = new ArrayList<>();
        maps.add(map);
        return doProxy(new JqlMetaInfo(sql,maps, modelClass), (jmi)->{return baseBrief.nativeData(jmi.getSql() , map, sqlType);});
    }

    @Override
    public Integer batchUpdate(String sql, List<Map<String, Object>> paramMap) {
        JqlMetaInfo jqlMetaInfo = new JqlMetaInfo(sql,paramMap, modelClass);
        return doProxy(jqlMetaInfo, (jmi)-> baseBrief.batchUpdate(jmi.getSql() ,paramMap));
    }

    @Override
    public List<Id> batchInsert(String sql, List<Map<String, Object>> paramMap) {
        JqlMetaInfo jqlMetaInfo = new JqlMetaInfo(sql,paramMap, modelClass);
        return doProxy(jqlMetaInfo, (jmi)-> baseBrief.batchInsert(jmi.getSql() ,paramMap));
    }

    @Override
    public void setBriefContext(BriefContext briefContext) {
        SmartBriefContext smartBriefContext = (SmartBriefContext) briefContext;
        jqlExecutorChains =  smartBriefContext.getJqlExecutorFilters();
    }

    public BaseBrief getOrgBaseBrief(){
        return this.baseBrief;
    }
}
