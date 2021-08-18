package gitlet;

import java.util.*;
import java.util.function.Consumer;

public class LimeFamily {

    /* ROOT is the root lime of this LimeFamily */
    public LimeTree root;

    /* Creates an LimeFamily, where the first Lime's parent is an ID_pair. */
    public LimeFamily(Commit[] ID_pair) {
        root = new LimeTree(ID_pair, null);
    }

    // move current branch node first M1
    public void addLeftChild(LimeTree node, Commit child) {
        if (node != null) {
            node.addChildHelper(node.Parents_pair[0], child);
        }
    }
    // move merged branch node later M2
    public void addRightChild(LimeTree node, Commit child) {
        if (node != null) {
            node.addChildHelper(node.Parents_pair[1], child);
        }
    }
/*    public void addChild(String[] PaID_pair) {
        if (root != null) {
            LimeTree child = new LimeTree(PaID_pair, this);
            root.children.add(child);
        }
    }*/

    /* Prints the name of all Limes in this LimeFamily in preorder, with
       the ROOT Lime printed first. Each Lime should be indented four spaces
       more than its parent. */
/*
    public void print() {
        System.out.print(printHelper(root));
    }

    private String printHelper(LimeTree node){
        if(node.arity() == 0) return printNode(node);
        else{
            String R;
            R = printNode(node);
            for(int i = 0;i < node.arity();i +=1){
                R += "    " + "/n"+ printHelper(node.child(i));
                return R ;
            }
        }
        return null;
    }
*/

    /* Prints the name of all Limes in this LimeFamily in Depth-First Traversals, with
       the ROOT Lime printed first. */
    public void printDFS() {
        LimeTreeDFSIterator LimeTreeDFSiterator = new LimeTreeDFSIterator();
        while(LimeTreeDFSiterator.hasNext()){
            LimeTree node = LimeTreeDFSiterator.next();
            System.out.println(printNode(node));
        }
    }

    public LimeTree SplitPoint() {
        LimeTreeDFSIterator LimeTreeDFSiterator = new LimeTreeDFSIterator();
        LimeTree tempAncestor = new LimeTree();
        while(LimeTreeDFSiterator.hasNext()){
            LimeTree node = LimeTreeDFSiterator.next();
            if(SplitPointHelper(node,tempAncestor)){
                tempAncestor = node;
            }
        }
        return tempAncestor;
    }
    private Boolean SplitPointHelper(LimeTree node,LimeTree tempAncestor){
        if(node.Parents_pair[0].Metadata[0].equals(node.Parents_pair[1].Metadata[0])){
            if(tempAncestor.height > node.height){return true;}
        }
        return false;
    }

    /*public void printBFS() {
        LimeTreeBFSIterator LimeTreeBFSiterator = new LimeTreeBFSIterator();
        while(LimeTreeBFSiterator.hasNext()){
            LimeTree node = LimeTreeBFSiterator.next();
            System.out.println(printNode(node));
        }
    }*/
    private String printNode(LimeTree node){
        String nodeString;
        nodeString = "{";
        for(Commit ID : node.Parents_pair){ nodeString += ID.Metadata[0] +"  "; }
        return nodeString + "}";
    }

    /* Returns an Iterator for this AmoebaFamily. */
    public Iterator<LimeTree> iterator() {
        return new LimeTreeDFSIterator();
    }

    public class LimeTree {
        public Commit[] Parents_pair;  //{remainedCommitID, MovedCommitID}
        public LimeTree parent;
        public ArrayList<LimeTree> children;
        public int height;

        public LimeTree(Commit[] paSHA_pair, LimeTree Parent){
            this.Parents_pair = paSHA_pair;
            this.parent = Parent;
            this.children = new ArrayList<LimeTree>();
            if(Parent==null){
                this.height = 0;
            }else{this.height = parent.height+1;}
        }
        public LimeTree(){
            this.height = 10000;
        }

        public LimeTree getParent() {
            return parent;
        }
        public ArrayList<LimeTree> getChildren() {
            return children;
        }
        public int arity(){ return children.size();}
        public LimeTree child(int k){ return children.get(k);}

        public void addChildHelper(Commit parent, Commit baby) {
            if (Parents_pair[0].equals(parent)) {
                LimeTree child = new LimeTree(new Commit []{baby,Parents_pair[1]}, this);
                children.add(child);
            }else if(Parents_pair[1].equals(parent)){
                LimeTree child = new LimeTree(new Commit []{Parents_pair[0],baby}, this);
                children.add(child);
            }else {
                for (LimeTree a : children) {
                    a.addChildHelper(parent, baby);
                }
            }
        }
    }

    public class LimeTreeDFSIterator implements Iterator<LimeTree> {

        private Stack<LimeTree> fringe = new Stack<LimeTree>();
        /* AmoebaDFSIterator constructor. Sets up all of the initial information
           for the AmoebaDFSIterator. */
        public LimeTreeDFSIterator() {
            fringe.push(root);
        }

        /* Returns true if there is a next element to return. */
        public boolean hasNext() {
            return !fringe.isEmpty();
        }

        /* Returns the next element. */
        public LimeTree next() {
            /*if (!hasNext()) {
                throw new NoSuchElementException("tree ran out of elements");
            }*/
            LimeTree node = fringe.pop();
            for(int i = node.arity()-1;i>=0;i-=1){
                if (node.child(i) != null) {
                    fringe.push(node.child(i));
                }
            }
            return node;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /* An Iterator class for the AmoebaFamily, running a BFS traversal on the
       AmoebaFamily. Complete enumeration of a family of N Amoebas should take
       O(N) operations. */
    public class LimeTreeBFSIterator implements Iterator<LimeTree> {

        /* AmoebaBFSIterator constructor. Sets up all of the initial information
           for the AmoebaBFSIterator. */
        private ArrayDeque<LimeTree> fringe = new ArrayDeque<LimeTree>();

        public LimeTreeBFSIterator() {
            fringe.push(root);
        }

        /* Returns true if there is a next element to return. */
        public boolean hasNext() {
            return !fringe.isEmpty();
        }

        /* Returns the next element. */
        public LimeTree next() {
            if (!hasNext()) {
                throw new NoSuchElementException("tree ran out of elements");
            }
            LimeTree node = fringe.remove();
            for(int i = node.arity()-1;i>=0;i-=1){
                if (node.child(i) != null) {
                    fringe.push(node.child(i));
                }
            }
            return node;

        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}


