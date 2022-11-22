import ast.*;

import static ast.Formula.*;

import parser.Aster;
import parser.Parser;
import parser.TokenTree;
import util.Common;

import static ast.Ast.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
InputStream inS;
        if (args.length>0){
            var f =new File( args[0]);
             inS=new FileInputStream(f);
        }else {

             inS = (System.in);
        }
        TokenTree ogar = Parser.ogar(inS);
        inS.close();
        var ast = Aster.doAst(ogar);
        var interpd = Interp.interp(ast);

        System.out.println(ast);
        System.out.println(interpd);
    }

}