package ast;

import parser.Span;

import java.util.Objects;

public class Metadata {

    private final Span span;
    private Metadata(){
        span=null;
    }
    public Metadata(Span span){
        this.span=        Objects.requireNonNull(span);
    }

    public Span getSpan(){ return span;}

    // robie tak bo pyniam czy to nie zepsuje porównywania klas
    @Override
    public int hashCode() {
        return 0;
    }
    @Override
    public boolean equals(Object obj) {
        return true;
    }

    public static final Metadata EMPTY = new Metadata();
}
