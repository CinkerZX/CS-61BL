import java.util.Arrays;

public class ArrayDeque<T> implements Deque<T>{

    // variables
    private int head;
    private int end;
    private int itemsetsize;
    private Object[] arrayDeque;
    private int threshhold;
    private int size;

    //constructor
    public ArrayDeque() {
        arrayDeque = new Object[8];
        this.head = 0;
        this.end = 1;
        this.itemsetsize = 0;
        this.threshhold = 16;
        this.size = 8;
    }

    @Override
    public void addFirst(T item) {
        if (itemsetsize == size - 2) {
            // the array is full
            Object[] newAD = new Object[size + 1];
            this.CutSize();
            System.arraycopy(arrayDeque, 0, newAD, 0, size);
            arrayDeque = newAD;
            size += 1;
            /*if ((head - end) == 1) {
                //  [_______eh__]  ---->  [_______e_h__]
                System.arraycopy(arrayDeque, 0, newAD, 0, end+1);
                System.arraycopy(arrayDeque, head, newAD, head+1, size-head);
                //newAD = Arrays.copyOfRange(arrayDeque, 0, end+1).addAll(newAD).addAll(Arrays.copyOfRange(arrayDeque, head, size));
                arrayDeque = newAD;
                head += 1;
                size += 1;
            } else if (end - head == size - 1) {
                //  [h_________e] ----> [h_________e_]
                System.arraycopy(arrayDeque, 0, newAD, 0, size);
                arrayDeque = newAD;
                size += 1;
            }*/
        }

        arrayDeque[head] = item;
        itemsetsize += 1;
        if (head == 0) {
            head = size - 1;
        } else {
            head -= 1;
        }
    }

    @Override
    public void addLast(T item) {
        if (itemsetsize == size - 2) {
            // the array is full [hxxxxxxxxe] ----> [hxxxxxxxxe.]
            Object[] newAD = new Object[size + 1];
            this.CutSize();
            System.arraycopy(arrayDeque, 0, newAD, 0, size);
            arrayDeque = newAD;
            size += 1;
            /*if (head - end == 1) {
                //  [____eh__]  ---->  [____e_h__]
                System.arraycopy(arrayDeque, 0, newAD, 0, end + 1);
                System.arraycopy(arrayDeque, head, newAD, head + 1, size - head);
                //newAD = Arrays.copyOfRange(arrayDeque, 0, end+1).addAll(newAD).addAll(Arrays.copyOfRange(arrayDeque, head, size));
                arrayDeque = newAD;
                head += 1;
                size += 1;
            } else if (end - head == size - 1) {
                //  [h_________e] ----> [h_________e_]
                System.arraycopy(arrayDeque, 0, newAD, 0, size);
                arrayDeque = newAD;
                size += 1;
            }*/
        }

        arrayDeque[end] = item;
        itemsetsize += 1;
        if (end == size - 1) {  //[...hxxxxxxxe]
            end = 0;
        } else {  //[hxxxxxxe....]
            end += 1;
        }
    }

    @Override
    public int size() {
        return this.itemsetsize;
    }

    @Override
    public void printDeque() {
        if (this.isEmpty()) {
            System.out.println("The ArrayDeque is empty\n");
        } else {
            this.CutSize();
            for (int i = 1; i <= itemsetsize; i++) {
                System.out.print(arrayDeque[i].toString());
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    private void CutSize() {
        // [___h_________e]
        Object[] newAD = new Object[itemsetsize + 2];
        if (head < end) {
            System.arraycopy(arrayDeque, head, newAD, 0, itemsetsize+1);
        } else {  // [___e_________h]
            System.arraycopy(arrayDeque, head, newAD, 0, size - head);
            System.arraycopy(arrayDeque, 0, newAD, size - head, end );
        }
        arrayDeque = newAD;
        head = 0;
        end = itemsetsize + 1;
        size = itemsetsize + 2;
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        if (size - itemsetsize >= threshhold) {
            this.CutSize();
            return this.removeFirst();
        } else {
            if (head == size - 1) {
                head = 0;
            } else {
                head += 1;
            }
            itemsetsize -= 1;
            return (T)arrayDeque[head];
        }
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        if (size - itemsetsize >= threshhold) {
            this.CutSize();
            return this.removeLast();
        } else {
            if (end == 0) {
                end = size - 1;
            } else {
                end -= 1;
            }
            itemsetsize -= 1;
            return (T)arrayDeque[end];
        }
    }

    // index starts from 0
    @Override
    public T get(int index) {
        if (isEmpty()) {
            System.out.println("The ArrayDeque is empty!");
            return null;
        }
        if (index < 0 || index >= itemsetsize) {
            System.out.println("Illegal Index Number!");
            return null;
        } else {
            if (head + 1 + index > size - 1) { // [_X___eh_] index = 2, head = 6 length = 8 , return 1
                return (T)arrayDeque[head + 1 + index - size];
            } else { // [__h_X_e_] index = 1, head = 2 length = 8 , return 4
                return (T)arrayDeque[head + index + 1];
            }
        }
    }
}
