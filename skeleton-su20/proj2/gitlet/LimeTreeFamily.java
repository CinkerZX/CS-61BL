package gitlet;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

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

        // Constructor
        public LimeTree(String Sha_Moved, String Sha_Remained, LimeTree Pa_tree) {
            this.PaSha_pair = new String[]{Sha_Moved, Sha_Remained};
            this.parent = Pa_tree;
            this.children = new ArrayList<LimeTree>();
        }

        public LimeTree(String Sha_Moved, String Sha_Remained) {
            PaSha_pair = new String[]{Sha_Moved, Sha_Remained};
            parent = null;
            this.children = new ArrayList<LimeTree>();
        }

        // Add sub tree
        public void addChild(LimeTree child_tree){
            this.children.add(child_tree);
        }

        public LimeTree getParent() {
            return parent;
        }

        public ArrayList<LimeTree> getChildren() {
            return children;
        }

        public String[] getnode(){
            return PaSha_pair;
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
            fringe.push(new String[] {root.PaSha_pair[1],root.PaSha_pair[0]});
            addChildHelper(root);
        }
    }

    /* Add child, move the Sha_pair[0] first, Sha_pair saved in stack */
    public void addChildHelper(LimeTree pa) throws IOException {
        if (!hasNext()) {
            throw new NoSuchElementException("Fringe is clean");
        }
        else{
            // get the string pair out from fringe
            String[] move_compare = fringe.pop();
            // Get the pa_sha by pa_tree.PaSha_pair[0](move)
            Commit this_commit = BranchManage.getCommit(move_compare[0]);
            if(!this_commit.getPa_sha().equals("")){
                //put the pre_sha[1](retain) [0] in the fringe
                fringe.push(new String[] {move_compare[1],move_compare[0]});
                String[] pre_sha = this_commit.getPa_sha();
                LimeTree child_tree = new LimeTree(pre_sha[0], move_compare[1], pa);
                //add tree
                pa.children.add(child_tree);
                // Put the rest into the fringe
                if(pre_sha.length > 1){
                    for(String s : pre_sha){
                        fringe.push(new String[] {s,move_compare[1]});
                    }
                }
                addChildHelper(child_tree);
            }
            else{//push the remain into the fringe
                fringe.push(new String[] {move_compare[1],move_compare[0]});
                addChildHelper(pa);
            }
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

/*
Depth first travelsal
 */
    public void depth_First_Tra(){
        LimeTree root_node = getRoot();
        //Stack<LimeTree> fringe = new Stack<LimeTree>();
        ArrayDeque<LimeTree> fringe_2 = new ArrayDeque<>();
        ArrayList<LimeTree> children = root_node.getChildren();
        System.out.println(children.size());
        Collections.reverse(Arrays.asList(children));
        for(LimeTree t : children){
            //fringe.push(t);
            fringe_2.add(t);
        }
        tree_print("", root_node);
        //Depth_Tra_helper(fringe,a);
        Width_Tra_helper(fringe_2,a);
    }

    public String a = "    ";

    public void Depth_Tra_helper(Stack<LimeTree> bookmark, String head){
        if(!bookmark.isEmpty()){
            LimeTree tree_to_print = bookmark.pop();
            tree_print(head, tree_to_print);
            Collections.reverse(Arrays.asList(tree_to_print.children));
            for (LimeTree t : tree_to_print.children){
                bookmark.push(t);
            }
            Depth_Tra_helper(bookmark,a+head);
        }
    }
/*
Width first travelsal
Just change the (LIFO) stack to a (FIFO) queue
 */
    public void Width_Tra_helper(ArrayDeque<LimeTree> bookmark, String head){
        if(!bookmark.isEmpty()){
            LimeTree tree_to_print = bookmark.remove();
            tree_print(head,tree_to_print);
            for (LimeTree t : tree_to_print.children){
                bookmark.add(t);
            }
            Width_Tra_helper(bookmark,a+head);
        }
    }

    // print out the tree
    public void tree_print(String HEAD, LimeTree p_tree){
        System.out.print(HEAD);
        System.out.println(p_tree.PaSha_pair[0].toString()+p_tree.PaSha_pair[1].toString());
    }
}
