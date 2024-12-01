package com.javaoffers.brief.modelhelper.oracle;

import com.javaoffers.brief.modelhelper.fun.ConditionTag;
import com.javaoffers.brief.modelhelper.fun.condition.where.LimitWordCondition;

import static com.javaoffers.brief.modelhelper.oracle.OracleSelectConditionParse.oracleSelect;

/**
 * oracle的分页如下：每页查询5条， 第一页 1-5， 第二页： 6-10
 * SELECT * FROM
 * (
 * SELECT A.*, ROWNUM RN
 * FROM (SELECT * FROM TABLE_NAME) A
 * WHERE ROWNUM <= 5
 * )
 * WHERE RN > 0
 *
 */
public class OracleLimitWordCondition extends LimitWordCondition {

    public OracleLimitWordCondition(int pageNum, int pageSize) {
        super(pageNum, pageSize);
    }

    /**
     * 追加后半部分：
     * A
     * WHERE ROWNUM <= 40
     * )
     * WHERE RN >= 21
     */
    @Override
    public String getSql() {
        String startIndexTag = getNextTag();
        String endPositionTag = getNextTag();
        int endPosition = super.len + super.startIndex;
        this.getParams().put(endPositionTag, endPosition);
        this.getParams().put(startIndexTag, super.startIndex);
        // A 是表的别名
        return ") A where ROWNUM <= #{" + endPositionTag + "} ) WHERE RN >= #{" + startIndexTag + "}";
    }

    @Override
    public String cleanLimit(String limitSql) {
        limitSql = limitSql.substring(0,oracleSelect.length());
        limitSql = limitSql.replaceAll("\\) A where ROWNUM <= \\? \\) WHERE RN >= \\?","");
        return limitSql;
    }
}
