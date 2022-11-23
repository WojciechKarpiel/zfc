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

    static final Map<String, Supplier<Formula>> AXIOMS = Map.of(
            "ekstensionalności", () -> ZFC.EXTENSIONALITY,
            "pary", () -> ZFC.PARY,
            "pustego", ZFC::PUSTY
    );

    public static Formula parseFormula(TokenTree tokenTree) {
        return new Aster().internalPrsF(tokenTree);
    }

    private Formula internalPrsF(TokenTree tree) {
        return switch (tree) {
            case TokenTree.Leaf leaf -> {// aksjomaty?


                var ax = AXIOMS.get(leaf.s());
                if (ax != null) {
                    yield ax.get();
                }

             yield   Formula.varRef(Objects.requireNonNull(vars.get(leaf.s())), leaf.getMetadata());
            }
            case TokenTree.Branch branch -> {
                List<TokenTree> thisTrees = branch.trees();
                var hd = (TokenTree.Leaf) thisTrees.get(0);
                String hds = hd.s();
                switch (hds) {
                    case "~":
                    case "not":
                    case "nie":
                        yield Formula.not(internalPrsF(thisTrees.get(1)) );
                    case "=":
                    case "eq":
                    case "eql":
                    {
                        var a =internalPrsF(thisTrees.get(1));
                        var b =internalPrsF(thisTrees.get(2));
                     yield   Formula.eql(a,b, branch.getMetadata());
                    }
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
                    case "in":
                        yield Formula.in(internalPrsF(thisTrees.get(1)), internalPrsF(thisTrees.get(2)), branch.getMetadata());
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
                        throw new UnimplementedException(hds +" nie znam w " + hd.getMetadata().getSpan());

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
            case TokenTree.Leaf leaf -> {
                if (leaf.s().equals("???")) yield null;

                if (AXIOMS.containsKey(leaf.s())) {
                    yield Ast.formulaX(internalPrsF(leaf), leaf.getMetadata());
                }
                Variable obj = vars.get(leaf.s());
                if (obj==null){
                    throw  new ZfcException(leaf.s() + " nie znana zmienna w " + leaf.p());
                }
                yield  Ast.astVar(Objects.requireNonNull(obj), leaf.getMetadata());
            }
            case TokenTree.Branch branch -> {
                List<TokenTree> thisTrees = branch.trees();
                var wholeMeta = branch.getMetadata();
                Function<Integer, Ast> parseSubtree = i -> internalPrsA(thisTrees.get(i));
                Function<Integer, TokenTree.Leaf> getLeaf = i -> ((TokenTree.Leaf) (thisTrees.get(i)));
                var hd = (TokenTree.Leaf) thisTrees.get(0);
                String hds = hd.s();
                switch (hds) {
                    case "modusPonens":
                    {
                        var w = parseSubtree.apply(1);
                        var p = parseSubtree.apply(2);
                        var s = Variable.local(getLeaf.apply(3).s() );
                        var prev=put(s);
                        var b =parseSubtree.apply(4);
                        put(s.getName(),prev);
                        yield Ast.modusPonens(w,p,s,b, wholeMeta);
                    }
                    case "chain":
                    {

                        var v= Variable.local(getLeaf.apply(1).s());
                            var e= parseSubtree.apply(2);
                            var prev = put(v);
                            var rest = parseSubtree.apply(3);
                            put(v.getName(), prev);

                        yield Ast.chain(v,e,rest, wholeMeta);
                    }
                    case "app":
                    case "apply":
                    {
                  yield      Ast.apply(parseSubtree.apply(1) ,parseSubtree.apply(2),wholeMeta);
                    }
                    case "forAll":
                    case "forall":
                    {
    var  v = Variable.local(getLeaf.apply(1).s());
                        var prev = put(v);
                        var r= Ast.introForAll(Ast.astVar(v,getLeaf.apply(1).getMetadata()), parseSubtree.apply(2), wholeMeta);
                        put(v.getName(),prev);
                        yield r;
                    }
                    case "impl":
                    case "implies":
                    {
                        var ap =(Formula.AppliedConstant)  internalPrsF(thisTrees.get(1));
                        var n = Variable.local(getLeaf.apply(2).s());
                        var prev=put(n);
    var r=                       Ast. introImpl(ap,n, parseSubtree.apply(3),wholeMeta);
                        put(n.getName(), prev);
                        yield r;
                    }
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
                    case "constant": {
                        Formula constant = this.internalPrsF(branch);
                        Common.assertC(constant instanceof Formula.Constant);
                        yield Ast.formulaX(constant, wholeMeta);
                    }
                    case "applyConstant":
                    case "appliedConstant": {
                        Formula constant = this.internalPrsF(branch);
                        Common.assertC(constant instanceof Formula.AppliedConstant);
                        yield Ast.formulaX(constant, wholeMeta);
                    }
                    case "extractWitness":{
                        //extractWitness(Ast sigma, Variable witness, Variable proof, Ast body, Metadata m)
                        var sigma = parseSubtree.apply(1);
                        var witness = Variable.local(getLeaf.apply(2).s());
                        var proof = Variable.local(getLeaf.apply(3).s());
                        Ast body;
                        {
                            var prevA = put(witness.getName(), witness);
                            var prevB = put(proof.getName(), proof);
                            body = parseSubtree.apply(4);
                            put(witness.getName(), prevA);
                            put(proof.getName(), prevB);
                        }
                        yield Ast.extractWitness(sigma,witness,proof,body, wholeMeta);

                    }
                    case "exFalsoQuodlibet":
                    case "exFalsoSequiturQuodlibet":
                    {
                        // (Ast not, Ast aJednak, Formula.AppliedConstant cnstChciany, Variable v, Ast body)
                        var not =parseSubtree.apply(1);
                        var aJednak =parseSubtree.apply(2);
                        var cnst = (Formula.AppliedConstant) internalPrsF(thisTrees.get(3));
                        var v = Variable.local(getLeaf.apply(4).s());

                        var prev = put(v);
                        var  b = parseSubtree.apply(5);
                        var r = Ast.exFalsoQuodlibet(not,aJednak,cnst,v, b,wholeMeta);
                        put(v.getName(),prev);
                        yield r;

                    }
                    case "???": yield null;
                    default:


                        throw new UnimplementedException( hds);

                }
            }
        };
    }


    private Variable put( Variable.Local variable) {
        return put(variable.getName(),variable);
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
