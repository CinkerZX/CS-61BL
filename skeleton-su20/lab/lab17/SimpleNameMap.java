import java.util.LinkedList;

public class SimpleNameMap {
    /* TODO: Instance variables here */
    public LinkedList<Entry>[] sNM;
    private int size;
    private int len;
    public static double threshold = 0.75;

    public SimpleNameMap() {
        // TODO: YOUR CODE HERE
        len = 10;
        size = 0;
        sNM = new LinkedList[10];

    }

    public SimpleNameMap(int len, int size) {
        // TODO: YOUR CODE HERE
        sNM = new LinkedList[len];
        this.size = size;
        this.len = len;
    }

    /* Returns the number of items contained in this map. */
    public int size() {
        // TODO: YOUR CODE HERE
        return size;
    }

    /* Returns the number of items contained in this map. */
    public int length() {
        // TODO: YOUR CODE HERE
        return len;
    }

    /* Returns true if the map contains the KEY. */
    public boolean containsKey(String key) {
        // TODO: YOUR CODE HERE
        for (Entry e : sNM[hash(key)]){
            if(key.equals(e.key)){
                return true;
            }
        }
        return false;
    }

    /* Returns the value for the specified KEY. If KEY is not found, return
       null. */
    public String get(String key) {
        // TODO: YOUR CODE HERE
        for (Entry e : sNM[hash(key)]){
            if(key.equals(e.key)){
                return e.value;
            }
        }
        return null;
    }

    /* Puts a (KEY, VALUE) pair into this map. If the KEY already exists in the
       SimpleNameMap, replace the current corresponding value with VALUE. */
    public void put(String key, String value) {
        // TODO: YOUR CODE HERE
        size +=1;
        Double loadfactor = ((double) len)/size;
        if(loadfactor > 0.75){
            resize();
        }

        if (sNM[hash(key)] == null){
            sNM[hash(key)] = new LinkedList<Entry>();
        }
        sNM[hash(key)].addLast(new Entry(key, value));
    }

    public void resize() {
        this.len *= 2;
        LinkedList<Entry>[] oldSNM = sNM.clone();
        this.sNM = new LinkedList[this.len];
        for(LinkedList<Entry> linkedE : oldSNM){
            if (linkedE == null) {continue;}
            for(Entry e : linkedE){
                if(e==null){continue;}
                this.put(e.key, e.value);
            }
        }
    }

    /* Removes a single entry, KEY, from this table and return the VALUE if
       successful or NULL otherwise. */
    public String remove(String key) {
        // TODO: YOUR CODE HERE
        String value;
        for (Entry e : sNM[hash(key)]){
            if(key.equals(e.key)){
                value = e.value;
                sNM[hash(key)].remove(e);
                size -=1;
                return value;
            }
        }
        return null;
    }

    private int hash(String key){
//        return key.charAt(0)-'A'
//        return Math.floorMod(key.hashCode(), 10);
        return Math.floorMod(key.hashCode(), 10);
    }

    private static class Entry {

        private String key;
        private String value;

        Entry(String key, String value) {
            this.key = key;
            this.value = value;
        }

        /* Returns true if this key matches with the OTHER's key. */
        public boolean keyEquals(Entry other) {
            return key.equals(other.key);
        }

        /* Returns true if both the KEY and the VALUE match. */
        @Override
        public boolean equals(Object other) {
            return (other instanceof Entry
                    && key.equals(((Entry) other).key)
                    && value.equals(((Entry) other).value));
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }
}