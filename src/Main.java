import ast.Formula;
import ast.Variable;
import ast.ZFC;
import parser.Parser;
import parser.TokenTree;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        TokenTree t = Parser.ogar("(elo  ziom\n(haha))");
        System.out.println(t);
        TokenTree.print(t);

//        System.out.println(Variable.createFresh("x"));
//        System.out.println(Variable.createFresh("x"));
        System.out.println(ZFC.PODZBIOROW(new Formula.Costant("elo",2)));
        System.out.println(ZFC.PODZBIOROW(new Formula.Costant("qq",5)));
    }
}