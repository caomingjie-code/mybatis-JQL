package com.javaoffers.brief.modelhelper.parser.update;

import com.javaoffers.brief.modelhelper.parser.ColNameProcessorInfo;
import com.javaoffers.brief.modelhelper.parser.ConditionName;
import com.javaoffers.brief.modelhelper.parser.NamingSelectContent;
import com.javaoffers.brief.modelhelper.parser.NamingSelectVisitorAdapter;
import com.javaoffers.brief.modelhelper.parser.insert.NamingInsertVisitorAdapter;
import com.javaoffers.thrid.jsqlparser.expression.ExpressionVisitorAdapter;
import com.javaoffers.thrid.jsqlparser.expression.JdbcParameter;
import com.javaoffers.thrid.jsqlparser.schema.Column;
import com.javaoffers.thrid.jsqlparser.schema.Table;
import com.javaoffers.thrid.jsqlparser.statement.select.SelectBody;
import com.javaoffers.thrid.jsqlparser.statement.select.SubSelect;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author mingJie
 */
public class NamingUpdateExpressionVisitorAdapter extends ExpressionVisitorAdapter {

    private ConditionName conditionName;
    private NamingSelectContent namingContent;
    //where colName的位置。where a=xx and b=xx. a的位置是0，b的位置是1. 负数<0表示无效
    private AtomicInteger whereIndex = new AtomicInteger(Integer.MIN_VALUE);

    /**
     * 处理wehere colName = ? and xxx = xxx
     * @param column
     */
    @Override
    public void visit(Column column) {
        if(ConditionName.isWhereOnName(this.conditionName)){
            String columnName = column.getColumnName();
            String updateTableName = this.namingContent.getUpdateTableName();
            if(this.namingContent.isProcessorCloName(updateTableName, columnName)){
                ColNameProcessorInfo colNameProcessorInfo = new ColNameProcessorInfo();
                colNameProcessorInfo.setColumn(column);
                colNameProcessorInfo.setConditionName(this.conditionName);
                colNameProcessorInfo.setColumnIndex(whereIndex.getAndIncrement());
                this.namingContent.getProcessorByTableName(updateTableName).accept(colNameProcessorInfo);
            }
        }

    }

    /**
     * 上游有对table+colName的判断.
     * 处理 update table set colName = ?
     * @param parameter
     */
    @Override
    public void visit(JdbcParameter parameter) {
        if(ConditionName.UPDATE_SET == conditionName){
            ColNameProcessorInfo colNameProcessorInfo = new ColNameProcessorInfo();
            colNameProcessorInfo.setConditionName(conditionName);
            Column column = new Column(new Table(this.namingContent.getUpdateTableName()), "?");
            colNameProcessorInfo.setColumn(column);
            //上游有对table+colName的判断. 所以这里可以直接调用
            this.namingContent.getProcessorByTableName(this.namingContent.getUpdateTableName()).accept(colNameProcessorInfo);
            parameter.setTag(colNameProcessorInfo.getColumn().getColumnName());
        }

    }

    /**
     * 如果update中存在子查询.
     */
    @Override
    public void visit(SubSelect subSelect) {
        SelectBody selectBody = subSelect.getSelectBody();
        //创建一个新的上下文对象给子查询语句使用
        NamingSelectContent namingContent = new NamingSelectContent();
        namingContent.setSubSelct(true);
        Map<String, String> tableNameMapper = this.namingContent.getTableNameMapper();
        tableNameMapper.forEach((k, v) -> {
            if (StringUtils.isNotBlank(k)) {
                namingContent.getTableNameMapper().put(k, v);
            }
        });
        namingContent.setProcessor(this.namingContent.getProcessor());
        //set 部分的子查询 不解析 select column . (保持加密)
        if(this.conditionName == ConditionName.UPDATE_SET){
            selectBody.accept(new NamingInsertVisitorAdapter(namingContent));
        }else if(this.conditionName == ConditionName.WHERE){
            //where 部分的 子查询 要解析 select column 部分
            selectBody.accept(new NamingSelectVisitorAdapter(namingContent));
        }
    }

    public NamingUpdateExpressionVisitorAdapter(ConditionName name, NamingSelectContent namingContent) {
        this.conditionName = name;
        this.namingContent = namingContent;
        if (ConditionName.isWhereOnName(name)) {
            whereIndex = new AtomicInteger(0);
        }
    }
}
