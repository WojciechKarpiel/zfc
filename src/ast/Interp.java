package ast;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Interp {

    public void addAssumption(Variable v, Formula assuption){
        put(v,assuption);
    }

    private final  Map<Variable,Formula> swiadkowie = new HashMap<>();

    private void  put(Variable v, Formula f){
        var byl=swiadkowie.put(v,f);
        if (!( byl==null)) throw new RuntimeException(" byl==null");
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
                   yield interp((new Subst(Map.of(ew.witness(), v, ew.proof(), f)).apply(/*interp */(ew.body()))));
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
            case Variable.Local local -> swiadkowie.getOrDefault(local,local);
            case Ast.ModusPonens modusPonens -> {
                var v =modusPonens.witness();
                var pop = modusPonens.poprzednik();
                var wyn = interp(modusPonens.wynikanie());
                if (wyn instanceof Formula.Implies implies){
                    if (!(implies.poprzednik().equalsF(interp(pop)))) throw new RuntimeException("implies.poprzednik().equalsF(interp(pop));");
                    // todo wywalić świadków? z interp(pop)?
                    put(v, implies.nastepnik());
                    yield interp(modusPonens.body());

                }else throw null;
            }
            case Ast.Chain chain -> {

                var intp = interp(chain.e());

                yield  interp(new Subst(chain.v(), intp).apply(chain.rest()));
            }
        };
    }


}
