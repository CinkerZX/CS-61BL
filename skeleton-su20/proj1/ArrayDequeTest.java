import org.junit.Test;
import static org.junit.Assert.*;

/** Performs some basic linked list tests. */
public class ArrayDequeTest {

    /** Adds a few things to the deque, checking isEmpty() and size() are correct,
     * finally printing the results. */
    @Test
    public void addIsEmptySizeTest() {
        System.out.println("Running add/isEmpty/Size test.");
        System.out.println("Make sure to uncomment the lines below (and delete this line).");

        //ArrayDeque<String> lld1 = new ArrayDeque<>();
        Deque<String> lld1 = new ArrayDeque<>();


        // Java will try to run the below code.
        // If there is a failure, it will jump to the finally block before erroring.
        // If all is successful, the finally block will also run afterwards.
        try {
            assertTrue(lld1.isEmpty());

            lld1.addFirst("front");
            assertEquals(1, lld1.size());
            assertFalse(lld1.isEmpty());

            lld1.addLast("middle");
            assertEquals(2, lld1.size());

            lld1.addLast("back");
            assertEquals(3, lld1.size());

        } finally {
            // The deque will be printed at the end of this test
            // or after the first point of failure.
            System.out.println("Printing out deque: ");
            lld1.printDeque();
        }
    }

    /** Adds an item, then removes an item, and ensures that deque is empty afterwards. */
    @Test
    public void addRemoveTest() {
        System.out.println("Running add/remove test.");
        System.out.println("Make sure to uncomment the lines below (and delete this line).");

        //ArrayDeque<Integer> lld1 = new ArrayDeque<>();
        Deque<Integer> lld1 = new ArrayDeque<>();
        try {
            assertTrue(lld1.isEmpty());

            lld1.addFirst(10);
            assertFalse(lld1.isEmpty());

            lld1.removeFirst();
            assertTrue(lld1.isEmpty());
        } finally {
            System.out.println("Printing out deque: ");
            lld1.printDeque();
        }
    }

    @Test
    public void addALot() {
        System.out.println("Running add/remove test.");
        System.out.println("Make sure to uncomment the lines below (and delete this line).");

        //Deque<Integer> lld1 = new ArrayDeque<>();
        Deque<Integer> lld1 = new LinkedListDeque<>();

        try {
            System.out.println(lld1.get(3));
            assertTrue(lld1.isEmpty());

            lld1.addLast(10);
            assertFalse(lld1.isEmpty());
            lld1.addLast(20);
            System.out.println(lld1.get(3));

            assertFalse(lld1.isEmpty());
            lld1.addLast(30);
            assertFalse(lld1.isEmpty());
            lld1.addLast(40);
            assertFalse(lld1.isEmpty());
            lld1.addFirst(50);
            System.out.println(lld1.get(3));
            assertFalse(lld1.isEmpty());
            lld1.addLast(60);
            assertFalse(lld1.isEmpty());
            lld1.addFirst(70);
            assertFalse(lld1.isEmpty());
            lld1.addFirst(80);
            System.out.println(lld1.get(0));
            System.out.println(lld1.get(1));
            System.out.println(lld1.get(2));
            System.out.println(lld1.get(3));
            System.out.println(lld1.get(4));
            assertFalse(lld1.isEmpty());
        } finally {
            System.out.println("Printing out deque: ");
            //System.out.println(lld1.ArrayD[7]);
            lld1.printDeque();
        }
    }
}
