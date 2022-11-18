package ast;

import util.Common;

import java.util.Map;

public record Subst(Map<Variable, Formula> map)  {
    public Subst(Variable v, Formula f) {
        this(Map.of(v,f));
    }

    public Formula apply(Formula f){
       return switch (f){
            case Variable v -> map().getOrDefault(v, f);

            case Formula.And and-> Formula.and(apply(and.a()) , apply(and.b()));
            case Formula.AppliedConstant appliedConstant -> appliedConstant;
            case Formula.Constant costant -> {
                costant.freeVariables().forEach(v -> Common.assertC( !map.containsKey(v)));
               yield  new Formula.Constant(costant.name(), costant.freeVariables(), apply(costant.formula()));
            }
            case Formula.Equals equals -> Formula.eql(apply(equals.a()),apply(equals.b()));
            case Formula.Exists exists -> Formula.exists(exists.var() /* oby nie zaszło*/,apply(exists.f()));
            case Formula.ForAll forAll -> Formula.forall(forAll.var(),apply(forAll.f()));
            case Formula.Implies implies -> Formula.implies(apply(implies.poprzednik()),apply(implies.nastepnik()));
            case Formula.In in -> Formula.in(apply(in.element()), apply(in.set()));
            case Formula.Not not -> Formula.not(apply(not.f()));
            case Formula.Or or -> Formula.or(apply(or.a()), apply(or.b()));
        };
    }

    private Variable checkVar(Variable v){
        if (!( !map.containsKey(v))) throw new RuntimeException(" !map.containsKey(v)");
        return v;
    }

    public Ast apply(Ast ast){
        return switch (ast){

            case Ast.Apply apply -> new Ast.Apply(apply( apply.fn()), apply( apply.arg() ));
            case Ast.ElimAnd elimAnd -> throw null;
            case Ast.ExtractWitness extractWitness -> new Ast.ExtractWitness(apply(extractWitness.sigma()),
                   checkVar( extractWitness.witness()), checkVar( extractWitness.proof()),apply(extractWitness.body()) );
            case Ast.FormulaX formulaX -> new Ast.FormulaX( apply(formulaX.f()));
            case Variable.Local v -> {
                Formula orDefault = map().getOrDefault(v, v);

                yield new Ast.FormulaX(orDefault);
            } // eee

            case Ast.ModusPonens modusPonens -> new Ast.ModusPonens(apply(modusPonens.wynikanie()), apply(modusPonens.poprzednik()) , modusPonens.witness(), apply(modusPonens.body()));
            case Ast.Chain chain -> {

                if (map.containsKey(chain.v())) throw new RuntimeException(" !map.containsKey(chain.v())");

                yield new Ast.Chain(chain.v(), apply(chain.e()),apply(chain.rest()));
            }
            case Ast.ElimNot elimNot -> new Ast.ElimNot(apply(elimNot.not()),apply(elimNot.aJednak()),
                    (Formula.Constant)  apply(    elimNot.cnstChciany()), checkVar(elimNot.v()), apply(elimNot.body()));
            case Ast.IntroAnd introAnd -> new Ast.IntroAnd(apply(introAnd.a()),
                    apply(introAnd.b())
            );
            case Ast.IntroForall introForall -> new Ast.IntroForall( checkVar(introForall.v()), apply(introForall.body()));
            case Ast.IntroImpl introImpl -> new Ast.IntroImpl((Formula.Constant) apply(introImpl.pop()),checkVar(introImpl.v()), apply(introImpl.nast())   );
        };
    }

}
