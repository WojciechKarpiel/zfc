package test;

import org.junit.jupiter.api.Test;
import util.vlist.VList;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VListTest {

    @Test
    void cons() {
        VList<Integer> em1 = VList.<Integer>empty().cons(1);
        var em2 = em1.cons(2);
        var em2a = em1.cons(997);

        var em2p = em2.toOpt().orElseThrow().tail().cons(2);
        assertTrue(em2.__sameUnderlying(em1));
        assertTrue(em2.__sameUnderlying(em2p));
        assertFalse(em2a.__sameUnderlying(em1));


        assertEquals(List.of(1), em1.toList());
        assertEquals(List.of(1, 2), em2.toList());
        assertEquals(List.of(1, 2), em2p.toList());
        assertEquals(List.of(1, 997), em2a.toList());

    }

}