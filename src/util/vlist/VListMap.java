package util.vlist;

import util.Para;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public final class VListMap<A, B> implements Map<A, B> {

    private final VList<Para<A, B>> vList;

    public VListMap(VList<Para<A, B>> vList) {
        this.vList = vList;
    }

    @Override
    public int size() {
        return vList.size();
    }

    @Override
    public boolean isEmpty() {
        return vList.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return lookup(x -> x.a().equals(key)).isPresent();
    }

    @Override
    public boolean containsValue(Object value) {
        return lookup(x -> x.b().equals(value)).isPresent();
    }

    @Override
    public B get(Object key) {
        return lookup(x -> x.a().equals(key)).map(Para::b).orElse(null);
    }

    private Optional<Para<A, B>> lookup(Predicate<Para<A, B>> pred) {
        for (Para<A, B> p : vList) {
            if (pred.test(p)) return Optional.of(p);
        }
        return Optional.empty();
    }


    ////////////////

    @Override
    public B put(A key, B value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public B remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends A, ? extends B> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<A> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<B> values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Entry<A, B>> entrySet() {
        throw new UnsupportedOperationException();
    }
}
