package test;

import ast.Formula;
import ast.Interp;
import org.junit.jupiter.api.Test;
import pisarz.Wypisz;

import java.io.IOException;

class MainTest {

    @Test
    void częśćWspólna() throws IOException {

        // uwaga przestawione a i b - naprawić
        var cel = """
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


        String fis = "(constant fi (x b p1) (in x p1))";
        String pdz = "(podzbiorów" + fis + ")";

        String chcm = String.format("""
                (chcem %s (constant () %s))""", pdz, cel);
        Formula interp = Interp.interp(chcm);
        System.out.println(Wypisz.doNapisu(interp, true));

    }
}