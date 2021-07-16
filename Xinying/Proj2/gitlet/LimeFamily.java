package gitlet;

import java.util.*;
import java.util.function.Consumer;

public class LimeFamily {

    /* ROOT is the root lime of this LimeFamily */
    public LimeTree root;

    /* Creates an LimeFamily, where the first Lime's parent is an ID_pair. */
    public LimeFamily(String[] ID_pair) {
        root = new LimeTree(ID_pair, null);
    }

    // move current branch node first M1
    public void addLeftChild(LimeTree node, String childID) {
        if (node != null) {
            node.addChildHelper(node.PaSHA_pair[0], childID);
        }
    }
    // move merged branch node later M2
    public void addRightChild(LimeTree node, String childID) {
        if (node != null) {
            node.addChildHelper(node.PaSHA_pair[1], childID);
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

    /* Prints the name of all Limes in this LimeFamily in Depth-First Traversals, with
       the ROOT Lime printed first. */
    public void printDFS() {
        LimeTreeDFSIterator LimeTreeDFSiterator = new LimeTreeDFSIterator();
        while(LimeTreeDFSiterator.hasNext()){
            LimeTree node = LimeTreeDFSiterator.next();
            System.out.println(printNode(node));
        }
    }

    public void printBFS() {
        LimeTreeBFSIterator LimeTreeBFSiterator = new LimeTreeBFSIterator();
        while(LimeTreeBFSiterator.hasNext()){
            LimeTree node = LimeTreeBFSiterator.next();
            System.out.println(printNode(node));
        }
    }

    private String printNode(LimeTree node){
        String nodeString;
        nodeString = "{";
        for(String ID : node.PaSHA_pair){ nodeString += ID+"  "; }
        return nodeString + "}";
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
        public int arity(){ return children.size();}
        public LimeTree child(int k){ return children.get(k);}
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


