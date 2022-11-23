package parser;

public record Position(int line, int column) {
    @Override
    public String toString() {
        return "("+line+","+column+")";
    }
}

