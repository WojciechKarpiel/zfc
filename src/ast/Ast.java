package ast;


// TODO + metadata i wtedy `equals` inne i hashcode
sealed public interface Ast permits Ast.Apply, Ast.Chain, Ast.ElimAnd, Ast.ExFalsoQuodlibet, Ast.ExtractWitness, Ast.FormulaX, Ast.IntroAnd, Ast.IntroForall, Ast.IntroImpl, Ast.ModusPonens, Ast.AstVar {

    // TODO +  METAdata

    record Apply(Ast fn, Ast arg) implements Ast{}
    record ExtractWitness(Ast sigma,  Variable witness, Variable proof,  Ast body  ) implements Ast{}
    record ModusPonens(Ast wynikanie,  Ast poprzednik, Variable witness,  Ast body  ) implements Ast{}

    // użytkownikowi wolno tylko aksjomaty tu wprowadzać
    record FormulaX(Formula f) implements Ast{}

    record ElimAnd(Ast and, Variable a, Variable b, Ast body) implements Ast    {}

    record ExFalsoQuodlibet(Ast not, Ast aJednak, Formula.AppliedConstant cnstChciany, Variable v, Ast body) implements  Ast{}

    record IntroForall(Variable v, Ast body) implements Ast{}
    record IntroAnd(Ast a, Ast b) implements Ast{}
    record IntroImpl(Formula.AppliedConstant pop, Variable v, Ast nast) implements Ast{}

    record Chain(Variable v, Ast e, Ast rest) implements  Ast{}

     record AstVar(Variable variable, Metadata metadata) implements Ast {
    }
    static AstVar astVar(Variable v){
        return astVar(v,Metadata.EMPTY);
    }
    static AstVar astVar(Variable v, Metadata m) {
        return new AstVar(v,m);
    }
//    record NamedVarUnsafe(String s) implements Ast{}
//    record DefConstant
 }
