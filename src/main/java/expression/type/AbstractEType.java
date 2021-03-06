package expression.type;

import java.util.Objects;

public abstract class AbstractEType<T extends Number> implements EType<T> {
    private final T value;

    public AbstractEType(T value) {
        this.value = value;
    }

    @Override
    public T value() {
        return value;
    }

    @Override
    public EType<T> add(EType<T> v) {
        return valueOf(calcAdd(v.value()));
    }

    @Override
    public EType<T> subtract(EType<T> v) {
        return valueOf(calcSubtract(v.value()));
    }

    @Override
    public EType<T> divide(EType<T> v) {
        return valueOf(calcDivide(v.value()));
    }

    @Override
    public EType<T> multiply(EType<T> v) {
        return valueOf(calcMultiply(v.value()));
    }

    @Override
    public EType<T> bitCount() {
        return valueOf(calcBitCount());
    }

    @Override
    public EType<T> negate() {
        return valueOf(calcNegate());
    }

    @Override
    public EType<T> abs() {
        return valueOf(calcAbs());
    }

    @Override
    public EType<T> pow2() {
        return valueOf(calcPow2());
    }

    @Override
    public EType<T> pow(EType<T> v) {
        return valueOf(calcPow(v.value()));
    }

    @Override
    public EType<T> log2() {
        return valueOf(calcLog2());
    }

    @Override
    public EType<T> log(EType<T> v) {
        return valueOf(calcLog(v.value()));
    }

    protected abstract T calcAdd(T v);

    protected abstract T calcSubtract(T v);

    protected abstract T calcMultiply(T v);

    protected abstract T calcDivide(T v);

    protected abstract T calcNegate();

    protected abstract T calcBitCount();

    protected abstract T calcAbs();

    protected abstract T calcPow2();

    protected abstract T calcPow(T v);

    protected abstract T calcLog2();

    protected abstract T calcLog(T v);

    @Override
    public abstract EType<T> valueOf(T v);

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        AbstractEType<?> that = (AbstractEType<?>) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return (primary() * 2399) % 1073676287;
    }

    protected abstract int primary();
}
