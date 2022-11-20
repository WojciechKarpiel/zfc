package test;

import ast.Formula;
import ast.Variable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FreeVarFinderTest {

    @Test
    void poprostuVar(){
        var hehe = Formula.varRef("hehe");
        var hehe2 = Formula.varRef("hehe");
       assertEquals(Set.of(hehe.variable()) ,  hehe.findFreeVariables());

       Assertions.assertTrue (Formula.forall(hehe, hehe).findFreeVariables().isEmpty());

        assertEquals(
                Set.of(hehe2.variable()),
                Formula.forall(hehe, Formula.and(hehe2,hehe)).findFreeVariables()) ;

        assertEquals(
                Set.of(hehe2.variable()),
                Formula.forall(hehe, Formula.and(hehe,hehe2)).findFreeVariables()) ;
    }

}