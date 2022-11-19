package ast;

import util.UnimplementedException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public sealed interface Formula permits Formula.And, Formula.AppliedConstant, Formula.Constant, Formula.Equals, Formula.Exists, Formula.ForAll, Formula.Implies, Formula.In, Formula.Not, Formula.Or, Variable {
    record Or(Formula a, Formula b) implements Formula {
    }

    static Or or(Formula a, Formula b) {
        return new Or(a, b);
    }

    record And(Formula a, Formula b) implements Formula {
    }

    static And and(Formula a, Formula b) {
        return new And(a, b);
    }

    record Implies(Formula poprzednik, Formula nastepnik) implements Formula {
    }

    static Implies implies(Formula a, Formula b) {
        return new Implies(a, b);
    }

    record Exists(Variable var, Formula f) implements Formula {
    }

    static Exists exists(Variable var, Formula f) {
        return new Exists(var, f);
    }

    static Formula existsOne(Function<Variable, Formula> varToFormula) {
        var x = Variable.local("x");
        var y = Variable.local("y");
        return exists(y, forall(x, iff(varToFormula.apply(x), eql(x, y))));
    }

    record ForAll(Variable var, Formula f) implements Formula {
    }

    static Formula forall(Variable var, Formula f) {
        return new ForAll(var, f);
    }

    record Not(Formula f) implements Formula {
    }

    static Not not(Formula f) {
        return new Not(f);
    }

    // Formally, let φ \varphi be any formula in the language of ZFC with all free variables among x , z , w 1 , … , w n {\displaystyle x,z,w_{1},\ldots ,w_{n}} ( y y is not free in φ \varphi ).
    record Constant(String name, List<Variable> freeVariables, Formula formula) implements Formula {
        public boolean isAtom() {
            return arity() == 0;
        }

        public int arity() {
            return freeVariables.size();
        }

        public boolean isFunction() {
            return !isAtom();
        }
    }

    record AppliedConstant(Constant fi, List<Formula> args)
            implements Formula {

        // todo assert fi.arity == args.len
        // todo assert free variables
    }

    static AppliedConstant appliedConstant(Constant fi, List<Formula> args) {
        if (!(fi.freeVariables.size() == args.size()))
            throw new RuntimeException(" fi.freeVariables.size() == args.size()");
        return new AppliedConstant(fi, args);
    }

    /////////
    record In(Formula element, Formula set) implements Formula {
    }

    static In in(Formula element, Formula set) {
        return new In(element, set);
    }

    record Equals(Formula a, Formula b) implements Formula {
    }

    static Equals eql(Formula a, Formula b) {
        return new Equals(a, b);
    }

    static Formula iff(Formula a, Formula b) {
        return new And(new Implies(a, b), new Implies(b, a));
    }

    default   Set<Variable> findFreeVariables() {
        FreeVarFinder freeVarFinder = new FreeVarFinder(this);
        return freeVarFinder.apply();
    }

    default boolean equalsF(Formula other) {
        return switch (this) {

            case And and -> {

                var r = other instanceof And && and.a.equalsF(((And) other).a()) && and.b.equalsF(((And) other).b());
                if (!r) {
                    System.out.println("co jes");
                }
                yield r;
            }
            case ForAll forAll -> {
                if (other instanceof ForAll ofa) {
                    Formula apply = new Subst(forAll.var(), ofa.var()).apply(forAll.f());
                    Formula f = ofa.f();
                    yield apply.equalsF(f);
                } else {
                    yield false;
                }
            }

            case AppliedConstant appliedConstant -> {
                if (other instanceof AppliedConstant oap) {
                    var r = appliedConstant.fi.equalsF(oap.fi());
                    for (int i = 0; r && i < appliedConstant.args().size(); i++) {
                        r = r && appliedConstant.args.get(i).equalsF(oap.args().get(i));
                    }
                    yield r;
                } else yield false;
            }
            case Constant costant ->
                // teorerycznie wincyj by trzeba, ale to wystarczy na razie
                    costant.equals(other);
            case Equals equals ->{
                if (other instanceof Equals) {
            var r = equals.a().equalsF(((Equals) other).a());
           r = r && equals.b().equalsF(((Equals) other).b());
            yield r;
            } else yield false;
        }
            case Exists exists -> {
                if (other instanceof  Exists e){

                    Formula apply = new Subst(exists.var(), e.var()).apply(exists.f());
                    Formula f = e.f();
                    yield apply.equalsF(f);
                }else yield false;
            }
            case Implies implies -> {
                if (other instanceof Implies o) {
                    yield implies.poprzednik().equalsF(o.poprzednik()) && implies.nastepnik.equalsF(o.nastepnik());
                } else yield false;
            }
            case In in -> {
                if (other instanceof In o) {
                    var a = in.element().equalsF(o.element());
                    var b = in.set.equalsF(o.set);
                    yield a && b;
                } else yield false;
            }
            case Not not -> other instanceof  Not && not.f().equalsF(((Not) other).f());
            case Or or ->
                other instanceof Or && or.a().equalsF(((Or) other).a())&& or.b().equalsF(((Or) other).b());


            case Variable v -> other instanceof Variable && v.equals(other);
        };
    }

}



class FreeVarFinder {
    private final Formula formula;
    Set<Variable> znalezione = new HashSet<>();
    Set<Variable> spokowe = new HashSet<>();

    public FreeVarFinder(Formula formula) {

        this.formula = formula;
    }

    Set<Variable> apply() {
        qqqq(formula);
        return znalezione;
    }


    void dodaj(Variable v) {
        if (!spokowe.contains(v))
            znalezione.add(v);
    }

    private void qqqq(Formula formula) {
        switch (formula) {

            case Formula.And and -> {
                qqqq(and.a());
                qqqq(and.b());
            }
            case Formula.AppliedConstant appliedConstant -> {
                qqqq(appliedConstant.fi());
                appliedConstant.args().forEach(this::qqqq);
            }
            case Formula.Constant constant -> {
                boolean[] qw = new boolean[constant.arity()];
                for(int i=0; i< constant.arity();i++){
                    var v = constant.freeVariables().get(i);
                    qw[i] = spokowe.add(v);
                }
qqqq(           constant.formula());

                for(int i=0; i< constant.arity();i++){
                    if (qw[i])
                        spokowe.remove(
                    constant.freeVariables().get(i));
                }

            }
            case Formula.Equals equals -> {
                qqqq(equals.a());
                qqqq(equals.b());
            }
            case Formula.Exists exists -> {
                var nieByloWczesniej = spokowe.add(exists.var());
                qqqq(exists.f());
                if(nieByloWczesniej)
                    spokowe.remove(exists.var());
            }
            case Formula.ForAll forAll -> {
                var nieByloWczesniej = spokowe.add(forAll.var());
                qqqq(forAll.f());
                if (nieByloWczesniej) {
                    spokowe.remove(forAll.var());
                }
            }
            case Formula.Implies implies -> {
               qqqq(implies.poprzednik());
               qqqq(implies.nastepnik());
            }
            case Formula.In in -> {
                qqqq(in.element());
                qqqq(in.set());
            }
            case Formula.Not not -> {
                qqqq(not.f());
            }
            case Formula.Or or -> {
                qqqq(or.a());
                qqqq(or.b());
            }
            case Variable local -> {
                dodaj(local);
            }
        }
    }

}