package parser;

public record Span(Position start, Position end) {
    @Override
    public String toString() {
        return String.format("[%s-%s]", start,end);
    }

    public Span(Position start, String s){
        this(start, new Position(start.line(),  start.column()+s.length()));


    }
}