package test;

import ast.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import parser.Aster;
import parser.Span;
import util.Common;
import util.ZfcException;
import util.ZlyPoprzednikWynikania;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.function.Function;

import static ast.Formula.in;
import static org.junit.jupiter.api.Assertions.*;
import static ast.Formula.*;
import static ast.Ast.*;

class InterpTest {

    @Test
    void wysypane() {
        String in = """
                (forall x
                  (forall q
                    (modusPonens
                       (implies 
                         (applyConstant (constant elo () x) ())
                         elo
                         elo
                       )
                     q w w
                     )
                  )
                )""";

        var ast = Aster.doAst(in);
        try {
            Interp.interp(ast);
        } catch (ZlyPoprzednikWynikania e) {
            var popZly = Common.spanSubstring(in, e.getPoprzednik().metadata().getSpan());
            var zapodZly = Common.spanSubstring(in, e.getZapodany().metadata().getSpan());

            assertEquals("x", popZly);
            assertEquals("q", zapodZly);
            assertEquals(4, e.getPoprzednik().metadata().getSpan().start().line());
            assertEquals(1, e.getZapodany().metadata().getSpan().start().line());
            System.out.println(e.getMessage());
        }
    }

    @Test
    void andrzejuTosieWysype() {
        var x = varRef("x");
        var y = varRef("y");
        var q = new Ast.AstVar(Variable.local("q"), Metadata.EMPTY);
        var orig = Interp.ALLOW_FREE_VARS;
        var orig2 = Interp.DOPISUJ_SWIADKOW;

        Interp.DOPISUJ_SWIADKOW = false;
        Interp.ALLOW_FREE_VARS = true;
        Function<Ast.AstVar, Ast> elo = ww ->
                Ast.modusPonens(formulaX(
                        implies(x, y),Metadata.EMPTY), ww, q.variable(), q,Metadata.EMPTY);


        var okk = Interp.interp(elo.apply(new Ast.AstVar(x.variable(), Metadata.EMPTY)));

        assertEquals(y, okk);


        assertThrows(ZfcException.class, () -> Interp.interp(elo.apply(new Ast.AstVar(y.variable(), Metadata.EMPTY))));
        Interp.ALLOW_FREE_VARS = orig;
        Interp.DOPISUJ_SWIADKOW = orig2;
    }
//    @Test
//    void interp() {
//        var p1 = Variable.local("p1");
//        var p1P = Variable.local("p1P");
//        var p2 = Variable.local("p2");
//        var p2P = Variable.local("p2P");
//        var impliesElo = Variable.local("impliesElo");
//        var wyniczek = Variable.local("wynik");
//        var jakisZbior = Variable.local("jakis");
//        var implX = Variable.local("i");
//        var qq = Variable.local("qq");
//        Ast kazdyZbiorPustyJestSobieRowny = Ast.extractWitness(Ast.formulaX(ZFC.PUSTY()), p1, p1P,
//                Ast.extractWitness(Ast.formulaX(ZFC.PUSTY()), p2, p2P,
//                        /* tu chcę mieć (Eq p1 p2) */
//                        // problem : skąd wynikanie, żeby zast
//                        Ast.chain(impliesElo,
//                                Ast.apply(Ast.apply(Ast.formulaX(ZFC.EXTENSIONALITY), new Ast.AstVar(p1, Metadata.EMPTY)), Ast.astVar(p2)),
////                            impliesElo
//                                Ast.modusPonens(Ast.astVar(impliesElo),
//                                        /*poprzednikWynikaniaAksjomatyEks*/
//                                        //ForAll[var=x77, f=And[a=Implies[poprzednik=In[element=x77, set=x72], nastepnik=In[element=x77, set=x94]], b=Implies[poprzednik=In[element=x77, set=x94], nastepnik=In[element=x77, set=x72]]]]
//                                        Ast.introForAll(jakisZbior,
//                                                Ast.introAnd(
//                                                        // Implies[poprzednik=In[element=x77, set=x72], nastepnik=In[element=x77, set=x94]]
//                                                        Ast.introImpl(Formula.appliedConstant(Formula.constant("elo", List.of(), in(varRef(jakisZbior), varRef(p1))), List.of()),
//                                                                implX, Ast.exFalsoQuodlibet(Ast.apply(Ast.astVar(p1P), Ast.astVar(jakisZbior)), Ast.astVar(implX),
//                                                                        Formula.appliedConstant(
//                                                                                Formula.constant("hehe", List.of(), in(varRef(jakisZbior), varRef(p2) /*2!*/)), List.of()), qq, Ast.astVar(qq)))
//                                                        ,
//                                                        // tu będzie to samo
//                                                        Ast.introImpl(Formula.appliedConstant(Formula.constant("elo2", List.of(), in(varRef(jakisZbior), varRef(p2))),
//                                                                        List.of()),
//                                                                implX, Ast.exFalsoQuodlibet(Ast.apply(Ast.astVar(p2P), Ast.astVar(jakisZbior)), Ast.astVar(implX),
//                                                                        Formula.appliedConstant(
//                                                                                Formula.constant("hehe2", List.of(), in(varRef(jakisZbior), varRef(p1))),
//                                                                                List.of()), qq, Ast.astVar(qq)))
//                                                )
//                                        )
//
//                                        , wyniczek, Ast.astVar(wyniczek))
//                        )
//                ));
//
//        Formula wynik = Interp.interp(kazdyZbiorPustyJestSobieRowny);
//
//
//        assertTrue(pusteZbiorySaRowne() .equalsF(wynik));
//    }

    public static Formula pusteZbiorySaRowne() {
        Function<Variable, Formula> puste = q -> {
            var n = Variable.local("___");
            return forall(varRef(n,Metadata.EMPTY), not(in(varRef(n,Metadata.EMPTY), varRef(q,Metadata.EMPTY))));
        };

        var x = Variable.local("_1");
        var y = Variable.local("_2");
        return forall(varRef(x,Metadata.EMPTY), implies(puste.apply(x), forall(varRef(y,Metadata.EMPTY),
                implies(puste.apply(y), eql(varRef(x,Metadata.EMPTY), varRef(y,Metadata.EMPTY)))
        )));
    }

    // TODO test co się wyala
}