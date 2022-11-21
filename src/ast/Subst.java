package ast;

import util.Common;
import util.UnimplementedException;

import java.util.Map;

public record Subst(Map<Variable, Formula> map)  {
    public Subst(Formula.VarRef v, Formula f) {
        this(v.variable(),f);
    }
    public  Subst(Variable v, Formula f) {
        this(Map.of(v,f));
    }

    public Formula apply(Formula f){
       return switch (f){
            case Formula.VarRef v -> map().getOrDefault(v.variable(), f);

            case Formula.And and-> Formula.and(apply(and.a()) , apply(and.b()));
            case Formula.AppliedConstant appliedConstant ->
                    Formula.appliedConstant((Formula.Constant) apply(appliedConstant.fi()) ,
                            appliedConstant.args().stream().map(this::apply).toList()
                            , appliedConstant.metadata());
            case Formula.Constant costant -> {
                costant.freeVariables().forEach(v -> Common.assertC( !map.containsKey(v)));
               yield   Formula.constant(costant.name(), costant.freeVariables(), apply(costant.formula()));
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
        if (!( !map.containsKey(v))) throw new RuntimeException(" !map.containsKey(variable)");
        return v;
    }

    public Ast apply(Ast ast){
        return switch (ast){

            case Ast.Apply apply -> Ast.apply(apply( apply.fn()), apply( apply.arg() ));
            case Ast.ElimAnd elimAnd -> throw new UnimplementedException();
            case Ast.ExtractWitness extractWitness -> Ast.extractWitness(apply(extractWitness.sigma()),
                   checkVar( extractWitness.witness()), checkVar( extractWitness.proof()),apply(extractWitness.body()) );
            case Ast.FormulaX formulaX -> Ast.formulaX( apply(formulaX.f()));
            case Ast.AstVar v -> {
                Formula orDefault = map().getOrDefault( v.variable(), Formula.varRef(v.variable(),v.metadata()) /* edgy ? */);

                yield Ast.formulaX(orDefault);
            } // eee

            case Ast.ModusPonens modusPonens -> Ast.modusPonens(apply(modusPonens.wynikanie()), apply(modusPonens.poprzednik()) , modusPonens.witness(), apply(modusPonens.body()));
            case Ast.Chain chain -> {

                if (map.containsKey(chain.v())) throw new RuntimeException(" !map.containsKey(chain.variable())");

                yield Ast.chain(chain.v(), apply(chain.e()),apply(chain.rest()), chain.metadata());
            }
            case Ast.ExFalsoQuodlibet exFalsoQuodlibet -> Ast.exFalsoQuodlibet(apply(exFalsoQuodlibet.not()),apply(exFalsoQuodlibet.aJednak()),
                    (Formula.AppliedConstant)  apply(    exFalsoQuodlibet.cnstChciany()), checkVar(exFalsoQuodlibet.v()), apply(exFalsoQuodlibet.body()));
            case Ast.IntroAnd introAnd -> Ast.introAnd(apply(introAnd.a()), apply(introAnd.b()), introAnd.metadata());
            case Ast.IntroForall introForall -> Ast.introForAll( checkVar(introForall.v()), apply(introForall.body()));
            case Ast.IntroImpl introImpl -> Ast.introImpl((Formula.AppliedConstant) apply(introImpl.pop()),checkVar(introImpl.v()), apply(introImpl.nast())   );
        };
    }

}
