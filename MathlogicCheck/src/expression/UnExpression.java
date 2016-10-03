package expression;

public class UnExpression extends Expression {
    public final Expression arg;

    public UnExpression(Operator op, Expression arg) {
        super(op);
        this.arg = arg;
    }

    public boolean equals(Object o) {
        return o instanceof UnExpression && equals((UnExpression) o);
    }

    public boolean equals(UnExpression e) {
        return op == e.op && arg.equals(e.arg);
    }

    public int hashCode() {
        return op.hashCode() * 67432 + arg.hashCode() * 76424;
    }

    public String toString() {
        return op + "" + arg;
    }

    public String toPlainString() {
        return toString();
    }
}
