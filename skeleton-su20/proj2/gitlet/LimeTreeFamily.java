package gitlet;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

import static gitlet.BranchManage.getCommit;

// limeTree structure
// each node: [Sha_Moved, Sha_Remained]
// have a parent tree, and a series of children tree

/*
How to construct a tree:
    1) Use LimeTreeFamily constructs a root
    2) Use addChild to add all the child, it calls addChildHelper which add child recursively
    3) Note: addChildHelper will iterate until the fringe is cleaned which judged by function next
*/
public class LimeTreeFamily {
    // inside class
    public static class LimeTree {
        public String[] PaSha_pair;
        public LimeTree parent;
        public ArrayList<LimeTree> children;
        public int level;

        // Constructor
        public LimeTree(String Sha_Moved, String Sha_Remained, LimeTree Pa_tree) {
            this.PaSha_pair = new String[]{Sha_Moved, Sha_Remained};
            this.parent = Pa_tree;
            this.children = new ArrayList<LimeTree>();
            this.level = 0;
        }

        public LimeTree(String Sha_Moved, String Sha_Remained) {
            PaSha_pair = new String[]{Sha_Moved, Sha_Remained};
            parent = null;
            this.children = new ArrayList<LimeTree>();
            this.level = 0;
        }

        // Add sub tree
        public void addChild(LimeTree child_tree){
            this.children.add(child_tree);
        }
    }

    // Feature of LimeTreeFamily
    private LimeTree root = null;

    // Constructor of LimeTreeFamily
    public LimeTreeFamily(String Sha_Moved, String Sha_Remained) {
        root = new LimeTree(Sha_Moved, Sha_Remained);
    }

    // Methods of LimeTreeFamily
    public LimeTree getRoot(){
        return root;
    }

    public void expandLimeTree(LimeTree pa_tree, LimeTree child_tree){
        //TODO: add subtrees
        pa_tree.addChild(child_tree);
    }

    /* Where we will store the pending travel sha & compared sha */
    public Stack<String[]> fringe = new Stack<String[]>();

    public void addChild() throws IOException {
        if (root != null){
            fringe.push(new String[] {root.PaSha_pair[0],root.PaSha_pair[1]});
            addChildHelper(root);
        }
    }

    /* Add child, move the Sha_pair[0] first, Sha_pair saved in stack */
    public void addChildHelper(LimeTree pa) throws IOException {
        if (!hasNext()) {
            System.out.println("Fringe is clean");
        }
        else{
            // (1) get the string pair out from fringe
            String[] move_compare = fringe.pop();  // (2) get out from the fringe
            // ensure pa
            while((!pa.PaSha_pair[0].equals(move_compare[0]) & !pa.PaSha_pair[0].equals(move_compare[1])) || (!pa.PaSha_pair[1].equals(move_compare[0]) & !pa.PaSha_pair[1].equals(move_compare[1]))){
                pa = pa.parent;
            }
            // Get the pa_sha by pa_tree.PaSha_pair[0](move)
            Commit this_commit = getCommit(move_compare[0]);
            // Pay attention to the pushing requirements
            if(!this_commit.getMetadata()[0].equals("initial commit") & !move_compare[0].equals(move_compare[1])){
                if (!getCommit(this_commit.getPa_sha()[0]).getMetadata()[0].equals("initial commit") & !move_compare[1].equals(move_compare[0])) {
                    fringe.push(new String[]{move_compare[1], move_compare[0]}); // (3) put the changed position pairs in

                }
                String[] pre_sha = this_commit.getPa_sha();
                LimeTree child_tree = new LimeTree(pre_sha[0], move_compare[1], pa);
                //add tree
                pa.children.add(child_tree); // trace back and add tree
                // Put the rest into the fringe
                if(pre_sha.length > 1){
                    Collections.reverse(Arrays.asList(pre_sha)); // reverse Arrays.asList can be used to String or Integer
                    for(String s : pre_sha){
                        if(!move_compare[1].equals(s) & !move_compare[1].equals("initial commit")) {
                            fringe.push(new String[]{move_compare[1], s});
                        }
                    }
                }
                else{
                    if(!move_compare[1].equals(pre_sha[0]) & !move_compare[1].equals("initial commit")){
                        fringe.push(new String[]{move_compare[1],pre_sha[0]}); // (4) put the traced back pair into
                    }
                }
                if(!pre_sha[0].equals(move_compare[1]) & !pre_sha[0].equals("initial commit")) {
                    fringe.push(new String[]{pre_sha[0], move_compare[1]}); // (5) put the added tree pair into, so that next time will trace back from here
                }
                pa = child_tree;
            }
            addChildHelper(pa);
        }
    }

    /* Returns whether or not we have any more nodes to visit. */
    public boolean hasNext() {
        return !fringe.isEmpty();
    }
    /* iteration*/
    public void next(LimeTree pa) throws IOException {
        if (!hasNext()) {
            throw new NoSuchElementException("Fringe is clean");
        }
        // get the string pair out from fringe
        String[] move_compare = fringe.pop();
        LimeTree child_tree = new LimeTree(move_compare[0], move_compare[1], pa);
        addChildHelper(child_tree);
    }

    /* Returns the split point */
    public void splitPoint_helper(LimeTree splitPoint){
        // todo: this function is for updating the commit id, if the leaf has shorter height, then update sp and h
        if(splitPoint.level <= h & !splitPoint.equals(sp)){
            h = splitPoint.level;
            sp = splitPoint;
        }
    }

    public String splitPoint() throws IOException {
        // todo: return the commit id of the split point
        depth_First_Tra_withoutPrint();
        return sp.PaSha_pair[0];
    }

/*
Depth first travelsal
 */
    public void depth_First_Tra() throws IOException {
        LimeTree root_node = getRoot();
        root_node.level = 0;
        Stack<LimeTree> fringe = new Stack<LimeTree>();
        // ArrayDeque<LimeTree> fringe_2 = new ArrayDeque<>();
        ArrayList<LimeTree> children = root_node.children;
        Collections.reverse(children); // attention: here do not need to add "Arrays.asList"
        for(LimeTree t : children){
            fringe.push(t);
            //fringe_2.add(t);
            t.level = 1;
        }
        tree_print(root_node);
        Depth_Tra_helper(fringe);
        //Width_Tra_helper(fringe_2);
    }

    public void depth_First_Tra_withoutPrint() throws IOException {
        LimeTree root_node = getRoot();
        root_node.level = 0;
        Stack<LimeTree> fringe = new Stack<LimeTree>();
        ArrayList<LimeTree> children = root_node.children;
        Collections.reverse(children); // attention: here do not need to add "Arrays.asList"
        for(LimeTree t : children){
            fringe.push(t);
            t.level = 1;
        }
        Depth_Tra_helper_withoutPrint(fringe);
    }

    public static LimeTree sp;
    public static int h = 10000;

    public void Depth_Tra_helper(Stack<LimeTree> bookmark) throws IOException {
        if(!bookmark.isEmpty()){
            LimeTree tree_to_print = bookmark.pop();
            tree_print(tree_to_print);
            if(tree_to_print.PaSha_pair[0].equals(tree_to_print.PaSha_pair[1])){
                splitPoint_helper(tree_to_print);
            }
            else{
                for (LimeTree t : tree_to_print.children){
                    t.level = t.parent.level+1;
                }
            }
            Collections.reverse(tree_to_print.children);
            for (LimeTree t : tree_to_print.children){
                bookmark.push(t);
            }
            Depth_Tra_helper(bookmark);
        }
    }

    public void Depth_Tra_helper_withoutPrint(Stack<LimeTree> bookmark) throws IOException {
        if(!bookmark.isEmpty()){
            LimeTree tree_to_print = bookmark.pop();
            if(tree_to_print.PaSha_pair[0].equals(tree_to_print.PaSha_pair[1])){
                splitPoint_helper(tree_to_print);
            }
            else{
                for (LimeTree t : tree_to_print.children){
                    t.level = t.parent.level+1;
                }
            }
            Collections.reverse(tree_to_print.children);
            for (LimeTree t : tree_to_print.children){
                bookmark.push(t);
            }
            Depth_Tra_helper_withoutPrint(bookmark);
        }
    }
/*
Width first travelsal
Just change the (LIFO) stack to a (FIFO) queue
 */
    public void Width_Tra_helper(ArrayDeque<LimeTree> bookmark) throws IOException {
        if(!bookmark.isEmpty()){
            LimeTree tree_to_print = bookmark.remove();
            tree_print(tree_to_print);
            for (LimeTree t : tree_to_print.children){
                bookmark.add(t);
            }
            Width_Tra_helper(bookmark);
        }
    }

    // print out the tree
    public void tree_print(LimeTree p_tree) throws IOException {
        System.out.print(StringUtils.repeat("    ", p_tree.level));
        System.out.println(getCommit(p_tree.PaSha_pair[0].toString()).getMetadata()[0]+getCommit(p_tree.PaSha_pair[1].toString()).getMetadata()[0]);
    }
}
