import expression.*;

import java.util.HashMap;
import java.util.Stack;

public class Parser {

    private final int LOWEST_PRIORITY = 3;
    private final char[] expr;
    private int p;
    private int factorNumber;
    HashMap<String, Integer> variables;
    Stack<Integer> virtualBrackets;

    public Parser(String expr) {
        this.expr = prepare(expr);
        p = 0;
        variables = new HashMap<>();
        factorNumber = 0;
        virtualBrackets = new Stack<>();
    }

    char[] prepare(String source) {
        return source.replaceAll(" |-", "").toCharArray();
    }

    public Expression parse() {
        Expression result = null;
        try {
            result = parseRec(0, LOWEST_PRIORITY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private Expression parseRec(int level, int prior) {
        skipSpaces();
        if (prior == 0) {
            return readFactor(level, prior);
        } else {
            Expression val = parseRec(level, prior - 1);
            skipSpaces();
            while (!end() && isOperation(peekChar()) && getPriority(peekChar()) == prior) {
                skipSpaces();
                Operator op = getOperator(getChar());
                skipSpaces();
                Expression next;
                if (op.getAssoc() == Operator.Assoc.RIGHT) {
                    virtualBrackets.add(level);
                    next = parseRec(level + 1, LOWEST_PRIORITY);
                } else {
                    next = parseRec(level, prior - 1);
                }
                val = new BinExpression(val, next, op);
                if (!end() && !virtualBrackets.empty() && virtualBrackets.peek() == level) {
                    virtualBrackets.pop();
                    return val;
                }
            }
            return val;
        }
    }

    private Expression readFactor(int level, int prior) {
        if (Character.isDigit(peekChar()) || Character.isLetter(peekChar())) {
            return readVariable();
        } else if (peekChar() == '(') {
            getChar();
            Expression e = parseRec(level + 1, LOWEST_PRIORITY);
            if (getChar() != ')') {
                System.err.println("Error: no )");
            }
            return e;
        } else if (peekChar() == '!') {
            getChar();
            return new UnExpression(Operator.NOT, parseRec(level + 1, prior));
        }
        System.err.println("Error: can't parse");
        return null;
    }

    private Expression readVariable() {
        StringBuilder builder = new StringBuilder();
        while (!end() && (Character.isDigit(peekChar()) || Character.isLetter(peekChar()))) {
            builder.append(getChar());
        }
        String var = builder.toString();
        int number;
        if (variables.containsKey(var)) {
            number = variables.get(var);
        } else {
            number = factorNumber++;
            variables.put(var, number);
        }
        return new Variable(number, var);
    }

    private char peekChar() {
        return expr[p];
    }

    private char getChar() {
        return expr[p++];
    }

    private Operator getOperator(char op) {
        switch (op) {
            case '|':
                return Operator.OR;
            case '&':
                return Operator.AND;
            case '>':
                return Operator.IMPL;
            case '!':
                return Operator.NOT;
            default:
                System.err.println("no such operator : " + op);
                return null;
        }
    }

    private int getPriority(char op) {
        switch (op) {
            case '>':
                return 3;
            case '|':
                return 2;
            case '&':
                return 1;
            default:
                System.err.println("no such operation : " + op);
                return -1;
        }
    }

    private boolean end() {
        return p >= expr.length;
    }

    void skipSpaces() {
        while (!end() && (peekChar() == ' ' || peekChar() == '\t'))
            getChar();
    }

    private boolean isOperation(char c) {
        return c == '|' || c == '&' || c == '>' || c == '!';
    }
}