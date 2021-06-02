package gitlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Stack;

// limeTree structure
// each node: [Sha_Moved, Sha_Remained]
// have a parent tree
// have a series of children tree
public class LimeTreeFamily {
    private LimeTree root = null;

    public LimeTreeFamily(String Sha_Moved, String Sha_Remained){
        root = new LimeTree(Sha_Moved, Sha_Remained);
    }

    /* Where we will store the pending travel sha & compared sha */
    public Stack<String[]> fringe = new Stack<String[]>();

    public void addChild(){
        if (root != null){
            fringe.push(new String[] {root.PaSha_pair[1],root.PaSha_pair[0]});
            root.addChildHelper(root);
        }
    }

    /* Add child, move the Sha_pair[0] first, Sha_pair saved in stack */
    public void addChildHelper(LimeTree pa_tree) throws IOException {
        // Get the pa_sha by pa_tree.PaSha_pair[0]
        Commit this_commit = BranchManage.getCommit(pa_tree.PaSha_pair[0]);
        if(!this_commit.getPa_sha().equals("")){
            String[] pre_sha = this_commit.getPa_sha();
            LimeTree child_tree = new LimeTree(pre_sha[0], pa_tree.PaSha_pair[1], pa_tree);
            pa_tree.children.add(child_tree);
            // Put the rest into the fringe
            if(pre_sha.length > 1){
                for(String s : pre_sha){
                    fringe.push(new String[] {s,pa_tree.PaSha_pair[1]});
                }
            }
            fringe.push(new String[] {child_tree.PaSha_pair[0],child_tree.PaSha_pair[1]});
            next(child_tree);
            fringe.push(new String[] {child_tree.PaSha_pair[1],child_tree.PaSha_pair[0]});
        }
    }
    /* Returns whether or not we have any more nodes to visit. */
    public boolean hasNext() {
        return !fringe.isEmpty();
    }

    /* Throws an exception if we have no more nodes to visit in our traversal.
       Otherwise, it picks the most recent entry to our stack and "explores" it.
       Exploring it requires visiting the node and adding its children to the
       fringe, since we must eventually visit them too. */
    public void next(LimeTree pa) throws IOException {
        if (!hasNext()) {
            throw new NoSuchElementException("Tree has completed");
        }
        String[] move_compare = fringe.pop();
        addChildHelper(pa);
    }

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
    }
}

