

public class LinkedListDeque<T> implements Deque<T> {

    // A sebclass of the main class
    private static class LLDNode<T> {
        public T item;
        public LLDNode next;
        public LLDNode prev;

        public LLDNode(T item, LLDNode next, LLDNode prev){
            this.item = item;
            this.next = next;
            this.prev = prev;
        }

        @Override
        public String toString() {
            return item + "";
        }
    }

    public LLDNode<T> head;
    public LLDNode<T> tail;
    private int size;

    public LinkedListDeque(){
        head = new LLDNode(null,null,null);
        tail = new LLDNode(null,null,head);
        head.next = tail;
        size = 0;
    }

    @Override
    public void addFirst(T item){
        LLDNode oldpoint = head.next;
        head.next = new LLDNode<T>(item, head.next, head);
        oldpoint.prev = head.next;
        size += 1;
    }

    @Override
    public void addLast(T item){
        LLDNode oldpoint = tail.prev;
        tail.prev = new LLDNode<T>(item, tail, tail.prev);
        oldpoint.next = tail.prev;
        size += 1;
    }

    @Override
    public int size(){
        return this.size;
    }

    @Override
    public void printDeque(){
        if(this.isEmpty()){
            System.out.println("The LinkedListDeque is empty\n");
        }
        else{
            LLDNode point = head;
            for(int i = 0; i < size; i++){
                point = point.next;
                System.out.print(point.item.toString());
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    @Override
    public T removeFirst(){
        if(isEmpty()){
            return null;
        }
        else{
            T removeditem = (T) head.next.item;
            head.next = head.next.next;
            head.next.prev = head;
            this.size -= 1;
            return removeditem;
        }
    }

    @Override
    public T removeLast(){
        if(isEmpty()){
            return null;
        }
        else{
            T removeditem = (T) tail.prev.item;
            tail.prev = tail.prev.prev;
            tail.prev.next = tail;
            this.size -= 1;
            return removeditem;
        }
    }

    @Override
    public T get(int index){
        if(isEmpty()||index <0 || index >= this.size()){
            return null;
        }
        else{
            LLDNode gotitem = head.next;
            while(index>0){
                gotitem = gotitem.next;
                index -= 1;
            }
            return (T) gotitem.item;
        }
    }

    public T getRecursiveHelper(int index,LLDNode Node){
        if(isEmpty()||index <0||index >= size){
            return null;
        }
        if(index == 0){
            return (T) Node.item;
        }
        else{
            return getRecursiveHelper(index-1, Node.next);
        }
    }

    public T getRecursive(int index){
        return getRecursiveHelper(index, head.next);
    }
}