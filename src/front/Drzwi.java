package front;

import ast.Ast;
import ast.Formula;

import java.util.ArrayList;
import java.util.List;

public class Drzwi {


    private final List<Formula> cele;

    public Drzwi(Formula cel) {
        this.cele = new ArrayList<>();
        cele.add(cel);
    }

    public void rozwiaz(Ast ast) {
        // interp -> formuła = nie
        // interp -> formuła | AST_Z_DZIURĄ
        ast = Ast.apply(null, Ast.formulaX(null, null), null);

    }


    public List<Formula> cele() {
        return cele;
    }
}
