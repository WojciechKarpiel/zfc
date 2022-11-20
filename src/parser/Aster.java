package parser;

import ast.*;
import util.Common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static ast.Ast.*;

public class Aster {


    private Aster() {
        this.vars =new HashMap<>();
    }
    private Map<String, Variable> vars;

    static final Map<String, Formula> AXIOMS = Map.of(
            "ekstensionalności", ZFC.EXTENSIONALITY,
            "pary", ZFC.PARY
    );

    public static Formula parseFormula(TokenTree tokenTree){
        return new Aster().parseFormula_(tokenTree);
    }

    private Formula parseFormula_(TokenTree tree) {
        return switch (tree) {
            case TokenTree.Leaf leaf -> // aksjomaty?
                //todo pos var?
                    Formula.varRef(Objects.requireNonNull(vars.get(leaf.s())), leaf.getMetadata());
            case TokenTree.Branch branch -> {
                List<TokenTree> thisTrees = branch.trees();
                var hd = (TokenTree.Leaf) thisTrees.get(0);
                String hds = hd.s();
                switch (hds) {
                    case "forall":
                    case "exists":
                        var vf = ((TokenTree.Leaf) thisTrees.get(1));
                        var vv =Variable.local(vf.s());
                        var v = Formula.varRef( vv , (vf.getMetadata()));
                        var prev = vars.put(vf.s(), vv);
                        Common.assertC(thisTrees.size() == 3);
                        Formula f;
                        if (hds.equals("forall")) {
                            f = Formula.forall(v, parseFormula_(thisTrees.get(2)), branch.getMetadata());
                        } else {
                            f = Formula.exists(v, parseFormula_(thisTrees.get(2)), branch.getMetadata());
                        }
                        vars.put(vf.s(), prev);
                        yield f;
                    case "or":
                        yield Formula.or(parseFormula_(thisTrees.get(1)), parseFormula_(thisTrees.get(2)), branch.getMetadata());
                    case "and":
                        yield Formula.and(parseFormula_(thisTrees.get(1)), parseFormula_(thisTrees.get(2)), branch.getMetadata());
                    case "implies":
                        yield Formula.implies(parseFormula_(thisTrees.get(1)), parseFormula_(thisTrees.get(2)), branch.getMetadata());
//                    case "constant":
//                        yield Formula.constant()

                    default:
                        Common.fail();
                        yield null;
                }
            }
        };
    }

    public Ast doAstUnsafe(String tree) {
        return doAstUnsafe(Parser.ogar(tree));
    }

    public Ast doAstUnsafe(TokenTree tree) {
        return null;
    }

    private void put(String s, Variable.Local variable) {
        if (vars.containsKey(s)) {
            System.out.println("UWAGA NADPISUJĘ " + s);
        }
        vars.put(s, variable);

    }
}
