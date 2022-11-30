package ast;


import java.util.function.UnaryOperator;

import static ast.Formula.*;

public class DescentF {
    private final UnaryOperator<Formula> pre;
    private final UnaryOperator<Formula> post;

    public DescentF(UnaryOperator<Formula> pre, UnaryOperator<Formula> post) {

        this.pre = pre;
        this.post = post;
    }

    public DescentF(UnaryOperator<Formula> pre) {
        this(pre, UnaryOperator.identity());
    }

    public Formula apply(Formula f) {
        if (pre != null) f = pre.apply(f);
        f = switch (f) {
            case Formula.And and -> and(apply(and.a()), apply(and.b()), and.metadata());
            case Formula.AppliedConstant appliedConstant -> appliedConstant((Constant) apply(appliedConstant.fi()),
                    appliedConstant.args().stream().map(this::apply).toList(),
                    appliedConstant.metadata())
            ;
            case Formula.Constant constant -> constant(constant.name(),
                    constant.freeVariables(),
                    apply(constant.formula())
            )
            ;
            case Formula.Equals equals -> eql(apply(equals.a()), apply(equals.b()), equals.metadata());
            case Formula.Exists exists -> exists(
                    (VarRef) apply(exists.var()),
                    apply(exists.f()),
                    exists.metadata()

            );
            case Formula.ForAll forAll -> Formula.forall(
                    (VarRef) apply(forAll.var()),
                    apply(forAll.f()),
                    forAll.metadata()
            );
            case Formula.Implies implies -> implies(
                    apply(implies.poprzednik()),
                    apply(implies.nastepnik()),
                    implies.metadata()
            );
            case Formula.In in -> in(apply(in.element()), apply(in.set()), in.metadata());
            case Formula.Not not -> not(apply(not.f()), not.metadata());
            case Formula.Or or -> or(apply(or.a()), apply(or.b()), or.metadata())
            ;
            case Formula.VarRef varRef -> varRef;
        };

        if (post != null) f = post.apply(f);
        return f;
    }
}
