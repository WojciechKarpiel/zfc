package ast;


// TODO + metadata i wtedy `equals` inne i hashcode
sealed public interface Ast permits Ast.Apply, Ast.Chain, Ast.ElimAnd, Ast.ExtractWitness, Ast.FormulaX, Ast.ModusPonens, Variable {
    record Apply(Ast fn, Ast arg) implements Ast{}
    record ExtractWitness(Ast sigma,  Variable witness, Variable proof,  Ast body  ) implements Ast{}
    record ModusPonens(Ast wynikanie,  Ast poprzednik, Variable witness,  Ast body  ) implements Ast{}

    // użytkownikowi wolno tylko aksjomaty tu wprowadzać
    record FormulaX(Formula f) implements Ast{}

    record ElimAnd(Ast and, Variable a, Variable b, Ast body) implements Ast    {}

    record Chain(Variable v, Ast e, Ast rest) implements  Ast{}

//    record NamedVarUnsafe(String s) implements Ast{}
//    record DefConstant
 }
