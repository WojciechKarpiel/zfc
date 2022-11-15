package ast;

public sealed interface Variable extends Ast, Formula permits  Variable.Local {
//        record Bruijn(int i) implements Variable {}

//    record NamedVar(String name) implements Variable {
//    }
//
//    record GenSym(String name, int i) implements Variable {
//        @Override
//        public String toString() {
//            return /*"g_" +*/ name() /*+ "_" */ + i;
//        }
//    }

    final class Local implements Variable {
        @Override
        public boolean equals(Object obj) {
            return this == obj;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this);
        }

        private final String name;

        public Local(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return getName();
        }
    }
    static Local local(String name){
        return new Local(name);
    }
/*
    static GenSym createFresh(String name) {
        return GenSymManager.I.createFresh(name);
    }

    enum GenSymManager {
        I;
        private int i = 0;

        public GenSym createFresh(String name) {
            return new GenSym(name, i++);
        }
    }
*/
}

