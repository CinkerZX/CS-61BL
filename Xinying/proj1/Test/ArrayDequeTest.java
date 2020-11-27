import org.junit.Test;

import javax.annotation.processing.SupportedSourceVersion;

import static org.junit.Assert.*;

public class ArrayDequeTest {

    @Test
    public void addIsEmptySizeTest() {
        System.out.println("Running add/isEmpty/Size test.");
        System.out.println("Make sure to uncomment the lines below (and delete this line).");

        ArrayDeque<String> lld1 = new ArrayDeque<>();

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

        ArrayDeque<Integer> lld1 = new ArrayDeque<>();

        try {
            assertTrue(lld1.isEmpty());

            lld1.addFirst(10);
            assertFalse(lld1.isEmpty());

            lld1.removeLast();
            assertTrue(lld1.isEmpty());

            lld1.addLast(10);
            assertFalse(lld1.isEmpty());

            lld1.removeFirst();
            assertTrue(lld1.isEmpty());

        } finally {
            System.out.println("Printing out deque: ");
            lld1.printDeque();
        }
    }

    @Test
    public void AddALotTest() {
        System.out.println("Running Increse size/Cut size test.");

        //ArrayDequezx<Integer> lld1 = new ArrayDequezx<>();
        ArrayDeque<Integer> lld1 = new ArrayDeque<>();
        try {
            lld1.addFirst(10);
            assertEquals(1, lld1.size());
            assertFalse(lld1.isEmpty());

            lld1.addLast(20);
            assertEquals(2, lld1.size());
            lld1.addLast(30);
            assertEquals(3, lld1.size());
            lld1.addLast(40);
            assertEquals(4, lld1.size());
            lld1.addLast(50);
            assertEquals(5, lld1.size());

            lld1.addLast(60);
            assertEquals(6, lld1.size());



            lld1.addLast(70);
            assertEquals(7, lld1.size());

            /*lld1.addFirst(00);
            assertEquals(8, lld1.size());*/

            lld1.addLast(80);


            /*lld1.printDeque();
            System.out.println(lld1.removeLast());
            lld1.printDeque();*/

            /*lld1.addFirst(80);
            lld1.printDeque();
            System.out.println(lld1.removeFirst());
            lld1.printDeque();*/


        } finally {
            System.out.println("Printing out deque: ");
            lld1.printDeque();
        }
    }

    @Test
    public void GetTest() {
        System.out.println("Running Increse size/Cut size test.");

        //Deque<Integer> lld1 = new ArrayDeque<>();
        Deque<Integer> lld1 = new LinkedListDeque<>();

        try {
            System.out.println(lld1.get(0));

            lld1.addFirst(10);
            assertEquals(1, lld1.size());
            assertFalse(lld1.isEmpty());

            lld1.addLast(20);
            assertEquals(2, lld1.size());
            lld1.addLast(30);
            assertEquals(3, lld1.size());
            lld1.addLast(40);
            assertEquals(4, lld1.size());

            System.out.println(lld1.get(0));

            System.out.println(lld1.get(5));
        } finally {
            System.out.println("Printing out deque: ");
            lld1.printDeque();
        }
    }
}
