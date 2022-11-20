package ast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public sealed interface Formula permits Formula.And, Formula.AppliedConstant, Formula.Constant, Formula.Equals, Formula.Exists, Formula.ForAll, Formula.Implies, Formula.In, Formula.Not, Formula.Or, Formula.VarRef {


    Metadata metadata();

    record VarRef(Variable variable, Metadata metadata) implements  Formula{}

    static VarRef varRef() {
        return Formula.varRef("?");
    }
    static VarRef varRef(String name){
        return varRef(Variable.local(name));
    }
    static VarRef varRef(Variable v){
        return varRef(v, Metadata.EMPTY);
    }

    static VarRef varRef(Variable v, Metadata m){
        return  new VarRef(v,m);
    }

    record Or(Formula a, Formula b, Metadata metadata) implements Formula {
    }

    static Or or(Formula a, Formula b) {
        return or(a, b,Metadata.EMPTY);
    }
    static Or or(Formula a, Formula b, Metadata m) {
        return new Or(a, b,m);
    }

    record And(Formula a, Formula b, Metadata metadata) implements Formula {
    }

    static And and(Formula a, Formula b) {
        return  and(a, b, Metadata.EMPTY);
    }
    static And and(Formula a, Formula b, Metadata metadata) {
        return new And(a, b,metadata);
    }

    record Implies(Formula poprzednik, Formula nastepnik, Metadata metadata) implements Formula {
    }

    static Implies implies(Formula a, Formula b) {
        return implies(a,b, Metadata.EMPTY);
    }
    static Implies implies(Formula a, Formula b,Metadata m) {
        return new Implies(a, b,m);
    }

    record Exists(VarRef var, Formula f, Metadata metadata) implements Formula {
    }

    static Exists exists(VarRef var, Formula f) {
        return exists(var,f, Metadata.EMPTY);
    }
    static Exists exists(VarRef var, Formula f,Metadata m) {
        return new Exists(var, f,m);
    }

    static Formula existsOne(Function<VarRef, Formula> varToFormula) {
        var x =   Formula.varRef("x");
        var y = Formula.varRef("y");
        return exists(y, forall(x, iff(varToFormula.apply(x), eql(x, y))));
    }

    record ForAll(VarRef var, Formula f, Metadata metadata) implements Formula {
    }

    static Formula forall(VarRef var, Formula f) {
        return forall(var, f,Metadata.EMPTY);
    }
    static Formula forall(VarRef var, Formula f, Metadata m) {
        return new ForAll(var, f,m);
    }

    record Not(Formula f, Metadata metadata) implements Formula {
    }

    static Not not(Formula f) {
        return not(f,Metadata.EMPTY);
    }
    static Not not(Formula f,Metadata m) {
        return new Not(f,m);
    }

    // Formally, let φ \varphi be any formula in the language of ZFC with all free variables among x , z , w 1 , … , w n {\displaystyle x,z,w_{1},\ldots ,w_{n}} ( y y is not free in φ \varphi ).
    record Constant(String name, List<Variable> freeVariables, Formula formula, Metadata metadata) implements Formula {
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

    static Constant constant(String name, List<Variable> freeVariables, Formula formula){
        return constant(name,freeVariables,formula,Metadata.EMPTY);
    }
    static Constant constant(String name, List<Variable> freeVariables, Formula formula, Metadata metadata){
        return new Constant(name,freeVariables,formula,metadata);
    }

    record AppliedConstant(Constant fi, List<Formula> args, Metadata metadata)
            implements Formula {

        // todo assert fi.arity == args.len
        // todo assert free variables
    }

    static AppliedConstant appliedConstant(Constant fi, List<Formula> args) {
        return appliedConstant(fi,args,Metadata.EMPTY);
    }
    static AppliedConstant appliedConstant(Constant fi, List<Formula> args, Metadata m) {
        if (!(fi.freeVariables.size() == args.size()))
            throw new RuntimeException(" fi.freeVariables.size() == args.size()");
        return new AppliedConstant(fi, args,m);
    }

    /////////
    record In(Formula element, Formula set, Metadata metadata) implements Formula {
    }

    static In in(Formula element, Formula set) {
        return in(element, set, Metadata.EMPTY);
    }
    static In in(Formula element, Formula set, Metadata m) {
        return new In(element, set,m );
    }

    record Equals(Formula a, Formula b, Metadata metadata) implements Formula {
    }

    static Equals eql(Formula a, Formula b) {
        return eql(a,b,Metadata.EMPTY);
    }
    static Equals eql(Formula a, Formula b, Metadata m) {
        return new Equals(a, b,m);
    }

    static Formula iff(Formula a, Formula b) {
        return iff(a,b, Metadata.EMPTY);
    }
    static Formula iff(Formula a, Formula b, Metadata m ) {
        return and(implies(a, b,m), implies(b, a,m),m);
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
                        r = appliedConstant.args.get(i).equalsF(oap.args().get(i));
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


            case VarRef v -> other instanceof VarRef && v.equals(other);
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
                var nieByloWczesniej = spokowe.add(exists.var().variable());
                qqqq(exists.f());
                if(nieByloWczesniej)
                    spokowe.remove(exists.var().variable());
            }
            case Formula.ForAll forAll -> {
                var nieByloWczesniej = spokowe.add(forAll.var().variable());
                qqqq(forAll.f());
                if (nieByloWczesniej) {
                    spokowe.remove(forAll.var().variable());
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
            case Formula.VarRef local -> {
                dodaj(local.variable());
            }
        }
    }

}