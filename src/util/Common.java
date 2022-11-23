package util;

import ast.Metadata;
import parser.Span;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

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
}
