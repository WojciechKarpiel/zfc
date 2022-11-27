package front;

import ast.Ast;
import ast.Descent;
import util.ZfcException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GoalManager {


    private final Map<Ast.Hole, Cel> cele = new HashMap<>();

    public void registerGoal(Ast.Hole h, Cel c) {
        cele.put(h, c);
    }

    public Map<Ast.Hole, Cel> getCele() {
        return Collections.unmodifiableMap(cele);
    }

    public Ast recreateAst(Ast.Hole initHole) {
        return new Descent(ast -> {
            if (ast instanceof Ast.Hole) {
                Cel cel = cele.get(initHole);
                return cel.getWynik().orElseThrow(ZfcException::new);
            } else {
                return ast;
            }
        }).descent(initHole);
    }
}
