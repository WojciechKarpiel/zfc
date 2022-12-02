package util;

import ast.Formula;
import pisarz.Wypisz;

public class NieRowne extends ZfcException {
    private final Formula a;
    private final Formula b;

    public NieRowne(Formula a, Formula b) {

        this.a = a;
        this.b = b;
    }

    @Override
    public String getMessage() {
        return String.format("""
                Nie równe:
                %s
                %s""", Wypisz.doNapisu(a, true), Wypisz.doNapisu(b, true));
    }

    public Formula getA() {
        return a;
    }

    public Formula getB() {
        return b;
    }
}
