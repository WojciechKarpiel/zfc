package pisarz;

import ast.Formula;
import ast.Metadata;
import ast.Variable;
import util.Common;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

public class Wypisz {
    private record Suffix(int i) {
    }

    private record SufVar(Variable v, Suffix i) {
    }

    //sw and okVars
    private final Writer writer;
    // a co jeśli zmienne bdą miały cyfry na końcu?
    private final Map<String, List<SufVar>> takenVariables;

    private void put(Variable v) {
        var l = takenVariables.getOrDefault(v.getName(), new ArrayList<>());
        takenVariables.put(v.getName(),l);
        if (l.stream().anyMatch(q->q.v.equals(v))){
            return;
        }
        var maxsuf = l.stream().map(q -> q.i.i).mapToInt(q -> q).max();
        if (maxsuf.isPresent()) {
            l.add(new SufVar(v, new Suffix(maxsuf.getAsInt() + 1)));
        } else {
            l.add(new SufVar(v, new Suffix(0)));
        }
    }

    private String get(Variable v) {
        var l = takenVariables.getOrDefault(v.getName(), List.of());
        var sv = l.stream().filter(p -> p.v.equals(v)).findAny().orElse(new SufVar(
                v, new Suffix(0)
        ));
    // a co jeśli zmienna kończy się liczbą?
        return sv.v.getName() + (sv.i.i == 0 ? "" : "_"+sv.i.i + "");
    }
    private void remove(Variable v){
        var l = takenVariables.getOrDefault(v.getName(), List.of());
        var sv = l.stream().filter(p -> !p.v.equals(v)).collect(Collectors.toList());
        takenVariables.put(v.getName(),sv);
    }


    public Wypisz(Writer writer) {
        this.writer = writer;
        this.takenVariables = new HashMap<>();
    }

    public static String doNapisu(Formula f) {
        var v = new StringWriter();
        new Wypisz(v).wypisz(f, 0);
        return v.toString();
    }

    public void wypisz(Formula formula) {
        wypisz(formula, 0);
    }

    private static final int IDENT_CONSTANT = 2;


    private void writeln(String s) {
        write(s + "\n");
    }

    private void write(String s) {
        try {
            writer.write(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void addIdent(int ident) {
        for (int i = 0; i < ident * IDENT_CONSTANT; i++) {
            write(" ");
        }
    }

    private void wypisz(Formula formula, int ident) {
        addIdent(ident);
        switch (formula) {
            case Formula.And and -> {
                writeln("and");
                wypisz(and.a(), ident + 1);
                wypisz(and.b(), ident + 1);
            }
            case Formula.AppliedConstant appliedConstant -> {
                writeln("appliedConstant");
                wypisz(appliedConstant.fi(), ident + 1);
                addIdent(ident + 1);
                writeln("args");
                appliedConstant.args().forEach(f -> wypisz(f, ident + 2));
            }
            case Formula.Constant constant -> {
                writeln("constant " + constant.name());
                addIdent(ident + 1);
                writeln("vars");
                constant.freeVariables().forEach(this::put);
                constant.freeVariables().forEach(v -> wypisz(Formula.varRef(v, Metadata.EMPTY), ident + 2));
                wypisz(constant.formula(), ident + 1);
                constant.freeVariables().forEach(this::remove);
            }
            case Formula.Equals equals -> {
                writeln("eql");
                wypisz(equals.a(), ident + 1);
                wypisz(equals.b(), ident + 1);
            }
            case Formula.Implies implies -> {
                writeln("implies");
                wypisz(implies.poprzednik(), ident + 1);
                wypisz(implies.nastepnik(), ident + 1);
            }
            case Formula.In in -> {
                writeln("in");
                wypisz(in.element(), ident + 1);
                wypisz(in.set(), ident + 1);
            }
            case Formula.Not not -> {
                writeln("not");
                wypisz(not.f(), ident + 1);
            }
            case Formula.Or or -> {
                writeln("or");
                wypisz(or.a(), ident + 1);
                wypisz(or.b(), ident + 1);
            }
            case Formula.VarRef varRef -> {
                writeln(get(varRef.variable()));
            }
            case Formula.Exists exists -> {
                writeln("exists");
                put(exists.var().variable());
                wypisz(exists.var(),ident+1);
                wypisz(exists.f(),ident+1);
                remove(exists.var().variable());
            }
            case Formula.ForAll forAll -> {
                writeln("forall");
                put(forAll.var().variable());
                wypisz(forAll.var(),ident+1);
                wypisz(forAll.f(),ident+1);
                remove(forAll.var().variable());

            }

        }
    }
}