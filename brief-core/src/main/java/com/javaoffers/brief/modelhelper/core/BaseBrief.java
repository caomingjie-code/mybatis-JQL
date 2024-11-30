package com.javaoffers.brief.modelhelper.core;

import com.javaoffers.brief.modelhelper.utils.SQLType;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @Description: 该项目的顶级接口
 * @param <T> model
 * @Auther: create by cmj on 2022/05/22 02:49
 */
public interface BaseBrief<T>{
	
    /*****************************************************************************/
	public int saveData(String sql);

	public int saveData(String sql,Map<String,Object> map);
	
	public int deleteData(String sql);

	public int deleteData(String sql,Map<String,Object> map);
	
	public int updateData(String sql);

	public int updateData(String sql,Map<String,Object> map);
	
	/*****************************************************************************/
	default public int querySize(){return 0;}

	public List<T> queryData(String sql);

	public List<T> queryData(String sql,Map<String,Object> map);

	public void queryStream(String sql, Map<String,Object> map, Consumer<T> consumer);

	public List<String> nativeData(String sql, SQLType sqlType);

	public List<String> nativeData(String sql,Map<String,Object> map, SQLType sqlType);

	/*****************************************************************************/ 
	public Integer batchUpdate(String sql,List<Map<String,Object>> paramMap);

	public List<Id> batchInsert(String sql, List<Map<String,Object>> paramMap);

	/*****************************************************************************/ 

}
