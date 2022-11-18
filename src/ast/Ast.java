package ast;


// TODO + metadata i wtedy `equals` inne i hashcode
sealed public interface Ast permits Ast.Apply, Ast.Chain, Ast.ElimAnd, Ast.ElimNot, Ast.ExtractWitness, Ast.FormulaX, Ast.IntroAnd, Ast.IntroForall, Ast.IntroImpl, Ast.ModusPonens, Variable {
    record Apply(Ast fn, Ast arg) implements Ast{}
    record ExtractWitness(Ast sigma,  Variable witness, Variable proof,  Ast body  ) implements Ast{}
    record ModusPonens(Ast wynikanie,  Ast poprzednik, Variable witness,  Ast body  ) implements Ast{}

    // użytkownikowi wolno tylko aksjomaty tu wprowadzać
    record FormulaX(Formula f) implements Ast{}

    record ElimAnd(Ast and, Variable a, Variable b, Ast body) implements Ast    {}

    record ElimNot(Ast not, Ast aJednak, Formula.Constant cnstChciany, Variable v, Ast body) implements  Ast{}

    record IntroForall(Variable v, Ast body) implements Ast{}
    record IntroAnd(Ast a, Ast b) implements Ast{}
    record IntroImpl(Formula.Constant pop, Variable v, Ast nast) implements Ast{}

    record Chain(Variable v, Ast e, Ast rest) implements  Ast{}

//    record NamedVarUnsafe(String s) implements Ast{}
//    record DefConstant
 }
