package parser;

public record Span(Position start, Position end) {
    public Span(Position start, String s){
        this(start, new Position(start.line(),  start.column()+s.length()));
    }
}