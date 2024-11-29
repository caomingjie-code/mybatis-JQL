package com.javaoffers.brief.modelhelper.parser;

import com.javaoffers.thrid.jsqlparser.expression.ExpressionVisitorAdapter;
import com.javaoffers.thrid.jsqlparser.schema.Column;
import com.javaoffers.thrid.jsqlparser.schema.Table;
import com.javaoffers.thrid.jsqlparser.statement.select.SelectBody;
import com.javaoffers.thrid.jsqlparser.statement.select.SubSelect;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author mingJie
 */
public class NamingSelectExpressionVisitorAdapter extends ExpressionVisitorAdapter {

    private ConditionName conditionName;
    private NamingSelectContent namingContent;
    //where colName的位置。where a=xx and b=xx. a的位置是0，b的位置是1. 负数<0表示无效
    private AtomicInteger whereIndex = new AtomicInteger(Integer.MIN_VALUE);

    /**
     * select columns, where columns. 这个方法代表真实的表字段.
     */
    public void visit(Column column) {

        String columnName = column.getColumnName();
        String tableName = "";
        Table table = column.getTable();
        if (table != null) {
            tableName = table.getName();
        }
        //获取真实的表名
        Map<String, String> tableNameMapper = namingContent.getTableNameMapper();
        tableName = tableNameMapper.getOrDefault(tableName, tableName);
        //LOGUtils.printLog(this.conditionName + ": " + tableName + "." + columnName);
        ColNameProcessorInfo colNameProcessorInfo = new ColNameProcessorInfo();
        colNameProcessorInfo.setColumn(column);
        colNameProcessorInfo.setConditionName(conditionName);
        colNameProcessorInfo.setColumnIndex(whereIndex.getAndIncrement());
        // 如果存在tableName不为空
        if (StringUtils.isNotBlank(tableName)) {
            //如果表与checkTableName相同，colName也相同
            if(namingContent.isProcessorTableName(tableName) && namingContent.isProcessorCloName(tableName, columnName)){
                namingContent.getProcessorByTableName(tableName).accept(colNameProcessorInfo);
                //column.setColumnName("NAME");
            }
        }
        //普通的单表查询(非子查询)
        else if(!namingContent.isHavaJoin()
                && !namingContent.isSubSelct()
                && namingContent.isProcessorCloName(namingContent.getSimpleSingleTable(), columnName)
        ){

            namingContent.getProcessorByTableName(namingContent.getSimpleSingleTable()).accept(colNameProcessorInfo);
            //column.setColumnName("NAME");

        }

        // 如果是没有join的子查询. 并且子查询的表与checkTableName相同: (select colName 部分)
        else if (namingContent.isSubSelct()
                && !namingContent.isHavaJoin()
                && ConditionName.SELECT_COL_NAME == conditionName
                && namingContent.isProcessorCloName(namingContent.getSubSelectMainTable(), columnName)
        ) {
            namingContent.getProcessorByTableName(namingContent.getSubSelectMainTable()).accept(colNameProcessorInfo);
            //column.setColumnName("NAME");

        }
        // 如果是没有join的子查询. 并且子查询的where部分需要判断是否包含checkTableName
        // (where 部分 可能存在主表的colName, 这些表（主表和子表）是否是需要检查的表)
        else if (namingContent.isSubSelct()
                && !namingContent.isHavaJoin()
                && ConditionName.WHERE == conditionName
                && namingContent.isContainsProcessorRealTableName()
        ) {
            //这些表（主表和子表）, 正确来讲只会属于其中一个表， 如果同时属于多个表则sql是错误的
            Collection<String> values = namingContent.getTableNameMapper().values();
            for(String tableName0 : values){
                if(namingContent.isProcessorCloName(tableName0, columnName )){
                    namingContent.getProcessorByTableName(tableName0).accept(colNameProcessorInfo);
                    return;
                }
            }
            //column.setColumnName("NAME");

        }
        // 如果是多表.
        else if (namingContent.isHavaJoin()
                &&namingContent.isContainsProcessorRealTableName()
        ) {
            // join 多表中存在要处理的表
            Collection<String> values = namingContent.getTableNameMapper().values();
            for(String tableName0 : values){
                if(namingContent.isProcessorCloName(tableName0, columnName )){
                    namingContent.getProcessorByTableName(tableName0).accept(colNameProcessorInfo);
                    return;
                }
            }
            //column.setColumnName("NAME");
        }
    }

    /**
     * 子查询 处理
     */
    @Override
    public void visit(SubSelect subSelect) {
        SelectBody selectBody = subSelect.getSelectBody();
        NamingSelectContent namingContent = new NamingSelectContent();
        namingContent.setSubSelct(true);
        Map<String, String> tableNameMapper = this.namingContent.getTableNameMapper();
        tableNameMapper.forEach((k, v) -> {
            if (StringUtils.isNotBlank(k)) {
                namingContent.getTableNameMapper().put(k, v);
            }
        });
        namingContent.setProcessor(this.namingContent.getProcessor());
        selectBody.accept(new NamingSelectVisitorAdapter(namingContent));
    }

    public NamingSelectExpressionVisitorAdapter(ConditionName name, NamingSelectContent namingContent) {
        this.conditionName = name;
        this.namingContent = namingContent;
        if(ConditionName.isWhereOnName(name)){
            whereIndex = new AtomicInteger(0);
        }
    }
}
