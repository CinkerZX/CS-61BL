import com.sun.org.apache.xpath.internal.functions.FuncFalse;
import java.util.Arrays;
public class ArrayDeque<T> implements Deque<T>{
    //Attributes
    private int size; // length of array
    private int head;
    private int end;
    private int item_set_size; //lenth of item
    public Object[] ArrayD;
    public int threshold;

    //Creates an empty ItemList Constructor
    public ArrayDeque(){
        //T[] ArrayD = (T[])new Object[8];
        ArrayD = new Object[8];
        size = 8;
        item_set_size = 0;
        head = 0;
        end = 1;
        threshold = 16;
    }

    //Add node
    @Override
    public void addFirst(T item){
        if (item_set_size == size - 2){
            Object[] ArrayD_2 = new Object[size+1];
            this.CutSize();
            System.arraycopy(ArrayD, 1, ArrayD_2, 2, item_set_size);
            ArrayD_2[1] = item;
            end = size;
            ArrayD = ArrayD_2;
            size = size+1;
        }
        else{
            ArrayD[head] = item;
            if (head == 0){
                head = size-1; //****************size-1 is the last position*******
            }
            else{
                head = head-1;
            }
        }
        item_set_size += 1;
    }

    @Override
    public void addLast(T item){
        if (item_set_size == size - 2){
            Object[] ArrayD_2 = new Object[size+1];
            this.CutSize();//[hXXXXXe]
            System.arraycopy(ArrayD, 0, ArrayD_2, 0, item_set_size+1);
            ArrayD_2[end] = item;
            end = end+1;
            ArrayD = ArrayD_2;
            size = size+1;
        }
        else{
            ArrayD[end] = item;
            if (end == size-1){
                end = 0;
            }
            else{
                end = end+1;
            }
        }
        item_set_size += 1;
    }

    @Override
    public int size(){
        return item_set_size;

    }

    // Remove node
    @Override
    public T removeFirst(){
        if (this.size - this.item_set_size > this.threshold){
            this.CutSize();
            return this.removeFirst();
        }
        else{
            if(head == size-1){
                head = 0;
            }
            else{
                head = head+1;
            }
            item_set_size -= 1;
            return (T)ArrayD[head];
        }
    }

    // reduce the size if remove too much
    private void CutSize(){
        Object[] ArrayD_2 = new Object[item_set_size+2];
        if (end < head){// XXXe___h
            System.arraycopy(ArrayD, head, ArrayD_2, 0, size-head);
            System.arraycopy(ArrayD, 0, ArrayD_2, size-head, end);
        }
        else{// [____hXXXXe__] [hXXXXXXXXXXe]
            System.arraycopy(ArrayD, head, ArrayD_2, 0, item_set_size+1);
        }
        head = 0;
        end = item_set_size+1;
        ArrayD = ArrayD_2;
        size = item_set_size+2;
    }


    @Override
    public T removeLast(){
        if (this.size - this.item_set_size > this.threshold){
            this.CutSize();
            return this.removeLast();
        }
        else{
            if(end == 0){
                end = size-1;
            }
            else{
                end -= 1;
            }
            item_set_size -= 1;
            return (T)ArrayD[end];
        }
    }

    @Override
    public void printDeque(){
        if(!isEmpty()) {
            this.CutSize();
            for (int x = head+1; x <= item_set_size; x++){
                System.out.print(ArrayD[x].toString());
                System.out.print(" ");
            }
            System.out.print('\n');
        }
    }

    @Override
    public T get(int index){ // index starts from 0
        if(isEmpty() || index <0 || index > item_set_size-1){
            return null;
        }
        else{
            if(head+index+1 > size-1){ // [___X_eh_] index = 4 head = 6 iloc = 3=4+6+1-8
                return (T)ArrayD[head+index+1-size];
            }
            else { // [___eh_X_] index = 1 head = 4 iloc = 6 = 4+1+1
                return (T)ArrayD[head+index+1];
            }
        }
    }
}