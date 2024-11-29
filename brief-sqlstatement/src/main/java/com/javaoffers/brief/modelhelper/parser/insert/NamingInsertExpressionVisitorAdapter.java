package com.javaoffers.brief.modelhelper.parser.insert;

import com.javaoffers.brief.modelhelper.parser.ColNameProcessorInfo;
import com.javaoffers.brief.modelhelper.parser.ConditionName;
import com.javaoffers.brief.modelhelper.parser.NamingSelectContent;
import com.javaoffers.thrid.jsqlparser.expression.ExpressionVisitorAdapter;
import com.javaoffers.thrid.jsqlparser.expression.JdbcParameter;
import com.javaoffers.thrid.jsqlparser.schema.Column;
import com.javaoffers.thrid.jsqlparser.schema.Table;
import com.javaoffers.thrid.jsqlparser.statement.select.SubSelect;
import com.javaoffers.thrid.jsqlparser.statement.select.WithItem;

/**
 * 每个item 对因的 expression. 可能是个?(jdbcParem), (subSelect). 其他不做处理.
 * @author mingJie
 */
public class NamingInsertExpressionVisitorAdapter extends ExpressionVisitorAdapter {

    private NamingSelectContent namingContent;

    @Override
    public void visit(JdbcParameter parameter) {
        boolean[] needEncryptColMark = this.namingContent.getNeedEncryptColMark();

        Integer index = this.namingContent.getNeedEncryptColMarkIndex() % this.namingContent.getInsertColLen();

        if(needEncryptColMark[index]){
            String insertTableName = this.namingContent.getInsertTableName();
            ColNameProcessorInfo colNameProcessorInfo = new ColNameProcessorInfo();
            colNameProcessorInfo.setConditionName(ConditionName.VALUES);
            Column column = new Column(new Table(insertTableName), "?");
            colNameProcessorInfo.setColumn(column);
            colNameProcessorInfo.setColumnIndex(index);
            this.namingContent
                    .getProcessorByTableName(insertTableName)
                    .accept(colNameProcessorInfo);
            parameter.setTag(column.getColumnName());
        }
    }

    //values ((subSelect), xx) 这种特殊情况
    @Override
    public void visit(SubSelect subSelect) {
        if (super.getSelectVisitor() != null) {
            if (subSelect.getWithItemsList() != null) {
                for (WithItem item : subSelect.getWithItemsList()) {
                    item.accept(super.getSelectVisitor());
                }
            }
            subSelect.getSelectBody().accept(super.getSelectVisitor());
        }
        if (subSelect.getPivot() != null) {
            subSelect.getPivot().accept(this);
        }

    }

    public NamingInsertExpressionVisitorAdapter( NamingSelectContent namingContent) {
        this.namingContent = namingContent;
    }

}
