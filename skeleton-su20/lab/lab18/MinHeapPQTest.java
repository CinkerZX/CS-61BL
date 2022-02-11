import org.junit.Test;
import static org.junit.Assert.*;

public class MinHeapPQTest {

    @Test
    public void test1() {
        MinHeapPQ<Character> mhpq = new MinHeapPQ<>();
        mhpq.insert('f', 6.0);
        assertEquals(1,mhpq.size());
        assertEquals("f", mhpq.peek().toString());
        assertTrue(mhpq.contains('f'));
        mhpq.insert('h', 8.0);
        mhpq.insert('d', 4.0);
        mhpq.insert('b', 2.0);
        mhpq.insert('c', 3.0);
        assertEquals(5,mhpq.size());
        assertEquals("b", mhpq.poll().toString());
        assertEquals("c", mhpq.poll().toString());
        assertEquals("d", mhpq.peek().toString());
        assertEquals(3,mhpq.size());
        assertFalse(mhpq.contains('c'));
        assertEquals("d", mhpq.peek().toString());
        mhpq.changePriority('f', 1.0);
        assertEquals("f", mhpq.peek().toString());
    }
}
