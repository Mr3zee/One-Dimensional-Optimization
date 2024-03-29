package expression.expression_tools;

import expression.type.EType;

import java.util.Objects;

public abstract class UnaryOperation<T extends Number> implements CommonExpression<T> {
    CommonExpression<T> expression;

    public UnaryOperation(CommonExpression<T> expression) {
        this.expression = expression;
    }

    @Override
    public EType<T> evaluate(EType<T> x) {
        return toCalculate(expression.evaluate(x));
    }

    protected abstract EType<T> toCalculate(EType<T> arg);

    @Override
    public String toString() {
        return String.format("%s(%s)",getOperand(), expression);
    }

    @Override
    public String toTex() {
        return String.format("%s(%s)", getTexOperand(), expression.toTex());
    }

    @Override
    public int getPriority() {
        return 40;
    }

    @Override
    public boolean dependsOnOrder() {
        return false;
    }

    protected abstract String getOperand();

    protected abstract String getTexOperand();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        UnaryOperation<?> that = (UnaryOperation<?>) o;
        return Objects.equals(expression, that.expression);
    }

    @Override
    public int hashCode() {
        return (primary() * 2467 + expression.hashCode()) % 1073676287;
    }

    protected abstract int primary();
}
