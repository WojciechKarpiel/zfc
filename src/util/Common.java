package util;

import ast.Formula;
import ast.Metadata;
import parser.Span;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Optional;

public final class Common {
    private Common() {
    }

    public static void fail() {
        assertC(false);
    }

    public static void assertC(Boolean cond) {
        if (!cond)
            throw new ZfcException();
    }

    public static String spanSubstring(String in, Metadata metadata) {
        return spanSubstring(in,metadata.getSpan());
    }
    public static String spanSubstring(String s, Span span) {
        return spanSubstring(new StringReader(s), span);
    }
    public static String spanSubstring(Reader r, Span span) {
        var line = 0;
        Common.assertC(span.start().line() == span.end().line()); // do poprawy potem
        try {
            BufferedReader bufferedReader = new BufferedReader(r);
            while (true) {
                var l = bufferedReader.readLine();
                if (line == span.start().line()) {
                    return l.substring(span.start().column(), span.end().column());
                } else {
                    line++;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public record Iff(Formula a, Formula b, Metadata m) {
        public Iff(Formula a, Formula b) {
            this(a, b, Metadata.EMPTY);
        }
    }

    public static Optional<Iff> detectIff(Formula f) {
        if (f instanceof Formula.And and) {
            if (and.a() instanceof Formula.Implies ia) {
                if (and.b() instanceof Formula.Implies ib) {
                    if (ia.poprzednik().equalsF(ib.nastepnik()) &&
                            ib.poprzednik().equalsF(ia.nastepnik())) {
                        return Optional.of(new Iff(ia.poprzednik(), ia.nastepnik()));
                    } else return Optional.empty();
                } else return Optional.empty();
            } else return Optional.empty();
        } else return Optional.empty();
    }
}
