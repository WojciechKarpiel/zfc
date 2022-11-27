package ast;

import util.UnimplementedException;

import java.util.function.UnaryOperator;

public class Descent {
    private final UnaryOperator<Ast> pre;
    private final UnaryOperator<Ast> post;

    public Descent(UnaryOperator<Ast> pre, UnaryOperator<Ast> post) {

        this.pre = pre;
        this.post = post;
    }

    public Descent(UnaryOperator<Ast> pre) {
        this(pre, UnaryOperator.identity());
    }
//    private Ast ad(Ast ast){
//       return  descent(fun.apply(ast));
//    }

    public Ast descent(Ast ast) {
        if (pre != null) ast = pre.apply(ast);
        ast = switch (ast) {
            case Ast.Apply apply -> {
                Ast fnD = descent(apply.fn());
                Ast argD = descent(apply.arg());
                if (fnD == apply.fn() && argD == apply.arg()) {
                    yield apply;
                } else yield Ast.apply(
                        fnD,
                        argD,
                        apply.metadata()
                );
            }
            case Ast.AstVar astVar -> astVar;
            case Ast.Chain chain -> Ast.chain(
                    chain.v(),
                    descent(chain.e()),
                    descent(chain.rest()),
                    chain.metadata()
            );
            case Ast.Chcem chcem -> Ast.chcem(descent(chcem.rzecz()), chcem.co(), chcem.metadata());
            case Ast.ElimAnd elimAnd -> throw new UnimplementedException();
            case Ast.ExFalsoQuodlibet exFalsoQuodlibet -> throw new UnimplementedException();
            case Ast.ExtractWitness extractWitness -> throw new UnimplementedException();
            case Ast.FormulaX formulaX -> formulaX;
            case Ast.Hole hole -> hole;
            case Ast.IntroAnd introAnd -> throw new UnimplementedException();
            case Ast.IntroForall introForall -> Ast.introForAll(
                    (Ast.AstVar) descent(introForall.v()),
                    descent(introForall.body()),
                    introForall.metadata()
            );
            case Ast.IntroImpl introImpl ->
                    Ast.introImpl(introImpl.pop(), introImpl.v(), descent(introImpl.nast()), introImpl.metadata());
            case Ast.ModusPonens modusPonens -> throw new UnimplementedException();
        };
        if (post != null) ast = post.apply(ast);
        return ast;

    }

}
