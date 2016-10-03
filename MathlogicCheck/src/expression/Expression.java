package expression;

public abstract class Expression {
    public final Operator op;

    public Expression(Operator op) {
        this.op = op;
    }

    public abstract String toPlainString();
}
