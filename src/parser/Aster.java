package parser;

import ast.AnnotatedTree;
import ast.Ast;
import ast.Formula;
import ast.ZFC;

import java.util.Map;

import static ast.Ast.*;

public enum Aster {
    I;

    static final Map<String, Formula> AXIOMS = Map.of(
            "ekstensionalności" , ZFC.EXTENSIONALITY,
            "pary", ZFC.PARY
    );


    public Ast doAstUnsafe(String tree) {
     return doAstUnsafe(Parser.ogar(tree) );
    }
    public Ast doAstUnsafe(TokenTree tree) {
        return switch (tree) {
            case TokenTree.Leaf leaf -> {

                var r = AXIOMS.get(leaf.s());
//                if (r == null)
//           yield    new NamedVarUnsafe(leaf.s());
//                else
                    yield new FormulaX(r);
            }
            case TokenTree.Branch branch -> {

    yield null;
            }
            };
    }
}
