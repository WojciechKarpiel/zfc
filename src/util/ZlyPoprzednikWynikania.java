package util;

import ast.Ast;
import ast.Formula;

public class ZlyPoprzednikWynikania extends ZfcException {

    private final Ast.ModusPonens modusPonens;
    private final Formula wynikanie;
    private final Formula zapodany;

    public ZlyPoprzednikWynikania(Ast.ModusPonens mp, Formula wynikanie , Formula zapodany){
        super("Zły poprzednik");
        this.modusPonens = mp;
        this.wynikanie = wynikanie;
        this.zapodany = zapodany;
    }

    public Formula getPoprzednik() {
        return ((Formula.Implies) getWynikanie()).poprzednik();
    }

    public Formula getZapodany() {
        return zapodany;
    }

    @Override
    public String getMessage() {
        return String.format("W wynikaniu %s trzeba było %s a jest %s", modusPonens.metadata(), getPoprzednik().metadata(), zapodany.metadata());
    }

    public Formula getWynikanie() {
        return wynikanie;
    }

    public Ast.ModusPonens getModusPonens() {
        return modusPonens;
    }
}
