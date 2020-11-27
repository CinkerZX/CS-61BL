import com.sun.org.apache.xpath.internal.functions.FuncFalse;
public class LinkedListDeque<T> implements Deque<T>{

    //Definite ItemListNode (sub)class
    public static class ItemListNode<T>{
        public T item;
        public ItemListNode next;
        public ItemListNode prev;

        public ItemListNode(T item, ItemListNode next, ItemListNode prev){
            this.item = item;
            this.next = next;
            this.prev = prev;
        }
    }

    //The first item (if it exists) Attributes
    private ItemListNode head;
    private ItemListNode tail;
    private int size;

    //Creates an empty ItemList Constructor
    public LinkedListDeque(){
        head = new ItemListNode(null, null, null);
        tail = new ItemListNode(null, null, null);
        head.next = tail;
        tail.prev = head;
        size = 0;
    }

    //Add node
    @Override
    public void addFirst(T item){
        ItemListNode after_head = head.next;
        head.next = new ItemListNode<T>(item, head.next, head);
        after_head.prev = head.next;
        size += 1;
    }

    @Override
    public void addLast(T item){
        ItemListNode before_tail = tail.prev;
        tail.prev = new ItemListNode<T>(item, tail, tail.prev);
        before_tail.next = tail.prev;
        size += 1;
    }


    @Override
    public int size(){
        return size;
    }

    // Remove node
    @Override
    public T removeFirst(){
        if(isEmpty()){
            return null;
        }
        else{
            T tem = (T) head.next.item;
            head.next.next.prev = head;
            head.next = head.next.next;
            this.size -= 1;
            return tem;
        }
    }

    @Override
    public T removeLast(){
        if(!isEmpty()){
            return null;
        }
        else{
            T tem = (T) tail.prev.item;
            tail.prev.prev.next = tail;
            tail.prev = tail.prev.prev;
            this.size -= 1;
            return tem;
        }
    }

    @Override
    public void printDeque(){
        if(!isEmpty()) {
            ItemListNode point = head;
            for (int x = 0; x < size; x++) {
                point = point.next;
                System.out.print(point.item);
                System.out.print(" ");
            }
            System.out.print('\n');
        }
    }

    @Override
    public T get(int index){
        ItemListNode point = head.next;
        if(isEmpty() || index > size()-1){
            return null;
        }
        else{
            for (int x = 0; x<=index; x++){
                if(x == index){
                    return (T) point.item;
                }
                else {
                    point = point.next;
                }
            }
            return null;
        }
    }

    public T getRecursiveHelper(int index, ItemListNode node){
        if(isEmpty() || index >= size){
            return null;
        }
        else if (index == 0){
            return (T) node.item;
        }
        else{
            return getRecursiveHelper(index-1, node.next);
        }
    }

    public T getRecursive(int index){
        return getRecursiveHelper(index, head);
    }

}