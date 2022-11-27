package util.vlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class VList<T> {

    public static sealed interface VListMatch permits VList.Empty, VList.HeadTail {
    }

    public static record HeadTail<T>(T head, VList<T> tail) implements VListMatch {
    }

    public static enum Empty implements VListMatch {I}


    private ArrayList<T> under;
    private int maxIdx;


    private VList(ArrayList<T> under, int maxIdx) {
        this.under = under;
        this.maxIdx = maxIdx;
    }


    private void ensure() {
        if (under == null) {
            under = new ArrayList<>();
        }
    }

    public VList<T> cons(T elem) {
        ensure();
        if (under.size() == maxIdx + 1) {
            under.add(elem);
            return new VList<>(under, maxIdx + 1);
        } else if (under.get(maxIdx + 1).equals(elem)) {
            return new VList<>(under, maxIdx + 1);
        } else {
            var newarray = new ArrayList<T>(maxIdx + 2); // może wincyj?
            for (int i = 0; i <= maxIdx; i++) {
                newarray.add(under.get(i));
            }
            newarray.add(elem);
            return new VList<>(newarray, maxIdx + 1);
        }
    }

    public Optional<HeadTail<T>> toOpt() {
        if (isEmpty()) return Optional.empty();
        else return Optional.of(new HeadTail<T>(under.get(maxIdx), new VList<>(under, maxIdx - 1)));
    }

    public VListMatch match() {
        var o = toOpt();
        if (o.isPresent()) {
            return o.get();
        } else {
            return Empty.I;
        }
    }

    public boolean isEmpty() {
        return maxIdx < 0;
    }


    public List<T> toList() {
        if (under == null) return List.of();
        else return under.subList(0, maxIdx + 1);
    }

//    public Stream<T> stream(){
//
//    }

    private final static VList<?> EMPTY = new VList<>(null, -1);

    public static <T> VList<T> empty() {
        return (VList<T>) EMPTY;
    }


    @Override
    public int hashCode() {
        return 0; // TODO
    }

    @Override
    public boolean equals(Object obj) {
        return false; //TODO
    }


    public boolean __sameUnderlying(VList<T> other) {
        return this.under == other.under;
    }
}
