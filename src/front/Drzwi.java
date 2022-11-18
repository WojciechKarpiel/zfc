package front;

import ast.Formula;
import ast.Interp;
import ast.Variable;
import org.graalvm.collections.Pair;

import java.util.*;

public class Drzwi {
    private final Queue<Pair<Variable, Formula>> cele;
    private final Map<Variable, Formula> swiadkowie;

    private Interp interp;

    public Drzwi(List<Pair<Variable, Formula>> goals) {
        cele = new ArrayDeque<>();
        cele.addAll(goals);
        swiadkowie = new HashMap<>();
        interp = new Interp();
    }

    public void addVoidGoal(Formula f) {
        cele.add(Pair.create(Variable.local("<NIE>"), f));
    }

    private void finishCurrentGoal() {
        var cel = cele.poll();
        swiadkowie.put(cel.getLeft(), cel.getRight());
        interp.addAssumption(cel.getLeft(), cel.getRight());
//        interp = new Interp();
//        swiadkowie.forEach((v,f)-> interp.addAssumption(v,f));
    }


}
