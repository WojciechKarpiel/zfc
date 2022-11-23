package util;

import ast.Ast;
import ast.Formula;

public class ZleExFalso extends ZfcException{


    private final Ast.ExFalsoQuodlibet exFalsoQuodlibet;
    private final Formula.Not not;
    private final Formula aJednak;

    public ZleExFalso(Ast.ExFalsoQuodlibet exFalsoQuodlibet, Formula.Not not, Formula aJednak) {

        this.exFalsoQuodlibet = exFalsoQuodlibet;
        this.not = not;
        this.aJednak = aJednak;
    }

    public Ast.ExFalsoQuodlibet getExFalsoQuodlibet() {
        return exFalsoQuodlibet;
    }

    public Formula.Not getNot() {
        return not;
    }

    public Formula getaJednak() {
        return aJednak;
    }

    @Override
    public String getMessage() {
        return String.format("Chciałeś Exfalso %s, ale zaprzeczenie %s nie jest takie jak ajednak %s",
                getExFalsoQuodlibet().metadata(), getNot().metadata(), getaJednak().metadata());
    }
}
