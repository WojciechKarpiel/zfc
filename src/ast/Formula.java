package ast;

import java.util.List;
import java.util.function.Function;

public sealed interface Formula permits Formula.And, Formula.AppliedConstant, Formula.Costant, Formula.Equals, Formula.Exists, Formula.ForAll, Formula.Implies, Formula.In, Formula.Not, Formula.Or, Variable {
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

    record Costant(String name, int arity) implements Formula {
        public boolean isAtom() {
            return arity == 0;
        }

        public boolean isFunction() {
            return !isAtom();
        }
    }

    record AppliedConstant(Formula.Costant fi, List<Formula> args)
            implements Formula {
        // todo assert fi.arity == args.len
    }

    /////////
    record In(Variable element, Variable set) implements Formula {
    }

    static In in(Variable element, Variable set) {
        return new In(element, set);
    }

    record Equals(Variable a, Variable b) implements Formula {
    }

    static Equals eql(Variable a, Variable b) {
        return new Equals(a, b);
    }

    static Formula iff(Formula a, Formula b) {
        return new And(new Implies(a, b), new Implies(b, a));
    }
}