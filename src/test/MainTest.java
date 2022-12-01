package test;

import ast.Formula;
import ast.Interp;
import org.junit.jupiter.api.Test;
import parser.Aster;
import pisarz.Wypisz;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    @Test
    void częśćWspólna() throws IOException {

        // uwaga przestawione a i b - naprawić
        var celq = """
                (forall b
                  (forall a
                    (exists x
                      (forall e
                        (iff
                          (in e x)
                          (and (in e a) (in e b)) 
                        )
                      )
                    )
                  )
                )""";


        var cel = """
                (forall a
                  (forall b
                    (exists x
                      (forall e
                        (iff
                          (in e x)
                          (and (in e a) (in e b)) 
                        )
                      )
                    )
                  )
                )""";
        String fis = "(constant fi (x b p1) (in x p1))";
        String pdz = "(podzbiorów" + fis + ")";

        String chcm = String.format("""
                (chcem %s (constant () %s))""", pdz, celq);
        Formula interp = Interp.interp(chcm);

        String chn = "(chain celq  " + chcm + " " +
                """
                        (forall nx 
                          (forall ny
                             (apply (apply celq ny) nx) 
                           )
                        )""" +
                ")";

        String chc = String.format("(chcem %s (constant () %s))", chn, cel);
        var chcq = Aster.doAst(chc);
        Formula interp2 = Interp.interp(chcq);
        String x = Wypisz.doNapisu(chcq);
        System.out.println(x); // meh aksjomat wypisz :(
        System.out.println(Wypisz.doNapisu(interp2, true));

        assertTrue(Interp.interp(x).equalsF(interp2));
        // andElim => andImpl

    }
}