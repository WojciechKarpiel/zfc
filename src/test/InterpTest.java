package test;

import ast.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static ast.Formula.in;
import static org.junit.jupiter.api.Assertions.*;
import static ast.Formula.*;
class InterpTest {

    @Test
    void interp() {
        var p1 = Variable.local("p1");
        var p1P = Variable.local("p1P");
        var p2 = Variable.local("p2");
        var p2P = Variable.local("p2P");
        var impliesElo = Variable.local("impliesElo");
        var wyniczek = Variable.local("wynik");
        var jakisZbior = Variable.local("jakis");
        var implX = Variable.local("i");
        var qq = Variable.local("qq");
        Ast kazdyZbiorPustyJestSobieRowny = new Ast.ExtractWitness(new Ast.FormulaX(ZFC.PUSTY()), p1, p1P,
                new Ast.ExtractWitness(new Ast.FormulaX(ZFC.PUSTY()), p2, p2P,
                        /* tu chcę mieć (Eq p1 p2) */
                        // problem : skąd wynikanie, żeby zast
                        new Ast.Chain(impliesElo,
                                new Ast.Apply(new Ast.Apply(new Ast.FormulaX(ZFC.EXTENSIONALITY), p1), p2),
//                            impliesElo
                                new Ast.ModusPonens(impliesElo,
                                        /*poprzednikWynikaniaAksjomatyEks*/
                                        //ForAll[var=x77, f=And[a=Implies[poprzednik=In[element=x77, set=x72], nastepnik=In[element=x77, set=x94]], b=Implies[poprzednik=In[element=x77, set=x94], nastepnik=In[element=x77, set=x72]]]]
                                        new Ast.IntroForall(jakisZbior,
                                                new Ast.IntroAnd(
                                                        // Implies[poprzednik=In[element=x77, set=x72], nastepnik=In[element=x77, set=x94]]
                                                        new Ast.IntroImpl(new Formula.Constant("elo", List.of(), in(jakisZbior, p1)),
                                                                implX, new Ast.ElimNot(new Ast.Apply(p1P, jakisZbior), implX, new Formula.Constant("hehe", List.of(), in(jakisZbior, p2 /*2!*/)), qq, qq))
                                                        ,
                                                        // tu będzie to samo
                                                        new Ast.IntroImpl(new Formula.Constant("elo2", List.of(), in(jakisZbior, p2)),
                                                                implX, new Ast.ElimNot(new Ast.Apply(p2P, jakisZbior), implX, new Formula.Constant("hehe2", List.of(), in(jakisZbior, p1)), qq, qq))
                                                )
                                        )

                                        , wyniczek, wyniczek)
                        )
                ));

        Formula wynik = Interp.interp(kazdyZbiorPustyJestSobieRowny);



Formula coChcialem;
        {
            Function<Variable,Formula> puste = q -> {
                var n =Variable.local("___");
               return forall(n, not(in(n,q)));
            };

            var x = Variable.local("_1");
            var y = Variable.local("_2");
            coChcialem= forall(x,  implies(puste.apply(x),  forall(y,
                    implies(puste.apply(y), eql(x,y))
                    )));
        }

        assertTrue(coChcialem.equalsF(wynik));


    }

    // TODO test co się wyala
}