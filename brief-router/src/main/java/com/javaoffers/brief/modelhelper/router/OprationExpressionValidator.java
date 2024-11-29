package com.javaoffers.brief.modelhelper.router;

import com.javaoffers.thrid.jsqlparser.expression.operators.relational.EqualsTo;
import com.javaoffers.thrid.jsqlparser.expression.operators.relational.InExpression;
import com.javaoffers.thrid.jsqlparser.util.validation.validator.ExpressionValidator;

import java.util.function.Consumer;

/**
 * create by cmj on 2024-11-29
 */
public class OprationExpressionValidator extends ExpressionValidator {
    private Consumer<String> validator;
    //
    public void visit(EqualsTo equalsTo){
        validator.accept(equalsTo.toString());
    }

    public void visit(InExpression inExpression) {
        validator.accept(inExpression.toString());
    }

    public OprationExpressionValidator(Consumer<String> validator) {
        this.validator = validator;
    }
}
