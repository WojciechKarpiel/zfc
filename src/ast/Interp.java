package ast;

import util.Common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static util.Common.*;
public class Interp {
    public static boolean ALLOW_MISMATCHED_IMPLICATION_LOL = false;
    public static boolean DOPISUJ_SWIADKOW = true;
    public static boolean ALLOW_FREE_VARS = false;
    public void addAssumption(Variable v, Formula assuption){
        put(v,assuption);
    }

    private final  Map<Variable,Formula> swiadkowie = new HashMap<>();

    private void  put(Variable v, Formula f){
        var byl=swiadkowie.put(v,f);
        if (!( byl==null))
        {
            String x = "Nadpisuję " + v;
            System.out.println(x);

            throw new RuntimeException(x);
        }
    }

    public Map<Variable,Formula> weźŚwiadków(){ return  Collections.unmodifiableMap( swiadkowie);}
    public  Formula interp(Ast ast) {
        return switch (ast) {

            case Ast.Apply apply -> {
                var fn = interp(apply.fn());
                var arg = interp(apply.arg());
                if (fn instanceof Formula.ForAll forAll) {
                    var f = forAll.f();
                    var v = forAll.var();
                 yield    interp(new Ast.FormulaX(new Subst(v, arg).apply(f)));
                } else {
                    throw null;
                }
            }

            case Ast.ElimAnd elimAnd -> throw null;
            case Ast.ExtractWitness ew -> switch (interp(ew.sigma())) {
                case Formula.Exists sigma -> {
                    var v = sigma.var();
                    var f = sigma.f();
                put(v,f);
                    Ast apply = new Subst(Map.of(ew.witness(), v, ew.proof(), f)).apply(/*interp */(ew.body()));
                    Formula interp = interp(apply);
                    if (DOPISUJ_SWIADKOW){
                        interp = Formula.forall(v, Formula.implies(f, interp));
                    }
                    yield interp;
                }
                default -> throw null;
            };
            case Ast.FormulaX formul -> switch (formul.f()){
                case Formula.AppliedConstant ac ->
                {
                    var m = new HashMap<Variable,Formula>(ac.args().size());
                    for (int i = 0; i < ac.fi().arity(); i++) {
                        m.put(ac.fi().freeVariables().get(i), ac.args().get(i));
                    }
                    var s = new Subst(m);

                    yield s.apply(ac.fi().formula());
                }
                default -> formul.f();
            };
            case Variable.Local local -> {
                    if (ALLOW_FREE_VARS)
                        yield                    swiadkowie.getOrDefault(local,local);
                    else {
                            fail();
                        throw null;}
                    }
            case Ast.ModusPonens modusPonens -> {
                var v =modusPonens.witness();
                var pop = modusPonens.poprzednik();
                var wyn = interp(modusPonens.wynikanie());
                if (wyn instanceof Formula.Implies implies){
                    Formula int_pop = interp(pop);
                    if (!(implies.poprzednik().equalsF(int_pop))) {
                        if (ALLOW_MISMATCHED_IMPLICATION_LOL) {
                            System.out.println("UWAGA, dozwalam na przypał");
                            System.out.println(implies.poprzednik());
                            System.out.println(int_pop);
                        } else {
                            fail();
                            throw new RuntimeException("implies.poprzednik().equalsF(interp(pop));");
                        }
                    }
                    // todo wywalić świadków? z interp(pop)?
//                    put(v, implies.nastepnik());
                    Ast apply = new Subst(v, implies.nastepnik()).apply(modusPonens.body());
                    yield interp(apply);

                }else {
                    fail();
                    throw null;
                }
            }
            case Ast.Chain chain -> {

                var intp = interp(chain.e());

                yield  interp(new Subst(chain.v(), intp).apply(chain.rest()));
            }
            case Ast.ElimNot elimNot -> {
               var c= elimNot.cnstChciany();
                assertC(c.isAtom());

               var not = (Formula.Not) interp(elimNot.not());

               var aJednak= interp( elimNot.aJednak());
               assertC(not.f().equalsF(aJednak));

                var v=elimNot.v();
                var rest= elimNot.body();

    yield                 interp(new Subst(v, c.formula()).apply(rest));
            }
            case Ast.IntroForall introForall -> {
                var body = interp(introForall.body());
                assertC(!swiadkowie.containsKey(introForall.v()));
                Set<Variable> freeVars = body.FreeVarialbes();
                assertC(
                         freeVars.equals(Set.of(introForall.v())) ||
                         freeVars.isEmpty()
                 ) ;

                 yield Formula.forall(introForall.v(), body);

            }
            case Ast.IntroAnd introAnd -> {
                var a = interp(introAnd.a());
                var b = interp(introAnd.b());
                yield Formula.and(a,b);
            }
            case Ast.IntroImpl ii -> {
                 var popq = ((Formula.Constant) ii.pop());
                 Common.assertC(popq.isAtom());
                 var pop = popq.formula();
                 var bdzie = interp(new Subst(ii.v(),pop).apply(ii.nast()));
                 Common.assertC(bdzie.FreeVarialbes().isEmpty());
                 // nie jestem pewien czy tu nie przemycam czegoś niedobrego
            yield      Formula.implies(pop, bdzie);

            }
        };
    }


}
