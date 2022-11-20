package test;

import ast.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import util.ZfcException;

import java.util.List;
import java.util.function.Function;

import static ast.Formula.in;
import static org.junit.jupiter.api.Assertions.*;
import static ast.Formula.*;
class InterpTest {

    @Test
    void andrzejuTosieWysype(){
        var x = varRef("x");
        var y = varRef("y");
        var q = varRef("q");
var orig =         Interp.ALLOW_FREE_VARS;
var orig2 = Interp.DOPISUJ_SWIADKOW;

        Interp.DOPISUJ_SWIADKOW =false;
        Interp.ALLOW_FREE_VARS = true;
        Function<VarRef, Ast> elo = ww ->
                new Ast.ModusPonens(new Ast.FormulaX(
                        implies(x,y)), ww.variable(), q.variable(),q.variable() );



        var okk=Interp.interp(elo.apply(x));

        assertEquals(y  ,okk);


        assertThrows(ZfcException.class, () -> Interp.interp(elo.apply(y)));
        Interp.ALLOW_FREE_VARS = orig;
Interp.DOPISUJ_SWIADKOW = orig2;
    }
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
                                                        new Ast.IntroImpl( Formula.appliedConstant( Formula.constant("elo", List.of(), in(varRef( jakisZbior),varRef(p1))),List.of()),
                                                                implX, new Ast.ExFalsoQuodlibet(new Ast.Apply(p1P, jakisZbior), implX,
                                                                Formula.appliedConstant(
                                                                Formula.constant("hehe", List.of(), in( varRef(jakisZbior),varRef(p2) /*2!*/)), List.of()) , qq, qq))
                                                        ,
                                                        // tu będzie to samo
                                                        new Ast.IntroImpl(Formula.appliedConstant( Formula.constant("elo2", List.of(), in(varRef(jakisZbior),varRef( p2))),
                                                                List.of()),
                                                                implX, new Ast.ExFalsoQuodlibet(new Ast.Apply(p2P, jakisZbior), implX,
                                                                Formula.appliedConstant(
                                                                Formula.constant("hehe2", List.of(), in( varRef(jakisZbior),varRef( p1))),
                                                                        List.of()), qq, qq))
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
               return forall(varRef( n), not(in( varRef( n),varRef( q))));
            };

            var x = Variable.local("_1");
            var y = Variable.local("_2");
            coChcialem= forall(varRef(x),  implies(puste.apply(x),  forall(varRef( y),
                    implies(puste.apply(y), eql(varRef( x),varRef( y)))
                    )));
        }

        assertTrue(coChcialem.equalsF(wynik));


    }

    // TODO test co się wyala
}