package gitlet;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
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

    private LimeTree root = null;

    public LimeTree getRoot(){
        System.out.println(root.PaSha_pair);
        return root;
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
    public void addChildHelper(LimeTree pa_tree) throws IOException {
        // Get the pa_sha by pa_tree.PaSha_pair[0](move)
        Commit this_commit = BranchManage.getCommit(pa_tree.PaSha_pair[0]);
        if(!this_commit.getPa_sha().equals("")){
            //put the pre_sha[1](retain) [0] in the fringe
            fringe.push(new String[] {pa_tree.PaSha_pair[1],pa_tree.PaSha_pair[0]});
            String[] pre_sha = this_commit.getPa_sha();
            LimeTree child_tree = new LimeTree(pre_sha[0], pa_tree.PaSha_pair[1], pa_tree);
            //add tree
            pa_tree.children.add(child_tree);
            // Put the rest into the fringe
            if(pre_sha.length > 1){
                for(String s : pre_sha){
                    fringe.push(new String[] {s,pa_tree.PaSha_pair[1]});
                }
            }
            next(child_tree);
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
        Stack<LimeTree> fringe = new Stack<LimeTree>();
        //ArrayDeque<LimeTree> fringe_2 = new ArrayDeque<>();
        ArrayList<LimeTree> children = root_node.getChildren();
        Collections.reverse(Arrays.asList(children));
        for(LimeTree t : children){
            fringe.push(t);
            //fringe_2.push(t);
        }
        Depth_Tra_helper(root_node,fringe,a);
        //Width_Tra_helper(root_node,fringe_2,a);
    }

    public String a = "    ";

    public void Depth_Tra_helper(LimeTree T, Stack<LimeTree> bookmark, String head){
        if(!bookmark.isEmpty()){
            LimeTree tree_to_print = bookmark.pop();
            tree_print(head, tree_to_print);
            Collections.reverse(Arrays.asList(tree_to_print.children));
            for (LimeTree t : tree_to_print.children){
                bookmark.push(t);
                Depth_Tra_helper(t,bookmark,a+head);
            }
        }
    }
/*
Width first travelsal
Just change the (LIFO) stack to a (FIFO) queue
 */
    public void Width_Tra_helper(LimeTree T, ArrayDeque<LimeTree> bookmark, String head){
        if(!bookmark.isEmpty()){
            LimeTree tree_to_print = bookmark.remove();
            tree_print(head,tree_to_print);
            for (LimeTree t : tree_to_print.children){
                bookmark.push(t);
                Width_Tra_helper(t,bookmark,a+head);
            }
        }
    }

    // print out the tree
    public void tree_print(String HEAD, LimeTree p_tree){
        System.out.print(HEAD);
        System.out.println(p_tree.PaSha_pair);
    }
}
