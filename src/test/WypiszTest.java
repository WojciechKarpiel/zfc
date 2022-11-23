package test;

import org.junit.jupiter.api.Test;
import parser.Aster;
import parser.Parser;
import pisarz.Wypisz;

import static org.junit.jupiter.api.Assertions.*;

class WypiszTest {

    @Test
    void doNapisu() {
        var q =Aster.parseFormula(Parser.ogar("(forall x (and x (forall x (and x x) )))"));
        System.out.println((q));
        System.out.println(Wypisz.doNapisu(q));

    }
}