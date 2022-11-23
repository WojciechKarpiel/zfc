package test;

import static org.junit.jupiter.api.Assertions.*;

import ast.Formula;
import ast.Interp;
import ast.Metadata;
import ast.Variable;
import org.junit.jupiter.api.Test;
import parser.Aster;
import parser.Parser;
import parser.Position;
import parser.Span;
import pisarz.Wypisz;

import static ast.Formula.*;

class AsterTest {

    @Test
    void noToEloPustyZbior(){
         String wejscie = """
                 (extractWitness
                   pustego
                   p1 p1P
                   (extractWitness
                     pustego
                     p2 p2P
                     (chain
                       impliesElo
                       (apply (apply ekstensionalności p1) p2)
                       (modusPonens
                         impliesElo
                       
                         (forall
                           jakiśZbiór
                           (and
                             (implies
                               (applyConstant (constant elo () (in jakiśZbiór p1)) ())
                               implX
                               (exFalsoQuodlibet
                                 (apply p1P jakiśZbiór)
                                 implX
                                 (applyConstant (constant hehe () (in jakiśZbiór p2)) ())
                                 q q
                               )
                             )
    
                             (implies
                               (applyConstant (constant elo () (in jakiśZbiór p2)) ())
                               implX
                               (exFalsoQuodlibet
                                 (apply p2P jakiśZbiór)
                                 implX
                                 (applyConstant (constant hehe () (in jakiśZbiór p1)) ())
                                 q q
                               )
                             )
                           )
                         )
                         wynik
                         wynik
                       )
                     )
                   )
                 )""";

         var tt  =Parser.ogar(wejscie);

         var t= Aster.doAst(tt);

        Formula interpd = Interp.interp(t);

        String chcianyWynik = """
                (forall x
                  (implies
                    (forall n (not (in n x)))
                    (forall y
                      (implies
                        (forall n (not (in n y)))
                        (= x y)))))
                """;
        var pusteRowneHip = Aster.parseFormula(Parser.ogar(chcianyWynik));

        Formula formula = InterpTest.pusteZbiorySaRowne();
        assertTrue( formula.equalsF(pusteRowneHip));
        assertTrue( formula.equalsF(interpd));
        assertTrue( pusteRowneHip.equalsF(interpd));
        System.out .println(Wypisz.doNapisu(interpd) );
    }

    @Test
    void parseFormula() {

        String input = """
                (forall x
                  (exists y
                    (and x y)))""";
        var f = Aster.parseFormula(Parser.ogar(input));

        var x = varRef("x_");
        var y = varRef("y_");
        var expected = forall(x, exists(y, and(x, y)));
        assertTrue(expected.equalsF(f));

        assertEquals(f.metadata().getSpan(), (new Metadata(new Position(0, 0), new Position(2, 14))).getSpan());
        ForAll f1 = (ForAll) f;
        assertEquals(f1.var().metadata().getSpan(), new Span(new Position(0, 8), new Position(0, 9)));

        Exists f2 = (Exists) f1.f();
        var and =((And) f2.f());
        assertEquals(f1.var(), and.a());
        assertEquals(f2.var(), and.b());
    }
}