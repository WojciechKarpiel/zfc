package ast;


sealed public interface Ast permits Ast.Apply, Ast.Chain, Ast.ElimAnd, Ast.ExFalsoQuodlibet, Ast.ExtractWitness, Ast.FormulaX, Ast.IntroAnd, Ast.IntroForall, Ast.IntroImpl, Ast.ModusPonens, Ast.AstVar {

Metadata metadata();
    record Apply(Ast fn, Ast arg, Metadata metadata) implements Ast{}

    static Apply apply(Ast fn, Ast arg){
        return apply(fn,arg,Metadata.EMPTY);
    }

    static Apply apply(Ast fn, Ast arg, Metadata empty) {
        return new Apply(fn,arg,empty);
    }


    record ExtractWitness(Ast sigma,  Variable witness, Variable proof,  Ast body, Metadata metadata  ) implements Ast{}

    static ExtractWitness extractWitness(Ast sigma, Variable witness, Variable proof, Ast body, Metadata m){
        return new ExtractWitness(sigma,witness,proof,body,m);
    }
    static ExtractWitness extractWitness(Ast sigma, Variable witness, Variable proof, Ast body){
        return extractWitness(sigma, witness, proof, body, Metadata.EMPTY);
    }

    record ModusPonens(Ast wynikanie,  Ast poprzednik, Variable witness,  Ast body, Metadata metadata  ) implements Ast{}

    static ModusPonens modusPonens(Ast wynikanie,  Ast poprzednik, Variable witness,  Ast body  ){
        return Ast.modusPonens(wynikanie,poprzednik,witness,body,Metadata.EMPTY);
    }
    static ModusPonens modusPonens(Ast wynikanie,  Ast poprzednik, Variable witness,  Ast body, Metadata metadata  ){
        return new ModusPonens(wynikanie,poprzednik,witness,body,metadata);
    }

    // użytkownikowi wolno tylko aksjomaty tu wprowadzać
    record FormulaX(Formula f, Metadata metadata) implements Ast{}
    static FormulaX formulaX(Formula f){
        return Ast.formulaX(f,Metadata.EMPTY);
    }
    static FormulaX formulaX(Formula f,Metadata metadata){
        return new FormulaX(f,metadata);
    }


    record ElimAnd(Ast and, Variable a, Variable b, Ast body, Metadata metadata) implements Ast    {}

    static ElimAnd elimAnd(Ast and, Variable a, Variable b, Ast body){
        return Ast.elimAnd(and,a,b,body,Metadata.EMPTY);
    }
    static ElimAnd elimAnd(Ast and, Variable a, Variable b, Ast body, Metadata metadata){
        return new ElimAnd(and,a,b,body,metadata);
    }

    record ExFalsoQuodlibet(Ast not, Ast aJednak, Formula.AppliedConstant cnstChciany, Variable v, Ast body, Metadata metadata) implements  Ast{}

    static ExFalsoQuodlibet exFalsoQuodlibet(Ast not, Ast aJednak, Formula.AppliedConstant cnstChciany, Variable v, Ast body){
        return Ast.exFalsoQuodlibet(not,aJednak,cnstChciany,v,body,Metadata.EMPTY);
    }
    static ExFalsoQuodlibet exFalsoQuodlibet(Ast not, Ast aJednak, Formula.AppliedConstant cnstChciany, Variable v, Ast body, Metadata metadata){
            return  new ExFalsoQuodlibet(not,aJednak,cnstChciany,v,body,metadata);
    }


    record IntroForall(Variable v, Ast body, Metadata metadata) implements Ast{}
    static IntroForall introForAll(Variable v, Ast body){
        return Ast.introForAll(v,body,Metadata.EMPTY);
    }
    static IntroForall introForAll(Variable v, Ast body, Metadata m){
        return  new IntroForall(v,body,m);
    }
    record IntroAnd(Ast a, Ast b, Metadata metadata) implements Ast{}
    static IntroAnd introAnd(Ast a, Ast b){
        return Ast.introAnd(a,b,Metadata.EMPTY);
    }
    static IntroAnd introAnd(Ast a, Ast b, Metadata m){
        return  new IntroAnd(a,b,m);
    }
    record IntroImpl(Formula.AppliedConstant pop, Variable v, Ast nast, Metadata metadata) implements Ast{}
    static IntroImpl introImpl(Formula.AppliedConstant p,Variable v, Ast n){
        return Ast.introImpl(p,v,n,Metadata.EMPTY);
    }
    static IntroImpl introImpl(Formula.AppliedConstant p,Variable v, Ast n, Metadata m){
        return  new IntroImpl(p,v,n,m);
    }

    record Chain(Variable v, Ast e, Ast rest, Metadata metadata) implements  Ast{}

    static Chain chain(Variable v, Ast e, Ast rest){
        return Ast.chain(v,e,rest,Metadata.EMPTY);
    }
    static Chain chain(Variable v, Ast e, Ast rest, Metadata m){
        return new Chain(v,e,rest,m);
    }

     record AstVar(Variable variable, Metadata metadata) implements Ast {
    }
    static AstVar astVar(Variable v){
        return astVar(v,Metadata.EMPTY);
    }
    static AstVar astVar(String s, Metadata m) {
        return Ast.astVar(Variable.local(s),m);
    }
    static AstVar astVar(Variable v, Metadata m) {
        return new AstVar(v,m);
    }
//    record NamedVarUnsafe(String s) implements Ast{}
//    record DefConstant
 }
