package test;

import static org.junit.jupiter.api.Assertions.*;

import ast.Metadata;
import ast.Variable;
import org.junit.jupiter.api.Test;
import parser.Aster;
import parser.Parser;
import parser.Position;
import parser.Span;

import static ast.Formula.*;

class AsterTest {

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