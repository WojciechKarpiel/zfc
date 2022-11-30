package ast;


import java.util.ArrayList;
import java.util.function.Function;

import static ast.Formula.*;

/*
CEL: (napisany w losowym miejscu) udowodnić, że istanieje jeden zbiór pusty
 */


public class ZFC {
    private ZFC() {
    }

    public static final Formula EXTENSIONALITY;

    static {
        var a = Formula.varRef("a");
        var b = Formula.varRef("b");
        var x = Formula.varRef("x");
        EXTENSIONALITY =
                forall(a,
                        forall(b,
                                implies(
                                        forall(x,
                                                iff(in(x, a),
                                                        in(x, b))),
                                        eql(a, b))));
    }

    public static final Exists PUSTY()

     {

        var x = Formula.varRef("x");
        var y = Formula.varRef("y");
        return  exists(x, forall(y, not(in(y, x))));
    }

    public static Formula PODZBIOROW(Constant fi) {
        if (!( fi.arity() >= 2)) throw new RuntimeException(" fi.arity() >= 2");
        int n = fi.arity() - 2;
        var ps = new ArrayList<Formula.VarRef>(n);
        for (int i = 1; i <= n; i++) {
            ps.add(Formula.varRef("p" + i));
        }
        var x = Formula.varRef("x");
        var a = Formula.varRef("a");
        var b = Formula.varRef("b");

        var args = new ArrayList<Formula>(fi.arity());
        args.add(x);
        args.add(b);
        args.addAll( ps);
        var ret =
                forall(b, exists(a, forall(x,
                        iff(in(x, a),
                                and(in(x, b),
                                        appliedConstant(fi, args))))));
        for (int i = ps.size() - 1; i >= 0; i--) {
            ret = forall(ps.get(i), ret);
        }
        return ret;
    }

    public static final Formula PARY;

    static {
        var a = Formula.varRef("a");
        var b = Formula.varRef("b");
        var c = Formula.varRef("c");
        var x = Formula.varRef("x");

        PARY =
                forall(a,
                        forall(b,
                                exists(c,
                                        forall(x,
                                                iff(in(x, c),
                                                        or(eql(x, a),
                                                                eql(x, b)))))));
    }

    public static final Formula SUMY;

    static {
        var a = Formula.varRef("a");
        var u = Formula.varRef("u");
        var r = Formula.varRef("r");
        var x = Formula.varRef("x");

        SUMY = forall(r, exists(u, forall(x, iff(in(x, u), exists(a, and(in(x, a), in(a, r)))))));
    }

    public static final Formula POTEGOWY;

    static {
        var x = Formula.varRef("x");
        var y = Formula.varRef("y");
        var z = Formula.varRef("z");
        var p = Formula.varRef("p");

        POTEGOWY = forall(x, exists(p, forall(z,
                iff(in(z, p), forall(y, implies(in(y, z), in(y, x)))))));
    }

    public static final Formula NIESKONCZONOSCI;

    static {
        var x = Formula.varRef("x");
        var a = Formula.varRef("a");
        var b = Formula.varRef("b");
        var c = Formula.varRef("c");
        var d = Formula.varRef("d");
        var e = Formula.varRef("e");

        NIESKONCZONOSCI =
                exists(x, exists(a, and(
                        and(in(a, x), forall(b, not(in(b, a)))),
                        forall(c, implies(in(c, x), exists(d, and(in(d, x), forall(e, iff(in(e, d), or(in(e, c), eql(e, c))))))))
                )));
    }

    public static Formula ZASTEPOWANIA(Constant Θ/*, Variable X, Variable Y*/) {
        int n = Θ.arity() - 3;
        if (!( n >= 0)) throw new RuntimeException(" n >= 0");
        var X = Formula.varRef("X");
        var Y = Formula.varRef("Y");
        var x = Formula.varRef("x");
        var y = Formula.varRef("y");
        var ps = new ArrayList<Formula.VarRef>();
        for (int i = 1; i <= n; i++) ps.add(Formula.varRef("p" + i));

        Function<VarRef, Formula> applyΘ = q -> {
            var args = new ArrayList<Formula>(Θ.arity());
            args.add(x);
            args.add(q);
            args.add(X);
            args.addAll(ps);
            return appliedConstant(Θ, args);
        };
        Formula res = //uwaga "x" w dwóch miejscach
                forall(x,
                        implies(
                                existsOne(applyΘ), exists(Y, forall(y, iff(in(y, Y), exists(x, and(in(x, X), applyΘ.apply(y))))))
                        )
                );

        for (int i = ps.size() - 1; i >= 0; i--) {
            res = forall(ps.get(i), res);
        }

        return forall(X, exists(Y, res));
    }

    private static Formula nonEmpty(VarRef set) {
        var x = Formula.varRef("x");
        return exists(x, in(x, set));
    }

    public static final Formula REGULARNOSCI;

    static {
        var x = Formula.varRef("x");
        var y = Formula.varRef("y");
        var z = Formula.varRef("z");
        REGULARNOSCI = forall(x, implies(nonEmpty(x), exists(y, and(in(y, x), not(exists(z, and(in(z, x), in(z, y))))))));
    }

    public static final Formula WYBORU;
    static {
        var x = Formula.varRef("x");
        var a = Formula.varRef("a");
        var b = Formula.varRef("b");
        var r = Formula.varRef("r");
        var s = Formula.varRef("s");
        var nonEmprty =forall(a, implies(in(a,r), nonEmpty(a)));
        var drw = forall(a,forall(b,implies(
                and(in(a,r),and(in(b,r),not(eql(a,b)))),
                not(exists(x,(and(in(x,a),in(x,b)))))
        ) ));
    WYBORU= forall(r,implies(and(nonEmprty,drw),
            exists(s, forall(a,implies(in(a,r),
                    existsOne(v -> and(in(v,s),in(v,a))))))
            ));
    }

}
