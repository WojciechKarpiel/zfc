import ast.Formula;
import ast.Interp;
import front.Drzwi;
import parser.Aster;
import parser.Parser;
import parser.TokenTree;
import pisarz.Wypisz;

import java.io.*;

/**
 * Najpierw możliwość zdefiniowania <=>
 * <p>
 * Teraz zdefiniować operacje na zbiorach:
 * sumę, git
 * cz. wspólną git
 * różnicę - bedzie na tą samą modłę co cz wspólna, meh
 * No to teraz ulepszyć drzwi, żeby różnicę zdefiniować drzwiami
 */
public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            InputStream inS;
            if (args[0].equals("--")) {
                inS = (System.in);
            } else {
                var f = new File(args[0]);
                inS = new FileInputStream(f);
            }


            TokenTree ogar = Parser.ogar(inS);
            inS.close();
            var ast = Aster.doAst(ogar);
            var interpd = Interp.interp(ast);

            System.out.println(ast);
            System.out.println(Wypisz.doNapisu(interpd));
        } else {
            System.out.println("zapodaj cel");
            System.out.print("$ ");
            Formula cel;
            try {
                var r = new BufferedReader(new InputStreamReader(System.in));
                var l = r.readLine();
                cel = Aster.parseFormula(Parser.ogar(l));
                // nie zamykam stdin, bdzie potrzebne
            } catch (Exception e) {
                e.printStackTrace();
                var f = "(forall n (implies (forall x (in x n)) (forall x (in x n))))";
                System.out.println("Coś nie pykło, podmieniam cel na:\n" + f);
                cel = Aster.parseFormula(Parser.ogar(f));
            }
            System.out.println("No to zaczynamy!");
            var d = new Drzwi(cel);
            var ast = d.repl();
            System.out.println(ast);
        }
    }
}