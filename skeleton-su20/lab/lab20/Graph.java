import sun.awt.image.ImageWatched;
import sun.usagetracker.UsageTrackerClient;

import java.util.*;

public class Graph implements Iterable<Integer> {

    private LinkedList<Edge>[] adjLists;
    private int vertexCount;

    /* Initializes a graph with NUMVERTICES vertices and no Edges. */
    public Graph(int numVertices) {
        adjLists = (LinkedList<Edge>[]) new LinkedList[numVertices];
        for (int k = 0; k < numVertices; k++) {
            adjLists[k] = new LinkedList<Edge>();
        }
        vertexCount = numVertices;
    }

    /* Adds a directed Edge (V1, V2) to the graph. That is, adds an edge
       in ONE directions, from v1 to v2. */
    public void addEdge(int v1, int v2) { addEdge(v1, v2, 0);
    }

    /* Adds an undirected Edge (V1, V2) to the graph. That is, adds an edge
       in BOTH directions, from v1 to v2 and from v2 to v1. */
    public void addUndirectedEdge(int v1, int v2) {
        addUndirectedEdge(v1, v2, 0);
    }

    /* Adds a directed Edge (V1, V2) to the graph with weight WEIGHT. If the
       Edge already exists, replaces the current Edge with a new Edge with
       weight WEIGHT. */
    public void addEdge(int v1, int v2, int weight) {
        // TODO: YOUR CODE HERE
        for (Edge i : adjLists[v1]) {
            if (i.to == v2){
                adjLists[v1].remove(i);
            }
        }
        adjLists[v1].add(new Edge(v1, v2, weight));
    }

    /* Adds an undirected Edge (V1, V2) to the graph with weight WEIGHT. If the
       Edge already exists, replaces the current Edge with a new Edge with
       weight WEIGHT. */
    public void addUndirectedEdge(int v1, int v2, int weight) {
        // TODO: YOUR CODE HERE
        addEdge(v1, v2, weight);
        addEdge(v2, v1, weight);
    }

    /* Returns true if there exists an Edge from vertex FROM to vertex TO.
       Returns false otherwise. */
    public boolean isAdjacent(int from, int to) {
        // TODO: YOUR CODE HERE
        for (Edge i : adjLists[from]){
            if (i.to == to){
                return true;
            }
        }
        return false;
    }

    /* Returns a list of all the vertices u such that the Edge (V, u)
       exists in the graph. */
    public List<Integer> neighbors(int v) {
        // TODO: YOUR CODE HERE
        List<Integer> neighbor = new ArrayList<Integer>();
        for (Edge i : adjLists[v]) {
            neighbor.add(i.to);
        }
        return neighbor;
    }

    public List<Integer> inNeighbors(int v) {
        // TODO: YOUR CODE HERE
        List<Integer> neighbor = new ArrayList<Integer>();
        for (LinkedList<Edge> i : adjLists) {
            for (Edge e : i){
                if (e.to == v){
                    neighbor.add(e.from);
                }
            }
        }
        return neighbor;
    }

    /* Returns the number of incoming Edges for vertex V. */
    public int inDegree(int v) {
        // TODO: YOUR CODE HERE
        int numIn = 0;
        for (LinkedList<Edge> i : adjLists) {
            for (Edge e : i){
                if (e.to == v){
                    numIn++;
                }
            }
        }
        return numIn;
    }

    /* Returns an Iterator that outputs the vertices of the graph in topological
       sorted order. */
    public Iterator<Integer> iterator() {
        return new TopologicalIterator();
    }

    /**
     *  A class that iterates through the vertices of this graph,
     *  starting with a given vertex. Does not necessarily iterate
     *  through all vertices in the graph: if the iteration starts
     *  at a vertex v, and there is no path from v to a vertex w,
     *  then the iteration will not include w.
     */
    private class DFSIterator implements Iterator<Integer> {

        private Stack<Integer> fringe;
        private HashSet<Integer> visited;

        public DFSIterator(Integer start) {
            fringe = new Stack<>();
            visited = new HashSet<>();
            fringe.push(start);
        }

        public boolean hasNext() {
            if (!fringe.isEmpty()) {
                int i = fringe.pop();
                while (visited.contains(i)) {
                    if (fringe.isEmpty()) {
                        return false;
                    }
                    i = fringe.pop();
                }
                fringe.push(i);
                return true;
            }
            return false;
        }

        public Integer next() {
            int curr = fringe.pop();
            ArrayList<Integer> lst = new ArrayList<>();
            for (int i : neighbors(curr)) {
                lst.add(i);
            }
            lst.sort((Integer i1, Integer i2) -> -(i1 - i2));
            for (Integer e : lst) {
                fringe.push(e);
            }
            visited.add(curr);
            return curr;
        }

        //ignore this method
        public void remove() {
            throw new UnsupportedOperationException(
                    "vertex removal not implemented");
        }

    }

    /* Returns the collected result of performing a depth-first search on this
       graph's vertices starting from V. */
    public List<Integer> dfs(int v) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        Iterator<Integer> iter = new DFSIterator(v);

        while (iter.hasNext()) {
            result.add(iter.next());
        }
        return result;
    }

    /* Returns true iff there exists a path from START to STOP. Assumes both
       START and STOP are in this graph. If START == STOP, returns true. */
    public boolean pathExists(int start, int stop) {
        // TODO: YOUR CODE HERE
        if(start == stop){
            return true;
        }
        List<Integer> dftStart = dfs(start);
        for (int i : dftStart) {
            if (i == stop){
                return true;
            }
        }
        return false;
    }


    /* Returns the path from START to STOP. If no path exists, returns an empty
       List. If START == STOP, returns a List with START. */
    public List<Integer> path(int start, int stop) {
        // TODO: YOUR CODE HERE
        LinkedList<Integer> path = new LinkedList<Integer>();
        if (start==stop){
            return new ArrayList<Integer>(start);
        }

        ArrayList<Integer> result = new ArrayList<Integer>();
        Iterator<Integer> iter = new DFSIterator(start);
        Boolean pathExists = false;
        while (iter.hasNext()) {
            Integer i = iter.next();
            result.add(i);
            if (i==stop){
                pathExists = true;
            }
        }

        if (!pathExists){
            return path;
        }

        path.addFirst(stop);
        while (!isAdjacent(start,stop)){
            for (Integer i : inNeighbors(stop)) {
                if (result.contains(i)){
                    path.addFirst(i);
                    stop = i;
                    break;
                }
            }
        }
        path.addFirst(start);
        return path;
    }

    public List<Integer> topologicalSort() {
        ArrayList<Integer> result = new ArrayList<Integer>();
        Iterator<Integer> iter = new TopologicalIterator();
        while (iter.hasNext()) {
            result.add(iter.next());
        }
        return result;
    }

    private class TopologicalIterator implements Iterator<Integer> {

        private Stack<Integer> fringe;
        private int[] currentInDegree; // nodes int | updated indegree

        // TODO: Instance variables here!

        public TopologicalIterator() {
            fringe = new Stack<Integer>();
            currentInDegree = new int[vertexCount];
            // TODO: put all initial indegree into
            for (int i = 0; i < vertexCount; i++) {
                int indegree_i = inDegree(i);
                currentInDegree[i] = indegree_i;
                if (indegree_i == 0){
                    fringe.push(i);
                }
            }
        }

        public boolean hasNext() {
            // TODO: YOUR CODE HERE
            if (!fringe.isEmpty()) {
                int i = fringe.pop();
                while (currentInDegree[i] == -1) { //eliminate the visited (idegree==-1) one
                    if (fringe.isEmpty()) {
                        return false;
                    }
                    i = fringe.pop();
                }
                fringe.push(i);
                return true;
            }
            return false;
        }

        public Integer next() {
            int curr = fringe.pop();
            for (Integer e : neighbors(curr)) {
                currentInDegree[e]--;
                if (currentInDegree[e]==0){
                    fringe.push(e);
                }
            }
            currentInDegree[curr]--;
            return curr;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    private class Edge {

        private int from;
        private int to;
        private int weight;

        Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        public String toString() {
            return "(" + from + ", " + to + ", weight = " + weight + ")";
        }

    }

    private void generateG1() {
        addEdge(0, 1);
        addEdge(0, 2);
        addEdge(0, 4);
        addEdge(1, 2);
        addEdge(2, 0);
        addEdge(2, 3);
        addEdge(4, 3);
    }

    private void generateG2() {
        addEdge(0, 1);
        addEdge(0, 2);
        addEdge(0, 4);
        addEdge(1, 2);
        addEdge(2, 3);
        addEdge(4, 3);
    }

    private void generateG3() {
        addUndirectedEdge(0, 2);
        addUndirectedEdge(0, 3);
        addUndirectedEdge(1, 4);
        addUndirectedEdge(1, 5);
        addUndirectedEdge(2, 3);
        addUndirectedEdge(2, 6);
        addUndirectedEdge(4, 5);
    }

    private void generateG4() {
        addEdge(0, 1);
        addEdge(1, 2);
        addEdge(2, 0);
        addEdge(2, 3);
        addEdge(4, 2);
    }

    private void printDFS(int start) {
        System.out.println("DFS traversal starting at " + start);
        List<Integer> result = dfs(start);
        Iterator<Integer> iter = result.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next() + " ");
        }
        System.out.println();
        System.out.println();
    }

    private void printPath(int start, int end) {
        System.out.println("Path from " + start + " to " + end);
        List<Integer> result = path(start, end);
        if (result.size() == 0) {
            System.out.println("No path from " + start + " to " + end);
            return;
        }
        Iterator<Integer> iter = result.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next() + " ");
        }
        System.out.println();
        System.out.println();
    }

    private void printTopologicalSort() {
        System.out.println("Topological sort");
        List<Integer> result = topologicalSort();
        Iterator<Integer> iter = result.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next() + " ");
        }
    }


    public ArrayList<Integer> shortestPath(int start, int stop) {
        // TODO: YOUR CODE HERE
        Queue<Edge> fringe = new PriorityQueue<>(new EdgeComparator());
        HashMap<Integer,Integer> paths = new HashMap<>();   // HashMap<To, From>
        HashSet<Integer> notVisited = new HashSet<>();         // visited nodes
        HashSet<Integer> updated = new HashSet<>();
        ArrayList<Integer> SPath = new ArrayList<>();

        for (int k=0; k < vertexCount; k++){
            notVisited.add(k);
            if (start !=k){
                fringe.offer(getEdge(start,k));
            }
//            else{
//                fringe.offer(new Edge(start,k,0));
//            }
        }
        while(!notVisited.isEmpty() || !fringe.isEmpty()) {
            Edge temp = fringe.poll();
            if (temp == null){break;}
            if (!updated.contains(temp.to)){
                paths.put(temp.to,temp.from);
                System.out.println("("+temp.to+","+temp.from+")   "+temp.weight);
            }
            updated.add(temp.to);
            if (temp == null){break;}
            for (Edge e : adjLists[temp.to]){
                if (notVisited.contains(e.to)){ // add the neighbor into the fringe
                    Edge tempE = new Edge(e.from, e.to, Math.abs(e.weight+temp.weight));
                    fringe.offer(tempE);
                }
            }
            notVisited.remove(temp.to); // maintain
        }

        if(paths.get(stop) == null){

        }else{
            SPath.add(0, stop);
            int vertex = paths.get(stop).intValue();
            while(vertex != start){
                SPath.add(0, vertex);
                vertex = paths.get(vertex).intValue();
            }
        }

        SPath.add(0,start);
        return SPath;
    }

    public Edge getEdge(int u, int v) {
        // TODO: YOUR CODE HERE
        for (Edge e : adjLists[u]){
            if (e.to == v){
                return new Edge(e.from,e.to,e.weight);
            }
        }
        return new Edge(u,v,Integer.MAX_VALUE);
    }

    class EdgeComparator implements Comparator<Edge> {
        public int compare(Edge u, Edge v) {
            if (u.weight == v.weight) {
                return 0;
            }
            if (u.weight < v.weight) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    public static void main(String[] args) {
        Graph g1 = new Graph(5);
        g1.generateG1();
        g1.printDFS(0);
        g1.printDFS(2);
        g1.printDFS(3);
        g1.printDFS(4);

        g1.printPath(0, 3);
        g1.printPath(0, 4);
        g1.printPath(1, 3);
        g1.printPath(1, 4);
        g1.printPath(4, 0);

        Graph g2 = new Graph(5);
        g2.generateG2();
        g2.printTopologicalSort();
    }
}