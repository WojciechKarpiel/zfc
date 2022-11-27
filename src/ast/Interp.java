package ast;

import util.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static util.Common.assertC;
import static util.Common.fail;

public class Interp {
    public static boolean ALLOW_MISMATCHED_IMPLICATION_LOL = false;
    public static boolean DOPISUJ_SWIADKOW = true;
    public static boolean ALLOW_FREE_VARS = false;

    public void addAssumption(Formula.VarRef v, Formula assuption) {
        put(v, assuption);
    }

    private final Map<Formula.VarRef, Formula> swiadkowie = new HashMap<>();

    private void put(Formula.VarRef v, Formula f) {
        var byl = swiadkowie.put(v, f);
        if (!(byl == null)) {
            String x = "Nadpisuję " + v;
            System.out.println(x);

            throw new RuntimeException(x);
        }
    }

    public Map<Formula.VarRef, Formula> weźŚwiadków() {
        return Collections.unmodifiableMap(swiadkowie);
    }

    public static Formula interp(Ast ast) {
        var i = new Interp();
        var formula = i.interpInternal(ast);
        // todo co jak nie ma światków?
        if (DOPISUJ_SWIADKOW) {

            Common.assertC(formula.findFreeVariables().isEmpty());
        } else {
            System.out.println("nie sprawdzam wolnych zmiennych");
        }
        return formula;
    }

    private Formula interpInternal(Ast ast) {
        return switch (ast) {
            case Ast.Chcem chcem -> {
                assertC(chcem.co().fi().isAtom());
                var chciane = chcem.co().fi().formula();
                var prawdziwe = interpInternal(chcem.rzecz());

                if (!chciane.equalsF(prawdziwe)) {
                    throw new NieToCoChciales(chcem, prawdziwe);
                }
                yield chciane;
            }
            case Ast.Apply apply -> {
                var fn = interpInternal(apply.fn());
                var arg = interpInternal(apply.arg());
                if (fn instanceof Formula.ForAll forAll) {
                    var f = forAll.f();
                    var v = forAll.var();
                    yield interpInternal(Ast.formulaX(new Subst(v, arg).apply(f), f.metadata()));
                } else {
                    throw new ZfcException();
                }
            }

            case Ast.ElimAnd elimAnd -> throw new ZfcException();
            case Ast.ExtractWitness ew -> switch (interpInternal(ew.sigma())) {
                case Formula.Exists sigma -> {
                    var v = sigma.var();
                    var f = sigma.f();
                    put(v, f);
                    Ast apply = new Subst(Map.of(ew.witness(), v, ew.proof(), f)).apply(/*interp */(ew.body()));
                    Formula interp = interpInternal(apply);
                    if (DOPISUJ_SWIADKOW) {
                        interp = Formula.forall(v, Formula.implies(f, interp));
                    }
                    yield interp;
                }
                default -> throw new ZfcException("miala być sigma");
            };
            case Ast.FormulaX formul -> switch (formul.f()) {
                case Formula.AppliedConstant ac -> {
                    var m = new HashMap<Variable, Formula>(ac.args().size());
                    for (int i = 0; i < ac.fi().arity(); i++) {
                        m.put(ac.fi().freeVariables().get(i), ac.args().get(i));
                    }
                    var s = new Subst(m);

                    yield s.apply(ac.fi().formula());
                }
                default -> formul.f();
            };
            case Ast.AstVar astVar -> {
                var vv = Formula.varRef(astVar.variable(), astVar.metadata());

                if (ALLOW_FREE_VARS || swiadkowie.containsKey(vv)) {
                    yield swiadkowie.getOrDefault(vv, vv);
                } else {

                    // a co jeśli jest pod Forall?
                    throw new NieznanaZmienna(astVar);

                }
            }
            case Ast.ModusPonens modusPonens -> {
                var v = modusPonens.witness();
                var pop = modusPonens.poprzednik();
                var wyn = interpInternal(modusPonens.wynikanie());
                if (wyn instanceof Formula.Implies implies) {
                    Formula int_pop = interpInternal(pop);
                    if (!(implies.poprzednik().equalsF(int_pop))) {
                        if (ALLOW_MISMATCHED_IMPLICATION_LOL) {
                            System.out.println("UWAGA, dozwalam na przypał");
                            System.out.println(implies.poprzednik());
                            System.out.println(int_pop);
                        } else {
                            throw new ZlyPoprzednikWynikania(modusPonens, implies, int_pop);
                        }
                    }
                    // todo wywalić świadków? z interp(pop)?
//                    put(variable, implies.nastepnik());
                    Ast apply = new Subst(v, implies.nastepnik()).apply(modusPonens.body());
                    yield interpInternal(apply);

                } else {
                    fail();
                    throw new ZfcException();
                }
            }
            case Ast.Chain chain -> {

                var intp = interpInternal(chain.e());

                yield interpInternal(new Subst(chain.v(), intp).apply(chain.rest()));
            }
            case Ast.ExFalsoQuodlibet exFalsoQuodlibet -> {
                var c = exFalsoQuodlibet.cnstChciany().fi();
                assertC(c.isAtom());

                var not = (Formula.Not) interpInternal(exFalsoQuodlibet.not());

                var aJednak = interpInternal(exFalsoQuodlibet.aJednak());
                if (!not.f().equalsF(aJednak)) {
                    throw new ZleExFalso(exFalsoQuodlibet, not, aJednak);
                }

                var v = exFalsoQuodlibet.v();
                var rest = exFalsoQuodlibet.body();

                yield interpInternal(new Subst(v, c.formula()).apply(rest));
            }
            case Ast.IntroForall introForall -> {
                //todo unhack
                Formula.VarRef key = Formula.varRef(introForall.v().variable(), introForall.v().metadata());
                assertC(!swiadkowie.containsKey(key));
                swiadkowie.put(key, key);
                var body = interpInternal(introForall.body());
                swiadkowie.remove(key);
//                Set<Variable> freeVars = body.findFreeVariables();
//                assertC(
//                         freeVars.equals(Set.of(introForall.variable())) ||
//                         freeVars.isEmpty()
//                 ) ;

                yield Formula.forall(key, body);

            }
            case Ast.IntroAnd introAnd -> {
                var a = interpInternal(introAnd.a());
                var b = interpInternal(introAnd.b());
                yield Formula.and(a, b);
            }
            case Ast.IntroImpl ii -> {
                var popqc = ((Formula.AppliedConstant) ii.pop());
                var popq = popqc.fi();
                assertC(popq.isAtom());
                var pop = popq.formula();
                var bdzie = interpInternal(new Subst(ii.v(), pop).apply(ii.nast()));
//                Set<Variable> freeVariables = bdzie.findFreeVariables();
//                Common.assertC(freeVariables.isEmpty());
                // nie jestem pewien czy tu nie przemycam czegoś niedobrego
                yield Formula.implies(pop, bdzie, ii.metadata());

            }
            case Ast.Hole hole -> throw new ZfcException();
        };
    }


}
