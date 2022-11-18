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
        var a = new Variable.Local("a");
        var b = new Variable.Local("b");
        var x = new Variable.Local("x");
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

        var x = new Variable.Local("x");
        var y = new Variable.Local("y");
        return  exists(x, forall(y, new Not(in(y, x))));
    }

    public static Formula PODZBIOROW(Constant fi) {
        if (!( fi.arity() >= 2)) throw new RuntimeException(" fi.arity() >= 2");
        int n = fi.arity() - 2;
        var ps = new ArrayList<Variable.Local>(n);
        for (int i = 1; i <= n; i++) {
            ps.add(new Variable.Local("p" + i));
        }
        var x = Variable.local("x");
        var a = Variable.local("a");
        var b = Variable.local("b");

        var args = new ArrayList<Formula>(fi.arity());
        args.add(x);
        args.add(b);
        args.addAll(ps);
        var ret =
                forall(b, exists(a,
                        iff(in(x, a),
                                new And(in(x, b),
                                        appliedConstant(fi, args)))));
        for (int i = ps.size() - 1; i >= 0; i--) {
            ret = forall(ps.get(i), ret);
        }
        return ret;
    }

    public static final Formula PARY;

    static {
        var a = Variable.local("a");
        var b = Variable.local("b");
        var c = Variable.local("c");
        var x = Variable.local("x");

        PARY =
                forall(a,
                        forall(b,
                                exists(c,
                                        forall(x,
                                                iff(in(x, c),
                                                        new Or(eql(x, a),
                                                                eql(x, b)))))));
    }

    public static final Formula SUMY;

    static {
        var a = Variable.local("a");
        var u = Variable.local("u");
        var r = Variable.local("r");
        var x = Variable.local("x");

        SUMY = forall(r, exists(u, forall(x, iff(in(x, u), exists(a, and(in(x, a), in(a, r)))))));
    }

    public static final Formula POTEGOWY;

    static {
        var x = Variable.local("x");
        var y = Variable.local("y");
        var z = Variable.local("z");
        var p = Variable.local("p");

        POTEGOWY = forall(x, exists(p, forall(z,
                iff(in(z, p), forall(y, implies(in(y, z), in(y, x)))))));
    }

    public static final Formula NIESKONCZONOSCI;

    static {
        var x = Variable.local("x");
        var a = Variable.local("a");
        var b = Variable.local("b");
        var c = Variable.local("c");
        var d = Variable.local("d");
        var e = Variable.local("e");

        NIESKONCZONOSCI =
                exists(x, exists(a, and(
                        and(in(a, x), forall(b, not(in(b, a)))),
                        forall(c, implies(in(c, x), exists(d, and(in(d, x), forall(e, iff(in(e, d), or(in(e, c), eql(e, c))))))))
                )));
    }

    public static Formula ZASTEPOWANIA(Constant Θ/*, Variable X, Variable Y*/) {
        int n = Θ.arity() - 3;
        if (!( n >= 0)) throw new RuntimeException(" n >= 0");
        var X = Variable.local("X");
        var Y = Variable.local("Y");
        var x = Variable.local("x");
        var y = Variable.local("y");
        var ps = new ArrayList<Variable>();
        for (int i = 1; i <= n; i++) ps.add(Variable.local("p" + i));

        Function<Variable, Formula> applyΘ = q -> {
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

    private static Formula nonEmpty(Variable set) {
        var x = Variable.local("x");
        return exists(x, in(x, set));
    }

    public static final Formula REGULARNOSCI;

    static {
        var x = Variable.local("x");
        var y = Variable.local("y");
        var z = Variable.local("z");
        REGULARNOSCI = forall(x, implies(nonEmpty(x), exists(y, and(in(y, x), not(exists(z, and(in(z, x), in(z, y))))))));
    }

    public static final Formula WYBORU;
    static {
        var x = Variable.local("x");
        var a = Variable.local("a");
        var b = Variable.local("b");
        var r = Variable.local("r");
        var s = Variable.local("s");
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
