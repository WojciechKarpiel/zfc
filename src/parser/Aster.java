package parser;

import ast.*;
import util.Common;
import util.UnimplementedException;
import util.ZfcException;

import java.rmi.dgc.Lease;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class Aster {


    private Aster() {
        this.vars = new HashMap<>();
    }

    private final Map<String, Variable> vars;

    static final Map<String, Formula> AXIOMS = Map.of(
            "ekstensionalności", ZFC.EXTENSIONALITY,
            "pary", ZFC.PARY
    );

    public static Formula parseFormula(TokenTree tokenTree) {
        return new Aster().internalPrsF(tokenTree);
    }

    private Formula internalPrsF(TokenTree tree) {
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
                        var vv = Variable.local(vf.s());
                        var v = Formula.varRef(vv, (vf.getMetadata()));
                        var prev = put(vf.s(), vv);
                        Common.assertC(thisTrees.size() == 3);
                        Formula f;
                        if (hds.equals("forall")) {
                            f = Formula.forall(v, internalPrsF(thisTrees.get(2)), branch.getMetadata());
                        } else {
                            f = Formula.exists(v, internalPrsF(thisTrees.get(2)), branch.getMetadata());
                        }
                        put(vf.s(), prev);
                        yield f;
                    case "or":
                        yield Formula.or(internalPrsF(thisTrees.get(1)), internalPrsF(thisTrees.get(2)), branch.getMetadata());
                    case "and":
                        yield Formula.and(internalPrsF(thisTrees.get(1)), internalPrsF(thisTrees.get(2)), branch.getMetadata());
                    case "implies":
                        yield Formula.implies(internalPrsF(thisTrees.get(1)), internalPrsF(thisTrees.get(2)), branch.getMetadata());
                    case "applyConstant":
                    case "appliedConstant":
                        var fi = (Formula.Constant) internalPrsF(thisTrees.get(1));
                        var argst = ((TokenTree.Branch) thisTrees.get(2)).trees();
                        Common.assertC(argst.size() == fi.arity());
                        var args = argst.stream().map(this::internalPrsF).toList();
                        yield Formula.appliedConstant(fi, args, branch.getMetadata());
                    case "constant":
                        var name = ((TokenTree.Leaf) thisTrees.get(1)).s();
                        var vars = ((TokenTree.Branch) thisTrees.get(2)).trees()
                                .stream().<Variable>map(vw -> {
                                    var q = ((TokenTree.Leaf) vw);
                                    return Variable.local(q.s());
                                }).toList();

                        var ff = internalPrsF(thisTrees.get(3));
                        yield Formula.constant(name, vars, ff, branch.getMetadata());

                    default:
                        var ax = AXIOMS.get(hds);
                        Common.assertC(ax != null);
                        yield ax;

                }
            }
        };
    }

    public static Ast doAst(String tree) {
        return doAst(Parser.ogar(tree));
    }

    public static Ast doAst(TokenTree tree) {
        return new Aster().internalPrsA(tree);
    }

    private Ast internalPrsA(TokenTree tree) {
        return switch (tree) {
            case TokenTree.Leaf leaf -> Ast.astVar(Objects.requireNonNull(vars.get(leaf.s())), leaf.getMetadata());
            case TokenTree.Branch branch -> {
                List<TokenTree> thisTrees = branch.trees();
                var wholeMeta = branch.getMetadata();
                Function<Integer, Ast> parseSubtree = i -> internalPrsA(thisTrees.get(i));
                var hd = (TokenTree.Leaf) thisTrees.get(0);
                String hds = hd.s();
                switch (hds) {
                    case "and":
                        yield Ast.introAnd(parseSubtree.apply(1), parseSubtree.apply(2), wholeMeta);
                    case "andElim": {
                        Ast toDoElom = parseSubtree.apply(1);

                        String vAs = ((TokenTree.Leaf) thisTrees.get(2)).s();
                        Variable vA = Variable.local(vAs);
                        String vBs = ((TokenTree.Leaf) thisTrees.get(3)).s();
                        Variable.Local vB = Variable.local(vBs);
                        Ast poElom;
                        {
                            var prevA = put(vAs, vA);
                            var prevB = put(vBs, vB);
                            poElom = parseSubtree.apply(4);
                            put(vAs, prevA);
                            put(vBs, prevB);
                        }
                        yield Ast.elimAnd(toDoElom, vA, vB, poElom, wholeMeta);
                    }
                    case "constant":
                        Formula constant = this.internalPrsF(branch);
                        Common.assertC(constant instanceof Formula.Constant);
                        yield Ast.formulaX(constant, wholeMeta);


                    default:
                        if (AXIOMS.containsKey(hds)) {
                            yield Ast.formulaX(internalPrsF(branch), wholeMeta);
                        }

                        throw new UnimplementedException();

                }
            }
        };
    }


    private Variable put(String s, Variable variable) {
        if (variable == null) {
            return vars.remove(s);
        }
        if (vars.containsKey(s)) {
            System.out.println("UWAGA NADPISUJĘ " + s);
        }
        return vars.put(s, variable);

    }
}
