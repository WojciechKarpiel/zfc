package ast;


import java.util.Objects;

sealed public interface Ast permits Ast.ElimAnd, Ast.Apply, Ast.AstAxiom, Ast.AstVar, Ast.Chain, Ast.Chcem, Ast.ExFalsoQuodlibet, Ast.ExtractWitness, Ast.FormulaX, Ast.Hole, Ast.IntroAnd, Ast.IntroExists, Ast.IntroForall, Ast.IntroImpl, Ast.ModusPonens {

    Metadata metadata();

    public final class Hole implements Ast {
        private final Metadata metadata;
        private final int id;
        private static int idCounter = 0;


        public Hole(Metadata metadata) {
            this.metadata = metadata;
            this.id = idCounter++;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Hole hole = (Hole) o;
            return id == hole.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public Metadata metadata() {
            return metadata;
        }

        public int getId() {
            return id;
        }
    }

    static Hole hole() {
        return hole(Metadata.EMPTY);
    }

    static Hole hole(Metadata m) {
        return new Hole(m);
    }

    record Chcem(Ast rzecz, Formula.Constant co, Metadata metadata) implements Ast {
    }

    static Chcem chcem(Ast rzecz, Formula.Constant co, Metadata metadata) {
        return new Chcem(rzecz, co, metadata);
    }

    record Apply(Ast fn, Ast arg, Metadata metadata) implements Ast {
    }

    static Apply apply(Ast fn, Ast arg, Metadata m) {
        return new Apply(fn, arg, m);
    }


    record ExtractWitness(Ast sigma,  Variable witness, Variable proof,  Ast body, Metadata metadata  ) implements Ast{}

    static ExtractWitness extractWitness(Ast sigma, Variable witness, Variable proof, Ast body, Metadata m){
        return new ExtractWitness(sigma,witness,proof,body,m);
    }

    record ModusPonens(Ast wynikanie,  Ast poprzednik, Variable witness,  Ast body, Metadata metadata  ) implements Ast{}

    static ModusPonens modusPonens(Ast wynikanie, Ast poprzednik, Variable witness, Ast body, Metadata metadata) {
        return new ModusPonens(wynikanie, poprzednik, witness, body, metadata);
    }

    // użytkownikowi wolno tylko aksjomaty tu wprowadzać
    record FormulaX(Formula f, Metadata metadata) implements Ast {
    }

    static FormulaX formulaX(Formula f, Metadata metadata) {
        return new FormulaX(f, metadata);
    }

    sealed interface AstAxiom extends Ast {
        Formula intoFormula();

        String getName();
    }

    record AstPodzbiorow(Formula.Constant f, Metadata metadata) implements AstAxiom {

        @Override
        public Formula intoFormula() {
            return ZFC.PODZBIOROW(f);
        }

        @Override
        public String getName() {
            return "podzbiorów";
        }
    }


    record ElimAnd(Ast and, Variable a, Variable b, Ast body, Metadata metadata) implements Ast {
    }

    static ElimAnd elimAnd(Ast and, Variable a, Variable b, Ast body, Metadata metadata) {
        return new ElimAnd(and, a, b, body, metadata);
    }

    record ExFalsoQuodlibet(Ast not, Ast aJednak, Formula.Constant cnstChciany, Variable v, Ast body,
                            Metadata metadata) implements Ast {
    }

    static ExFalsoQuodlibet exFalsoQuodlibet(Ast not, Ast aJednak, Formula.Constant cnstChciany, Variable v, Ast body, Metadata metadata) {
        return new ExFalsoQuodlibet(not, aJednak, cnstChciany, v, body, metadata);
    }


    record IntroForall(Ast.AstVar v, Ast body, Metadata metadata) implements Ast {
    }

    static IntroForall introForAll(Ast.AstVar v, Ast body, Metadata m) {
        return new IntroForall(v, body, m);
    }

    record IntroExists(Ast proof, Formula.Constant formula, Metadata metadata) implements Ast {

        public IntroExists withMeta(Metadata m) {
            if (m == metadata) return this;
            else return new IntroExists(proof, formula, m);
        }
    }

    record IntroAnd(Ast a, Ast b, Metadata metadata) implements Ast {
    }

    static IntroAnd introAnd(Ast a, Ast b, Metadata m) {
        return new IntroAnd(a, b, m);
    }

    record IntroImpl(Formula.Constant pop, Variable v, Ast nast, Metadata metadata) implements Ast {
    }

    static IntroImpl introImpl(Formula.Constant p, Variable v, Ast n, Metadata m) {
        return new IntroImpl(p, v, n, m);
    }

    record Chain(Variable v, Ast e, Ast rest, Metadata metadata) implements Ast {
    }

    static Chain chain(Variable v, Ast e, Ast rest, Metadata m) {
        return new Chain(v, e, rest, m);
    }

    record AstVar(Variable variable, Metadata metadata) implements Ast {
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
