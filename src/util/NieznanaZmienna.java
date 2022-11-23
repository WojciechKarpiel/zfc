package util;

import ast.Ast;

public class NieznanaZmienna  extends ZfcException{
    private final Ast.AstVar v;

    public NieznanaZmienna(Ast.AstVar v){
        super();
        this.v=v;
    }

    @Override
    public String getMessage() {
        return String.format("No i co z %s w %s? nie znam", v.variable(), v.metadata());
    }
}
