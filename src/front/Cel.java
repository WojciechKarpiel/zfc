package front;

import ast.*;
import parser.Aster;
import parser.TokenTree;
import util.Common;
import util.NieRowne;
import util.vlist.VList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ast.Formula.*;

public class Cel {

    private final Ast.Hole hole;


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
        this.hole = hole;
        this.kontekst = odziedziczone;
        this.wynik = null;
        gm.registerGoal(hole, this);
    }

    public boolean spelniony() {
        return getWynik().isPresent();
    }


    private Optional<CtxElem> znajdz(String s) {
        for (CtxElem c : kontekst) {
            if (c.name.equals(s)) return Optional.of(c);
        }

        return Optional.empty();
    }

    private void assertNoWynik() {
        Common.assertC(wynik == null);
    }


    public void wypelnijKontekstem(String n) {
        assertNoWynik();
        var elem = znajdz(n).orElseThrow();

        elem.tpe().map(t -> t.equalsF(cel)).ifPresent(Common::assertC);
        wynik = Ast.astVar(elem.v(), Metadata.EMPTY);
    }

    public void intro(String name) {
        assertNoWynik();
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
                Constant appliedConstant = (constant("?h" + hole1.hashCode(), List.of(), impl.poprzednik(), Metadata.EMPTY));
                this.wynik = Ast.introImpl(appliedConstant, hehehe, hole1, Metadata.EMPTY);
            }
            default -> throw new IllegalStateException("Unexpected value: " + cel);
        }
    }

    // może leniwa mapa?
    private Map<VarRef, Formula> ctxToMap2() {
        var r = new HashMap<VarRef, Formula>(kontekst.size());
        for (CtxElem c : kontekst) {
            VarRef other = varRef(c.v(), Metadata.EMPTY);

            if (!r.containsKey(other)) {
                r.put(other, c.tpe().orElse(other));
            }
        }
        return r;

    }

    ;

    private Map<String, Variable> ctxToMap() {
        var r = new HashMap<String, Variable>(kontekst.size());
        for (CtxElem c : kontekst) {
            if (!r.containsKey(c.name)) {
                r.put(c.name, c.v());
            }
        }
        return r;
    }

    public void exact(TokenTree term, boolean strict) {
        assertNoWynik();


        Map<String, Variable> ctx = ctxToMap();

        Ast wynik1 = Aster.doAst(term, ctx);
        if (strict) {
            Formula interp = Interp.interp(wynik1, ctxToMap2());

            if (!interp.equalsF(cel)) {
                throw new NieRowne(cel, interp);
            }
        }

        this.wynik = wynik1;

    }

    @SuppressWarnings({"NonAsciiCharacters"})
    public void chain(String zmienna, TokenTree takąchcęzmienną) {
        assertNoWynik();
        final Formula chciane;
//        if (takąchcęzmienną.isPresent()) {
        Map<String, Variable> ctx = ctxToMap();
        chciane = Aster.parseFormula(takąchcęzmienną, ctx);
//        } else chciane = null;
        final var h1 = new Ast.Hole(Metadata.EMPTY);
        var g = new Cel(gm, h1, chciane, kontekst);

        var hRest = new Ast.Hole(Metadata.EMPTY);
        var v = Variable.local(zmienna);
        // todo chciane z wcześniej!
        var nkrest = kontekst.cons(new CtxElem(zmienna, v, chciane));
        var g2 = new Cel(gm, hRest, this.cel, nkrest);

        this.wynik = Ast.chain(v, h1, hRest, Metadata.EMPTY);

    }


    public Optional<Ast> getWynik() {
        return Optional.ofNullable(wynik);
    }

    public Formula f() {
        return cel;
    }

    public Ast.Hole getHole() {
        return hole;
    }

    public VList<CtxElem> kontekst() {
        return kontekst;
    }
}
