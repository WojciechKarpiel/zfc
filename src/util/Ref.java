package util;

public class Ref<T> {
    private T t;

    public Ref(T t) {
        this.t = t;
    }

    public T get() {
        return t;
    }

    public void set(T t) {
        this.t = t;
    }
}
