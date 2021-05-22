package gitlet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

public class LimeFamily {

    private LimeTree root = null;

    public LimeFamily(String[] ID_pair) {
        root = new LimeTree(ID_pair, null);
    }

    // move current branch node first
    public void addLeftChild(String[] PaID_pair, String childID) {
        if (root != null) {
            root.addChildHelper(PaID_pair[0], childID);
        }
    }
    // move merged branch node later
    public void addRightChild(String[] PaID_pair, String childID) {
        if (root != null) {
            root.addChildHelper(PaID_pair[1], childID);
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
    public void print() {

    }

    /* Returns an Iterator for this AmoebaFamily. */
    public Iterator<LimeTree> iterator() {
        return new LimeTreeDFSIterator();
    }

    public class LimeTree {
        public String[] PaSHA_pair;  //{remainedCommitID, MovedCommitID}
        public LimeTree parent;
        public ArrayList<LimeTree> children;

        public LimeTree(String[] paSHA_pair, LimeTree Parent){
            this.PaSHA_pair = paSHA_pair;
            this.parent = Parent;
            this.children = new ArrayList<LimeTree>();
        }

        public LimeTree getParent() {
            return parent;
        }
        public ArrayList<LimeTree> getChildren() {
            return children;
        }

        public void addChildHelper(String parentID, String childID) {
            if (PaSHA_pair[0].equals(parentID)) {
                LimeTree child = new LimeTree(new String[]{childID,PaSHA_pair[1]}, this);
                children.add(child);
            }else if(PaSHA_pair[1].equals(parentID)){
                LimeTree child = new LimeTree(new String[]{PaSHA_pair[0],childID}, this);
                children.add(child);
            }else {
                for (LimeTree a : children) {
                    a.addChildHelper(parentID, childID);
                }
            }
        }

    }

    public class LimeTreeDFSIterator implements Iterator<LimeTree> {

        private Stack<LimeTree> fringe = new Stack<LimeTree>();
        /* AmoebaDFSIterator constructor. Sets up all of the initial information
           for the AmoebaDFSIterator. */
        public LimeTreeDFSIterator(LimeTree root) {
            if (root != null) {
                fringe.push(root);
            }
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
            LimeTree node = fringe.pop();
            for(LimeTree child : node.children){
                if (child != null) {
                    fringe.push(child);
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

        // TODO: IMPLEMENT THE CLASS HERE

        /* AmoebaBFSIterator constructor. Sets up all of the initial information
           for the AmoebaBFSIterator. */
        public LimeTreeBFSIterator() {
        }

        /* Returns true if there is a next element to return. */
        public boolean hasNext() {
            return false;
        }

        /* Returns the next element. */
        public LimeTree next() {
            return null;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}


