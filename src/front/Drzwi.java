package front;

import ast.Ast;
import ast.Formula;
import ast.Interp;
import util.ZfcException;
import util.vlist.VList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

public class Drzwi {


    private final GoalManager gm;
    private final Cel pierwotnyCel;
    private Cel obecnyCel;

    public Drzwi(Formula cel) {
        this.gm = new GoalManager();
        var h = Ast.hole();
        Cel cel1 = new Cel(gm, h, cel, VList.empty());
        gm.registerGoal(h, cel1);
        this.pierwotnyCel = cel1;
        this.obecnyCel = cel1;
    }

    public Ast repl() throws IOException {
        var in = System.in;
        var reader = new BufferedReader(new InputStreamReader(in));
        return repl(reader, -1);
    }

    // bufferedreader bo ma `readline`
    public Ast repl(BufferedReader reader, int maxSteps) throws IOException {
        var step = 0;
        while (true) {
            if (maxSteps > 0 && step > maxSteps) {
                throw new ZfcException("może nieskończona pętla w teście?");
            }
            System.out.println("Krok nr " + step);
            step++;
            if (obecnyCel.spelniony()) {
                var nc = nastepny();
                if (nc.isPresent()) {
                    obecnyCel = nc.get();
                } else {
                    System.out.println("Spełniłeś cel : " + pierwotnyCel.f());
                    Ast x = gm.recreateAst(pierwotnyCel.getHole());
                    System.out.println("No to sprawdzam");
                    System.out.println(x);
                    if (!Interp.interp(x).equalsF(pierwotnyCel.f())) {
                        throw new ZfcException("No jednak lipa!");
                    } else {
                        System.out.println("OK!");
                    }

                    return x;
                }
            }
            System.out.println("kontekst:");
            System.out.println(obecnyCel.kontekst().toList());
            System.out.println("cel");
            System.out.println(obecnyCel.f());
            System.out.println("dawaj:");

            var l = reader.readLine();
            if (l == null) {
                System.out.println("olewam");
                return null;
            }
            if (l.startsWith("intro ")) {
                var n = l.substring("intro ".length());
                obecnyCel.intro(n);
                continue;
            } else if (l.startsWith("exact ")) {


                obecnyCel.bezposrednio(l.substring("exact ".length()));
                continue;
            } else if (l.startsWith("assumption ")) {
                var n = l.substring("assumption ".length());
                obecnyCel.wypelnijKontekstem(n);
            } else {
                System.out.println("co? " + l);
                continue;
            }

        }
    }

    private Optional<Cel> nastepny() {
        return gm.getCele().values().stream().filter(c -> !c.spelniony()).findFirst();
    }

}
