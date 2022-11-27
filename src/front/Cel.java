package front;

import ast.Ast;
import ast.Formula;
import ast.Metadata;
import ast.Variable;
import util.Common;
import util.vlist.VList;

import java.util.List;
import java.util.Optional;

import static ast.Formula.*;

public class Cel {

    record CtxElem(String name, Variable v, Formula tpeNullable) {
        public Optional<Formula> tpe() {
            return Optional.ofNullable(tpeNullable);
        }

        public CtxElem(String name, Variable v) {
            this(name, v, null);
        }
    }

    //    record PustyCel(Ast.Hole hole, Formula cel){}
//    record PelnyCel(PustyCel p, Ast wypelnienie){}
    private final VList<CtxElem> kontekst;
    private final GoalManager gm;
    private final Formula cel;
    private Ast wynik;

    public Cel(GoalManager gm, Ast.Hole hole, Formula cel, VList<CtxElem> odziedziczone) {
        this.gm = gm;
        this.cel = cel;
//        this.hole = hole;
        this.kontekst = odziedziczone;
        this.wynik = null;
        gm.registerGoal(hole, this);
    }

    public boolean spelniony() {
        return getWynik().isPresent();
    }


    public void intro(String name) {
        Common.assertC(wynik == null);
        switch (cel) {
            case ForAll fa -> {

                var żl = this.kontekst.cons(new CtxElem(name, fa.var().variable()));
                Ast.Hole hole1 = Ast.hole();
                var g = new Cel(gm, hole1, fa.f(), żl);
                this.wynik = Ast.introForAll(Ast.astVar(fa.var().variable(), Metadata.EMPTY),
                        hole1
                        , Metadata.EMPTY);
            }
            case Implies impl -> {
                Variable.Local hehehe = Variable.local(name);
                var zl = this.kontekst
                        .cons(new CtxElem(name, hehehe, impl.poprzednik()));
                ;
                Ast.Hole hole1 = Ast.hole();
                var g = new Cel(gm, hole1, impl.nastepnik(), zl);
                AppliedConstant appliedConstant = appliedConstant(constant("?h", List.of(), impl.poprzednik(), Metadata.EMPTY), List.of(), Metadata.EMPTY);
                this.wynik = Ast.introImpl(appliedConstant, hehehe, Ast.hole(), Metadata.EMPTY);
            }
            default -> throw new IllegalStateException("Unexpected value: " + cel);
        }
    }

    public Optional<Ast> getWynik() {
        return Optional.ofNullable(wynik);
    }
}
