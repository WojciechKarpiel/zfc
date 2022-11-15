package parser;

import java.util.List;

public sealed interface TokenTree permits TokenTree.Leaf, TokenTree.Branch {
    record Leaf(String s, Position p) implements TokenTree {
    }

    record Branch(List<TokenTree> trees, Position p1, Position p2) implements TokenTree {
    }

    private static String spaces(int i) {
        var s = new StringBuilder(i);
        while (i > 0) {
            i--;
            s.append(' ');
        }
        return s.toString();
    }

    static void print(TokenTree tokenTree) {
        print(tokenTree,false);
    }
    static void print(TokenTree tokenTree, boolean printParens) {
        int delta;
        if (printParens){
            delta=0;
        }else{
            delta=-1;
        }
        print(tokenTree, delta, printParens);
    }

    private static void print(TokenTree tokenTree, int ident, boolean printParens) {
        switch (tokenTree) {

            case Leaf leaf -> {
                System.out.print(spaces(ident) + leaf.s + "\n");
            }
            case Branch branch -> {
                if (printParens)
                    System.out.print(spaces(ident) + "(\n");
                branch.trees().forEach(t -> print(
                        t, ident + 1, printParens
                ));
                if (printParens)
                    System.out.print(spaces(ident) + ")\n");
            }
        }

    }
}