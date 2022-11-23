package util;

import ast.Ast;
import ast.Formula;

public class NieToCoChciales extends ZfcException{

    private final Ast.Chcem chcem;
    private final Formula wyszlo;

    public NieToCoChciales(Ast.Chcem chcem, Formula wyszlo){
        this.chcem = chcem;

        this.wyszlo = wyszlo;
    }

    public Ast.Chcem getChcem() {
        return chcem;
    }

    public Formula getWyszlo() {
        return wyszlo;
    }

    @Override
    public String getMessage() {
        return String.format("Chcem sie sypło na %s, bo wyszło: %s", chcem.metadata(), wyszlo);
    }
}
