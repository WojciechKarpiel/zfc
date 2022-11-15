package ast;
sealed public interface Ast permits Ast.App, Ast.Hole, Ast.Lambda, Ast.Pi, Ast.Universe, Variable {

    record Hole() implements Ast {
    }
    record Universe(int i) implements Ast {
    }

    record Lambda(Abstraction abs) implements Ast {
    }

    record Pi(Abstraction abs) implements Ast {
    }
    record App(Ast fn, Ast arg) implements Ast{}
    /*
    sigmy itd w kontekście?
    posty i jednostkowy zapewne tak
     */

//    record Empty() implements Ast{}
    // EmptyElim w kontekście, a nie w AST?

//     record Unit() implements Ast{}
//    record Tt() implements Ast{
//    }

}
