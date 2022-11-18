import ast.*;
import static ast.Formula.*;
import parser.Parser;
import parser.TokenTree;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        TokenTree t = Parser.ogar("(elo  ziom\n(haha))");
        System.out.println(t);
        TokenTree.print(t);

//        System.out.println(Variable.createFresh("x"));
//        System.out.println(Variable.createFresh("x"));


        Variable.Local zbior_pusty = Variable.local("zbior_pusty");
        Variable.Local jest_pusty = Variable.local("jest_pusty");
        Variable.Local elo = Variable.local("elo");
        var ast = new Ast.ExtractWitness(new Ast.FormulaX(ZFC.PUSTY), zbior_pusty, jest_pusty,
                new Ast.Apply(jest_pusty, elo));

        var i =new Interp();
        var interpd=i.interp(ast);
        System.out.println(ast);
        System.out.println(interpd);
        System.out.println(i.weźŚwiadków());

        var cok =Variable.local("cok");
        Function<Variable,Formula> pusty = (Variable x) -> forall(cok, not(in(cok,x)));
        var samWC = Variable.local("samWC");
Formula.Constant c = new Constant("jest_pusty", List.of(samWC), pusty.apply(samWC) );

        /* var chce =  forall x y, x:=extract(pusty), y:=extract(pusty), x=y*/
        /*

        plan: extensjonalności -> podzbiorów -> wywalić część /\
         */

        Variable dopodbiany1= Variable.local("do1");
        Variable dopodmiany2= Variable.local("do2");

        Variable wynik = Variable.local("wynik_eql");
        var bedzie =
    new Ast.ModusPonens(
                new Ast.Apply(new Ast.Apply(new Ast.FormulaX(ZFC.EXTENSIONALITY), dopodbiany1), dopodmiany2  ),
            null,
            wynik,wynik);
        i =new Interp();
        System.out.println(i.interp(bedzie));
        System.out.println(i.weźŚwiadków());
    }
}