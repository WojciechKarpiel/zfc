package ast;

import java.util.List;
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

    static Formula exists(Variable var, Formula f) {
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
        if (!( fi.freeVariables.size() == args.size())) throw new RuntimeException(" fi.freeVariables.size() == args.size()");
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

    default boolean equalsF(Formula other) {
        return switch (this) {

            case And and -> other instanceof And && and.a.equalsF((And) other) && and.b.equalsF(((And) other).b());
            case ForAll forAll -> {
                if (other instanceof ForAll ofa) {
                    yield new Subst(forAll.var(), ofa.var()).apply(forAll.f()).equalsF(ofa.f());
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
            case Equals equals -> throw new UnimplementedException();
            case Exists exists -> throw new UnimplementedException();
            case Implies implies -> throw new UnimplementedException();
            case In in -> throw new UnimplementedException();
            case Not not -> throw new UnimplementedException();
            case Or or -> throw new UnimplementedException();
            case Variable v -> other instanceof Variable && v.equals(other);
        };
    }

}

class UnimplementedException extends RuntimeException {

}