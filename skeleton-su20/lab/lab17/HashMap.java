
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class HashMap<K, V> implements Map61BL<K, V> {

    /* TODO: Instance variables here */
    public LinkedList<Entry<K, V>>[] sNM;
    private int size; // The number of items contained in the HashMap
    private int capacity;// Maximum Node numbers in HashMap
    private double loadfactor;

    /* Creates a new hash map with a default array of size 16 and a maximum load factor of 0.75. */
    public HashMap() {
        // TODO: YOUR CODE HERE
        size = 0;
        capacity = 16;
        loadfactor = 0.75;
        sNM = new LinkedList[capacity];

    }

    /* Creates a new hash map with an array of size INITIALCAPACITY and a maximum load factor of 0.75. */
    public HashMap(int initialCapacity) {
        size = 0;
        capacity = initialCapacity;
        loadfactor = 0.75;
        sNM = new LinkedList[capacity];
    }

    /* Creates a new hash map with INITIALCAPACITY and LOADFACTOR. */
    public HashMap(int initialCapacity, double loadFactor) {
        size = 0;
        capacity = initialCapacity;
        loadfactor = loadFactor;
        sNM = new LinkedList[capacity];
    }

    /* Returns the length of this HashMap's internal array. */
    public int capacity() {
        return capacity;
    }


    /* Returns the number of items contained in this map. */
    public int size() {
        // TODO: YOUR CODE HERE
        return size;
    }

    public void clear() {
        size = 0;
        sNM = new LinkedList[capacity];
    }


    /* Returns true if the map contains the KEY. */
    public boolean containsKey(K key) {
        // TODO: YOUR CODE HERE
        if(sNM[hash(key)] == null){
            return false;
        }
        for (Entry<K, V> e: sNM[hash(key)]) {
            if (key.equals(e.key)) {
                return true;
            }
        }
        return false;
    }

    /* Returns the value for the specified KEY. If KEY is not found, return
       null. */
    public V get(K key) {
        // TODO: YOUR CODE HERE
        for (Entry<K, V> e: sNM[hash(key)]) {
            if (key.equals(e.key)) {
                return e.value;
            }
        }
        return null;
    }

    /* Puts a (KEY, VALUE) pair into this map. If the KEY already exists in the
       SimpleNameMap, replace the current corresponding value with VALUE. */
    public void put(K key, V value) {
        // TODO: YOUR CODE HERE
        if(containsKey(key)) {
            remove(key);
        }
        size += 1;

        if(((double) size) / capacity > loadfactor) {
            resize();
        }
        if (sNM[hash(key)] == null) {
            sNM[hash(key)] = new LinkedList<Entry<K, V>>();
        }
        sNM[hash(key)].add(new Entry<K, V>(key, value));
    }

    public void resize() {
        this.capacity *= 2;
        this.size = 1;
        LinkedList<Entry<K, V>>[] oldSNM = sNM.clone();
        this.sNM = new LinkedList[this.capacity];
        for(LinkedList<Entry<K, V>> linkedE : oldSNM){
            if (linkedE == null){ continue; }
            for(Entry<K, V> e : linkedE){
                if( e == null ) { continue; }
                this.put(e.key, e.value);
            }
        }
    }

    public boolean remove(K key, V value){
        // TODO
        for (Entry<K, V> e: sNM[hash(key)]) {
            if (key.equals(e.key) && value.equals(e.value)) {
                sNM[hash(key)].remove(e);
                size -= 1;
                return true;
            }
        }
        return false;
    }
    /* Removes a single Entry<K, V>, KEY, from this table and return the VALUE if
       successful or NULL otherwise. */
    public V remove(K key) {
        // TODO: YOUR CODE HERE
        V value;
        for (Entry<K, V> e: sNM[hash(key)]) {
            if (key.equals(e.key)) {
                value = e.value;
                sNM[hash(key)].remove(e);
                size -= 1;
                return value;
            }
        }
        return null;
    }

    public Iterator<K> iterator() {
        return new HashMapIterator();
    }

    private int hash(K key) {
        return Math.floorMod(key.hashCode(), capacity);
    }

    private static class Entry<K, V> {

        private K key;
        private V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        /* Returns true if this key matches with the OTHER's key. */
        public boolean keyEquals(Entry<K, V> other) {
            return key.equals(other.key);
        }

        /* Returns true if both the KEY and the VALUE match. */
        @Override
        public boolean equals(Object other) {
            return (other instanceof Entry
                    && key.equals(((Entry<K, V>) other).key)
                    && value.equals(((Entry<K, V>) other).value));
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }

    private class HashMapIterator implements Iterator<K>{
        private int bookmark = -1; // size
        private ArrayList<K> keyList;

        public HashMapIterator() {
            keyList = new ArrayList<K>();
            for (int i=0; i<capacity; i++) {
                if (sNM[i] == null){ continue; }
                for(Entry<K, V> item : sNM[i]){
                    if( item == null ) { continue; }
                    keyList.add(item.key);
                }
            }
        }

        public K next() {
            bookmark += 1;
            return keyList.get(bookmark);
        }

        public boolean hasNext() {
            return (bookmark + 1) < size();
        }
    }

}