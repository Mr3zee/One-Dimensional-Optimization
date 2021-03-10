package expression.expression_tools;

import expression.type.EType;

public class Divide<T extends Number> extends BinaryOperation<T> {
    public Divide(CommonExpression<T> firstExp, CommonExpression<T> secondExp) {
        super(firstExp, secondExp);
    }

    @Override
    protected EType<T> toCalculate(EType<T> firstArg, EType<T> secondArg) {
        return firstArg.divide(secondArg);
    }

    @Override
    protected String getOperand() {
        return "/";
    }

    @Override
    public String toTex() {
        return String.format("(\\frac{%s}{%s})", firstExp.toTex(), secondExp.toTex());
    }

    @Override
    protected int primary() {
        return 1213;
    }

    @Override
    public int getPriority() {
        return 20;
    }

    @Override
    public boolean dependsOnOrder() {
        return true;
    }
}
