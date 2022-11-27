package util;

public record Para<A, B>(A a, B b) {
    public static <A, B> Para<A, B> n(A a, B b) {
        return new Para<>(a, b);
    }
}
