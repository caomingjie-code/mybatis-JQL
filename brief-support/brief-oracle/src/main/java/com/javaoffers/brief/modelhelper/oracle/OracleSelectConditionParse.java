package com.javaoffers.brief.modelhelper.oracle;

import com.javaoffers.brief.modelhelper.core.parse.SelectConditionParse;
import com.javaoffers.brief.modelhelper.fun.Condition;
import com.javaoffers.brief.modelhelper.fun.condition.where.LimitWordCondition;

import java.util.LinkedList;

public class OracleSelectConditionParse extends SelectConditionParse {

    @Override
    public void beforeSelectCol(LinkedList<Condition> conditions, StringBuilder selectCols) {
        //检测是否有分页
        Condition condition = conditions.peekLast();
        if(condition instanceof LimitWordCondition){
            StringBuilder oracleSelect = new StringBuilder("SELECT * FROM ( " +
                    "SELECT A.*, ROWNUM RN " +
                    "FROM ( ");

            oracleSelect.append(selectCols);
            selectCols.delete(0, selectCols.length());
            selectCols.append(oracleSelect);

            //弹出mysql的limit语法
            LimitWordCondition limit = (LimitWordCondition)conditions.pollLast();

            //生成oracle 分页语法
            OracleLimitWordCondition oracleLimit = new OracleLimitWordCondition(limit.pageNum, limit.pageSize);

            conditions.addLast(oracleLimit);

        }
    }
}
