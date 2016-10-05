import data_struct.ArrayBox;
import data_struct.Pair;
import expression.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Validator {
    private HashMap<Expression, ArrayBox<Pair<Expression, Integer>>> mp;
    private ArrayList<Expression> proof;
    private HashMap<Expression, Integer> assumptions;
    private HashMap<Expression, Integer> proved;
    private ArrayList<String> proofStrings, result;

    public Validator(ArrayList<String> assumptionStrings, ArrayList<String> proofStrings) {
        mp = new HashMap<>();
        this.proofStrings = proofStrings;
//        assumptions = new ArrayList<>();
        assumptions = new HashMap<>();
        proof = new ArrayList<>();
        proved = new HashMap<>();
        long start = System.currentTimeMillis();
        for (int i = 0; i < assumptionStrings.size(); i++) {
            Expression e = parse(assumptionStrings.get(i));
            if (!assumptions.containsKey(e))
                assumptions.put(parse(assumptionStrings.get(i)), i);
        }
        proofStrings.forEach(s -> proof.add(parse(s)));
        System.out.println((System.currentTimeMillis() - start) + "ms, parsed");
        result = new ArrayList<>();
    }

    private Expression parse(String s) {
        return new Parser(s).parse();
    }

    private void putInMp(Expression arg1, Expression arg2, int index) {
        if (!mp.containsKey(arg2)) {
            mp.put(arg2, new ArrayBox<>());
        }
        mp.get(arg2).add(new Pair<>(arg1, index));
    }

    private void acceptFormula(int index, String from) {
        Expression e = proof.get(index);
        result.add("(" + (index + 1) + ") " + proofStrings.get(index) + " " + from);
        proved.put(e, index);
        if (e instanceof BinExpression && ((BinExpression) e).op == Operator.IMPL) {
            putInMp(((BinExpression) e).left, ((BinExpression) e).right, index);
        }
    }

    public ArrayList<String> validate() {
        long s = System.currentTimeMillis();
        proving:
        for (int i = 0; i < proof.size(); i++) {
            Expression e = proof.get(i);
            Integer index;
            if ((index = searchFor(e, Axioms.axioms, false)) != -1) {
                acceptFormula(i, "(Сх. акс. " + (index + 1) + ")");
                continue;
            } else if ((index = assumptions.get(e)) != null) {
                acceptFormula(i, "(Предп. " + (index + 1) + ")");
                continue;
            } else if (mp.containsKey(e)) {
                for (Pair<Expression, Integer> from : mp.get(e)) {
                    if (proved.containsKey(from.val1)) {
                        acceptFormula(i, "(M.P. " + (proved.get(from.val1) + 1) + ", " + (from.val2 + 1) + ")");
                        continue proving;
                    }
                }
            }
            result.add("(" + (i + 1) + ") " + proofStrings.get(i) + " (Не доказано)");
        }
        System.out.println((System.currentTimeMillis() - s) + "ms, validated");
        return result;
    }

    private int searchFor(Expression arg, ArrayList<Expression> list, boolean strict) {
        for (int i = 0; i < list.size(); i++) {
            if (recComp(list.get(i), arg, new HashMap<>(), strict))
                return i;
        }
        return -1;
    }

    private boolean recComp(Expression source, Expression arg,
                                       HashMap<Integer, Expression> map, boolean strict) {
        if (source instanceof BinExpression && arg instanceof BinExpression) {
            BinExpression arg1 = (BinExpression) source, arg2 = (BinExpression) arg;
            return source.op == arg.op &&
                    recComp(arg1.left, arg2.left, map, strict) &&
                    recComp(arg1.right, arg2.right, map, strict);
        } else if (source instanceof UnExpression && arg instanceof UnExpression) {
            return source.op == arg.op &&
                    recComp(((UnExpression) source).arg, ((UnExpression) arg).arg, map, strict);
        } else if (source instanceof Variable) {
            Variable currVar = (Variable) source;
            if (strict)
                return arg instanceof Variable && currVar.name.equals(((Variable) arg).name);
            if (map.containsKey(currVar.number)) {
                return map.get(currVar.number).equals(arg);
            } else {
                map.put(currVar.number, arg);
                return true;
            }
        }
        return false;
    }
}