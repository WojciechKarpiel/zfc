package front;

import ast.Ast;
import ast.Formula;
import ast.Interp;
import parser.Parser;
import parser.TokenTree;
import pisarz.Wypisz;
import util.Common;
import util.Ref;
import util.ZfcException;
import util.vlist.VList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class Drzwi {

    public static boolean ADDITIONAL_CHECK = true;
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
        System.out.println("Uszanowanko! Będziemy rozwiązywać: " + Wypisz.doNapisu(pierwotnyCel.f(), true));
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
                    System.out.println("Wszystkie cele spełnione. Generuję rozwiązanie");
//                    System.out.println(Wypisz.doNapisu(pierwotnyCel.f()));
                    Ast x = gm.recreateAst(pierwotnyCel.getHole());
                    System.out.println("No to sprawdzam rozwiązanie:");
                    System.out.println((x));
                    String napisAst = Wypisz.doNapisu(x);
                    System.out.println(napisAst);

                    Formula interp = Interp.interp(x);
                    if (!interp.equalsF(pierwotnyCel.f())) {
                        throw new ZfcException("No jednak lipa, wyszło:\n" + Wypisz.doNapisu(interp, true));
                    } else {
                        System.out.println("OK!");
                    }

                    if (ADDITIONAL_CHECK) {
                        System.out.println("jeszcze posprawdzam kilka rzeczy");
                        Common.assertC(Interp.interp((napisAst)).equalsF(pierwotnyCel.f()));
                        System.out.println("git");
                    }

                    return x;
                }
            }
            System.out.print("Obecny cel: " + obecnyCel.getHole().getId());
            var jeszczeJakis = new Ref<>(false);
            ;
            niespelnione().filter(cc -> cc != obecnyCel).peek(q -> {
                        jeszczeJakis.set(true);
                        System.out.print("; pozostałe cele: ");
                    })
                    .forEach(x -> System.out.print(x.getHole().getId() + ", "));
            System.out.println();
            System.out.println("Kontekst:");
            obecnyCel.kontekst().forEach(q -> System.out.println(" " + q));
            System.out.println("Cel na teraz (" + obecnyCel.getHole().getId() + "):");
            System.out.println(/*obecnyCel.f() == null ? "evar " + obecnyCel.getHole().getId() :*/ Wypisz.doNapisu(obecnyCel.f(), true));
            System.out.println("dawaj:");
            System.out.print("$ ");
            var l = reader.readLine();
            if (l == null) {
                System.out.println("olewam");
                return null;
            }
            var tt = (TokenTree.Branch) Parser.ogar("(" + l + ")");

            var len = tt.trees().size();
            if (len == 0) {
                continue;
            }

            Function<Integer, TokenTree> getT = i -> (tt.trees().get(i));
            Function<Integer, String> get = i -> ((TokenTree.Leaf) getT.apply(i)).s();

            var go0 = get.apply(0);
            if (l.startsWith("intro ")) {
                var n = l.substring("intro ".length());
                obecnyCel.intro(n);
            } else if (l.startsWith("assumption ")) {
                var n = l.substring("assumption ".length());
                obecnyCel.wypelnijKontekstem(n);
            } else if (get.apply(0).equals("let") || get.apply(0).equals("niech") || get.apply(0).equals("chcem")) {
                var q = (getT.apply(2));
                obecnyCel.chain(get.apply(1), q);
            } else if (get.apply(0).equals("exact") || get.apply(0).equals("bezpośrednio")) {
                var strict = true;
                if (len > 2) {
                    String apply = get.apply(2);
                    strict = apply.equalsIgnoreCase("true") || apply.equalsIgnoreCase("nonstrict");
                }
                obecnyCel.exact(getT.apply(1), strict);

            } else if (go0.equals("cel")) {
                var i = Integer.parseInt(get.apply(1));
                obecnyCel = niespelnione().filter(q -> q.getHole().getId() == i).findFirst().orElseThrow();
            } else {
                System.out.println("co? " + l);
            }

        }
    }

    private Optional<Cel> nastepny() {
        return gm.getCele().values().stream().filter(c -> !c.spelniony()).findFirst();
    }

    private Stream<Cel> niespelnione() {
        return gm.getCele().values().stream().filter(c -> !c.spelniony());//.toList();
    }

}
