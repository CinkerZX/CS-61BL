public interface Deque <T>{
    public void addFirst(T item);
    public void addLast(T item);
    public int size();
    public T removeFirst();
    public T removeLast();
    public void printDeque();
    public T get(int index);

    default public boolean isEmpty(){
        if (this.size() == 0){
            return true;
        }
        else{
            return false;
        }
    }
}
