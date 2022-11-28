package pisarz;

import ast.Ast;
import ast.Formula;
import ast.Metadata;
import ast.Variable;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ast.Ast.*;

public class Wypisz {
    private final boolean krotko;

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
        takenVariables.put(v.getName(), l);
        if (l.stream().anyMatch(q -> q.v.equals(v))) {
            return;
        }
        var maxsuf = l.stream().map(q -> q.i.i).mapToInt(q -> q).max();
        if (maxsuf.isPresent()) {
            l.add(new SufVar(v, new Suffix(maxsuf.getAsInt() + 1)));
        } else {
            l.add(new SufVar(v, new Suffix(0)));
        }
    }

    private void withVar(Variable v, Runnable r) {
        put(v);
        r.run();
        remove(v);
    }

    private void withVars(List<Variable> vars, Runnable r) {
        if (vars.isEmpty()) r.run();
        else withVar(vars.get(0), () -> withVars(vars.subList(1, vars.size()), r));
    }

    private String get(Variable v) {
        var l = takenVariables.getOrDefault(v.getName(), List.of());
        var sv = l.stream().filter(p -> p.v.equals(v)).findAny().orElse(new SufVar(
                v, new Suffix(0)
        ));
        // a co jeśli zmienna kończy się liczbą?
        return sv.v.getName() + (sv.i.i == 0 ? "" : "_" + sv.i.i + "");
    }

    private void remove(Variable v) {
        var l = takenVariables.getOrDefault(v.getName(), List.of());
        var sv = l.stream().filter(p -> !p.v.equals(v)).collect(Collectors.toList());
        takenVariables.put(v.getName(), sv);
    }


    public Wypisz(Writer writer, boolean krotko) {
        this.writer = writer;
        this.krotko = krotko;
        this.takenVariables = new HashMap<>();
    }

    public static String doNapisu(Formula f) {
        return doNapisu(f, false);
    }

    public static String doNapisu(Ast ast) {
        var v = new StringWriter();
        new Wypisz(v, true).wypisz(ast);
        return v.toString();
    }

    public static String doNapisu(Formula f, boolean krotko) {
        var v = new StringWriter();
        new Wypisz(v, krotko).wypisz(f, 0);
        return v.toString();
    }

    public void wypisz(Formula formula) {
        wypisz(formula, 0);
    }

    private static final int IDENT_CONSTANT = 2;


    private void writeln(String s) {
        if (krotko) writes(s);
        else write(s + "\n");
    }

    private boolean pendingSpace = false;

    private void writes(String s) {
        write(s);
        pendingSpace = true;
    }

    private void write(String s) {
        try {
            if (pendingSpace && !s.equals(")") && !s.startsWith(" ")) writer.write(' ');
            writer.write(s);
            pendingSpace = false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void addIdent(int ident) {
        if (!krotko) {
            for (int i = 0; i < ident * IDENT_CONSTANT; i++) {
                write(" ");
            }
        }
    }

    private void wypisz(Formula formula, int ident) {
        var isVar = formula instanceof Formula.VarRef;
        if (krotko && !isVar) {
            write(" (");
        } else if (!krotko) addIdent(ident);
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
                if (!krotko) writeln("args");
                else write("(");
                appliedConstant.args().forEach(f -> wypisz(f, ident + 2));
                if (krotko) writes(")");
            }
            case Formula.Constant constant -> {
                writeln("constant " + constant.name());
                addIdent(ident + 1);
                if (!krotko) writeln("vars");
                else write("(");
                constant.freeVariables().forEach(this::put);
                constant.freeVariables().forEach(v -> wypisz(Formula.varRef(v, Metadata.EMPTY), ident + 2));
                if (krotko) writes(")");
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
                wypisz(exists.var(), ident + 1);
                wypisz(exists.f(), ident + 1);
                remove(exists.var().variable());
            }
            case Formula.ForAll forAll -> {
                writeln("forall");
                put(forAll.var().variable());
                wypisz(forAll.var(), ident + 1);
                wypisz(forAll.f(), ident + 1);
                remove(forAll.var().variable());

            }

        }
        if (krotko && !isVar) writes(")");
    }

    private void wypisz(Ast ast) {
        var noParen = ast instanceof AstVar || ast instanceof Hole || ast instanceof FormulaX;
        if (!noParen) write("(");
        switch (ast) {

            case Apply apply -> {
                writes("apply");
                wypisz(apply.fn());
                wypisz(apply.arg());
            }
            case AstVar astVar -> {
                write(" ");

                writes(get(astVar.variable()));
            }
            case Chain chain -> {
                writes("chain");
                put(chain.v());
                writes(get(chain.v()));
                wypisz(chain.e()); // to nie musi być pod <put>
                wypisz(chain.rest());
                remove(chain.v());

            }
            case Chcem chcem -> {
                writes("chcem ");
                wypisz(chcem.rzecz());
                wypisz(chcem.co());
            }
            case ElimAnd elimAnd -> {
                writes("andElim");
                wypisz(elimAnd.and());
                withVars(List.of(elimAnd.a(), elimAnd.b()), () -> {
                    writes(get(elimAnd.a()));
                    writes(get(elimAnd.b()));
                    wypisz(elimAnd.body());
                });
            }
            case ExFalsoQuodlibet exFalsoQuodlibet -> {
                writes("exFalsoSequiturQuodlibet");
                wypisz(exFalsoQuodlibet.not());
                wypisz(exFalsoQuodlibet.aJednak());
                wypisz(exFalsoQuodlibet.cnstChciany());
                withVar(exFalsoQuodlibet.v(), () -> {
                            writes(get(exFalsoQuodlibet.v()));
                            wypisz(exFalsoQuodlibet.body());
                        }
                );
            }
            case ExtractWitness extractWitness -> {
                writes("extractWitness");
                wypisz(extractWitness.sigma());
                withVars(List.of(extractWitness.witness(), extractWitness.proof()), () -> {
                    writes(get(extractWitness.witness()));
                    writes(get(extractWitness.proof()));
                    wypisz(extractWitness.body());
                });
            }
            case FormulaX formulaX -> {
                wypisz(formulaX.f());
            }
            case Hole hole -> {
                writes("_");
            }
            case IntroAnd introAnd -> {
                writes("and");
                wypisz(introAnd.a());
                wypisz(introAnd.b());
            }
            case IntroForall introForall -> {
                writes("forall");
                wypisz(introForall.v());
                wypisz(introForall.body());
            }
            case IntroImpl introImpl -> {
                writes("implies");
                wypisz(introImpl.pop());
                ;
                withVar(introImpl.v(), () -> {
                    writes(get(introImpl.v()));
                    wypisz(introImpl.nast());
                });
            }
            case ModusPonens modusPonens -> {
                writes("modusPonens");
                wypisz(modusPonens.wynikanie());
                wypisz(modusPonens.poprzednik());
                withVar(modusPonens.witness(), () -> {
                    writes(get(modusPonens.witness()));
                    wypisz(modusPonens.body());
                });
            }
        }

        if (!noParen) writes(")");
    }
}
