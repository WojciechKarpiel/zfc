package ast;

import parser.Position;
import parser.Span;

import javax.swing.*;
import java.util.Objects;
import java.util.Optional;

public final class Metadata {

    private final Span span;
    private Metadata(){
        span=null;
    }
    public Metadata(Position p1, String s){
        this(new Span(p1,s));
    }
    public Metadata(Position p1, Position p2){
    this(new Span(p1,p2));
    }
    public Metadata(Span span){
        this.span=        Objects.requireNonNull(span);
    }

    public Span getSpan(){ return span;}
    private Optional<Span> getSpanOpt(){ return Optional.ofNullable(span);}


    public boolean eql(Metadata other){
       return this.getSpanOpt().equals(other.getSpanOpt());
    }

    // robie tak bo pyniam czy to nie zepsuje porównywania klas
    @Override
    public int hashCode() {
        return 0;
    }
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object obj) {
        return true;
    }

    public static final Metadata EMPTY = new Metadata();
}
