package parser;

import java.util.*;
import java.util.function.Consumer;




sealed interface Token permits Token.LPar, Token.RPar, Token.Symbol {
    record LPar(Position p) implements Token {
    }

    record RPar(Position p) implements Token {
    }

    record Symbol(String s, Position p) implements Token {
    }
}



public class Parser {

    public static TokenTree ogar(String s) {
        return ogar(new Iterator<>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return s.length() > i;
            }

            @Override
            public Character next() {
                return s.charAt(i++);
            }
        });
    }

    public static TokenTree ogar(Iterator<Character> it) {
        return parse(tokenize(new PeekingCharIt((it))));
    }

    public static List<Token> tokenize(PeekingCharIt it) {
        var ret = new ArrayList<Token>();
        while (true) {
            var p = it.peek();
            if (p.isEmpty()) break;
            char c = p.get();
            if (c == '(') {
                ret.add(new Token.LPar(new Position(it.getLine(), it.getColumn())));
                it.next();
            } else if (c == ')') {
                ret.add(new Token.RPar(new Position(it.getLine(), it.getColumn())));
                it.next();

            } else if (Character.isWhitespace(c)) {
                it.next();
            } else {
                var s = new StringBuilder();
                var Position1 = new Position(it.getLine(), it.getColumn());
                while (it.peek().map(d -> d != ')' && d != '(' && !Character.isWhitespace(d)).orElse(false)) {
                    s.append(it.next());
                }
                ret.add(new Token.Symbol(s.toString(), Position1));
            }
        }
        return ret;
    }

    private record PTr(ArrayList<TokenTree> l, Position start) {
        public static PTr neww(Position p) {
            return new PTr(new ArrayList<>(), p);
        }
    }

    public static TokenTree parse(List<Token> tokens) {
        Stack<PTr> s = new Stack<>();
        var wartownik = PTr.neww(new Position(0, 0));
        s.push(wartownik);
        for (Token token : tokens) {
            switch (token) {
                case Token.LPar lPar -> s.push(PTr.neww(lPar.p()));
                case Token.RPar rPar -> {
                    var pp = s.pop();
                    var t = new TokenTree.Branch(pp.l(), pp.start, rPar.p());
                    s.peek().l.add(t);
                }
                case Token.Symbol symbol -> {
                    var lf = new TokenTree.Leaf(symbol.s(), symbol.p());
                    s.peek().l.add(lf);
                }
            }
        }
        /// s powinnio być jeden tu
        if (s.size() > 1) {
            throw new IllegalArgumentException("Jest o " + (s.size() - 1) + " nie zamkniętych");
        }
        return wartownik.l().get(0);
    }


}


class PeekingCharIt implements Iterator<Character> {

    private int line;
    private int nextL;
    private int column;
    private int nextC;

    private final Iterator<Character> iterator;
    private Optional<Character> peeked = Optional.empty();

    public PeekingCharIt(Iterator<Character> it) {
        line = 0;
        column = 0;
        nextC = 0;
        nextL = 0;
        this.iterator = it;
    }


    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }


    public Optional<Character> peek() {
        return peeked.or(() -> {
            if (iterator.hasNext()) {
                Character next = iterator.next();
                line = nextL;
                column = nextC;
                if (next.equals('\n')) {
                    nextL++;
                    nextC = 0;
                } else {
                    nextC++;
                }
                peeked = Optional.of(next);
            }
            return peeked;
        });
    }

    public Optional<Character> nextSafe() {
        var ret = peek();
        peeked = Optional.empty();
        return ret;
    }

    @Override
    public boolean hasNext() {
        return peeked.isPresent() || iterator.hasNext();
    }

    @Override
    public Character next() {
        return nextSafe().get();
    }

    @Override
    public void forEachRemaining(Consumer<? super Character> action) {
        peeked.ifPresent(action);
        iterator.forEachRemaining(action);
    }

    @Override
    public void remove() {
        if (peek().isPresent()) {
            peeked = Optional.empty();
        } else {
            iterator.remove();
        }
    }
}
