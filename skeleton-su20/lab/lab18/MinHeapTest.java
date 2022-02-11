import org.junit.Test;
import static org.junit.Assert.*;

public class MinHeapTest {

    @Test
    public void test1() {
        MinHeap<Character> mhpq = new MinHeap<>();
        mhpq.insert('f');
        assertEquals(1,mhpq.size());
        assertEquals("f", mhpq.findMin().toString());
        assertTrue(mhpq.contains('f'));
        mhpq.insert('h');
        mhpq.insert('d');
        mhpq.insert('b');
        mhpq.insert('c');
        assertEquals(5,mhpq.size());
        mhpq.removeMin();
        mhpq.removeMin();
        assertEquals("d", mhpq.findMin().toString());
        assertEquals(3,mhpq.size());
        assertFalse(mhpq.contains('c'));
    }
}
