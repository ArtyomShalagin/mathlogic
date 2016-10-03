import data_struct.Pair;
import expression.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Validator {
    private HashMap<Expression, ArrayList<Pair<Expression, Integer>>> mp;
    private ArrayList<Expression> assumptions, proved, proof;
    private HashSet<Expression> provedSet;
    private ArrayList<String> proofStrings;
    private ArrayList<String> result;

    public Validator(ArrayList<String> assumptionStrings, ArrayList<String> proofStrings) {
        mp = new HashMap<>();
        this.proofStrings = proofStrings;
        assumptions = new ArrayList<>();
        proved = new ArrayList<>();
        proof = new ArrayList<>();
        provedSet = new HashSet<>();
        assumptionStrings.forEach(s -> assumptions.add(parse(s)));
        proofStrings.forEach(s -> proof.add(parse(s)));
        result = new ArrayList<>();
    }

    private Expression parse(String s) {
        return new Parser(s).parse();
    }

    private void putInMp(Expression arg1, Expression arg2, int index) {
        if (!mp.containsKey(arg2)) {
            mp.put(arg2, new ArrayList<>());
        }
        mp.get(arg2).add(new Pair<>(arg1, index));
    }

    private void acceptFormula(int index, String from) {
        Expression e = proof.get(index);
        result.add("(" + (index + 1) + ") " + proofStrings.get(index) + " " + from);
        proved.add(e);
        provedSet.add(e);
        if (e instanceof BinExpression && ((BinExpression) e).op == Operator.IMPL) {
            putInMp(((BinExpression) e).left, ((BinExpression) e).right, index);
        }
    }

    public ArrayList<String> validate() {
        proving:
        for (int i = 0; i < proof.size(); i++) {
            Expression e = proof.get(i);
            int index;
            if ((index = searchFor(e, Axioms.axioms, false)) != -1) {
                acceptFormula(i, "(Сх. акс. " + (index + 1) + ")");
                continue;
            } else if ((index = searchFor(e, assumptions, true)) != -1) {
                acceptFormula(i, "(Предп. " + (index + 1) + ")");
                continue;
            } else if (mp.containsKey(e)) {
                for (Pair<Expression, Integer> from : mp.get(e)) {
                    if (provedSet.contains(from.val1)) {
                        acceptFormula(i, "(M.P. " + (proved.indexOf(from.val1) + 1) + ", " + (from.val2 + 1) + ")");
                        continue proving;
                    }
                }
            }
            result.add("(" + (i + 1) + ") " + proofStrings.get(i) + " (Не доказано)");
        }

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