package test;

import ast.Formula;
import ast.Interp;
import ast.Metadata;
import org.junit.jupiter.api.Test;
import parser.Aster;
import parser.Parser;
import parser.Position;
import parser.Span;
import pisarz.Wypisz;

import static ast.Formula.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AsterTest {

    public static final String ROWNE_PUSTE_ZBIORY_CHCIANE="""
                (forall x
                  (implies
                    (forall n (not (in n x)))
                    (forall y
                      (implies
                        (forall n (not (in n y)))
                        (= x y)))))
                """;
public static final String ROWNE_PUSTE_ZBIORY =    """
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
                      (constant elo () (in jakiśZbiór p1))
                      implX
                      (exFalsoQuodlibet
                        (apply p1P jakiśZbiór)
                        implX
                        (constant hehe () (in jakiśZbiór p2))
                        q q
                      )
                    )
            
                    (implies
                      (constant elo () (in jakiśZbiór p2))
                      implX
                      (exFalsoQuodlibet
                        (apply p2P jakiśZbiór)
                        implX
                        (constant hehe () (in jakiśZbiór p1))
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
    @Test
    void noToEloPustyZbior(){

        var tt  =Parser.ogar(ROWNE_PUSTE_ZBIORY);

         var t= Aster.doAst(tt);

        Formula interpd = Interp.interp(t);

        var pusteRowneHip = Aster.parseFormula(Parser.ogar(ROWNE_PUSTE_ZBIORY_CHCIANE));

        Formula formula = InterpTest.pusteZbiorySaRowne();
        assertTrue( formula.equalsF(pusteRowneHip));
        assertTrue( formula.equalsF(interpd));
        assertTrue( pusteRowneHip.equalsF(interpd));

        var zeSprawdzeniem = String.format("(chcę %s  (constant _ () %s) )", ROWNE_PUSTE_ZBIORY, ROWNE_PUSTE_ZBIORY_CHCIANE);
        var jeszczeInne = Interp.interp(Aster.doAst(Parser.ogar(zeSprawdzeniem)));
        assertTrue(jeszczeInne.equalsF(InterpTest.pusteZbiorySaRowne()));

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