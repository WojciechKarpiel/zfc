package test;

import ast.Ast;
import ast.Interp;
import front.Drzwi;
import org.junit.jupiter.api.Test;
import parser.Aster;
import parser.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DrzwiTest {

    @Test
    void repl() {
        var f = "(forall n (implies (forall x (in x n)) (forall x (in x n))))";
        var cel = Aster.parseFormula(Parser.ogar(f));
        var d = new Drzwi(cel);
        var input = """
                intro a
                intro b
                assumption b""";
        var rdr = new BufferedReader(new StringReader(input));
        Ast a;
        try {
            a = d.repl(rdr, 16);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertTrue(Interp.interp(a).equalsF(cel));
    }


    @Test
    void roznica() {

        var cel = Aster.parseFormula("(forall a (forall b (exists x (forall e (iff (in e x) (and (in e a) (not (in e b))))))))");
        var d = new Drzwi((cel));

        var input = """
                intro a
                intro b
                let elo (forall b (forall a (exists x (forall e (iff (in e x) (and (in e a) (not (in e b))))))))
                exact (podzbiorow (constant fi (x b p1) (not (in x p1))))
                exact (apply (apply elo b ) a)
                """;
        var rdr = new BufferedReader(new StringReader(input));
        Ast a;
        try {
            a = d.repl(rdr, 16);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertTrue(Interp.interp(a).equalsF(cel));

    }
    //  (forall a (forall b (exists x (forall e (iff (in e x) (and (in e a) (not (in e b))))))))
// (podzbiorow (constant fi (x b p1) (not (in x p1))))
}