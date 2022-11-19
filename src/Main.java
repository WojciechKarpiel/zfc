import ast.*;
import static ast.Formula.*;

import util.Common;

import static ast.Ast.*;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        /* var chce =  forall x y, x:=extract(pusty), y:=extract(pusty), x=y*/
        /*

        plan: extensjonalności -> podzbiorów -> wywalić część /\
         */


        Ast kazdyZbiorPustyJestSobieRowny ;

        var i =new Interp();



    var p1 = Variable.local("p1");
    var p1P = Variable.local("p1P");
    var p2 = Variable.local("p2");
    var p2P = Variable.local("p2P");
    var xinP1 = Variable.local("p2P");
    var impliesElo = Variable.local("impliesElo");
    var wyniczek = Variable.local("wynik");
    var poprzednikWynikaniaAksjomatyEks = Variable.local("poprzednikWynikaniaAksjomatyEks");
    // poprzednik to musi być
var jakisZbior = Variable.local("jakis")
;
var implX = Variable.local("i");
var qq = Variable.local("qq");
kazdyZbiorPustyJestSobieRowny= new ExtractWitness(new FormulaX(ZFC.PUSTY()) ,p1, p1P ,
            new ExtractWitness(new FormulaX(ZFC.PUSTY()),p2,p2P,
                    /* tu chcę mieć (Eq p1 p2) */
                    // problem : skąd wynikanie, żeby zast
                    new Ast.Chain(impliesElo,
                    new Ast.Apply(new Ast.Apply(new FormulaX(ZFC.EXTENSIONALITY),p1 ), p2)    ,
//                            impliesElo
                    new Ast.ModusPonens( impliesElo,
                            /*poprzednikWynikaniaAksjomatyEks*/
                            //ForAll[var=x77, f=And[a=Implies[poprzednik=In[element=x77, set=x72], nastepnik=In[element=x77, set=x94]], b=Implies[poprzednik=In[element=x77, set=x94], nastepnik=In[element=x77, set=x72]]]]
                            new Ast.IntroForall(jakisZbior,
                                    new Ast.IntroAnd(
                                            // Implies[poprzednik=In[element=x77, set=x72], nastepnik=In[element=x77, set=x94]]
                                            new Ast.IntroImpl( new Formula.Constant("elo",List.of(),in(jakisZbior,p1) ),
                                                    implX, new ElimNot(new Apply(p1P, jakisZbior ),implX,new Formula.Constant("hehe", List.of(),in(jakisZbior,p2 /*2!*/)) ,qq,qq  ))
                                            ,
                                            // tu będzie to samo
                                            new Ast.IntroImpl( new Formula.Constant("elo2",List.of(),in(jakisZbior,p2) ),
                                                    implX, new ElimNot(new Apply(p2P, jakisZbior ),implX,new Formula.Constant("hehe2", List.of(),in(jakisZbior,p1 )) ,qq,qq  ))
                                    )
                                    )

                            ,wyniczek,wyniczek)
                    )
                            )            );

        Interp.DOPISUJ_SWIADKOW=true; // czytelniej
        Formula interp = i.interp(kazdyZbiorPustyJestSobieRowny);
        Common.assertC(interp.findFreeVariables().isEmpty());
        System.out.println(interp);
        if (!Interp.DOPISUJ_SWIADKOW)
                System.out.println(i.weźŚwiadków());

    }
}

/*
Plan tak na poważnie


Mam zbiór pusty:
E p1, V x ~(x w p1)
E p2, V x ~(x w p2)

chcę mieć
V x, x w p1 -> x w p2

może jak w teoriach typów emptyElim?

NOT(F) -> F -> _

 */