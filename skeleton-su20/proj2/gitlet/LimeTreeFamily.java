package gitlet;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.zip.GZIPOutputStream;

import static gitlet.BranchManage.getCommit;
import static gitlet.NBtable.*;

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
        int count_pass_root = 1;
        if (root != null){
            fringe.push(new String[] {root.PaSha_pair[0],root.PaSha_pair[1]});
            addChildHelper(root, count_pass_root);
        }
    }

    /* Add child, move the Sha_pair[0] first, Sha_pair saved in stack */
    public void addChildHelper(LimeTree pa, int counter) throws IOException {
        if (!hasNext()) {
            //System.out.println("Fringe is clean");
        }
        else{
            // (1) get the string pair out from fringe
            String[] move_compare = fringe.pop();  // (2) get out from the fringe
            // ensure pa
            while((!pa.PaSha_pair[0].equals(move_compare[0]) & !pa.PaSha_pair[0].equals(move_compare[1])) || (!pa.PaSha_pair[1].equals(move_compare[0]) & !pa.PaSha_pair[1].equals(move_compare[1])) || (pa.PaSha_pair[0].equals(pa.PaSha_pair[1]))) {
                pa = pa.parent;
                if (pa.PaSha_pair[0].equals(move_compare[0]) & pa.PaSha_pair[1].equals(move_compare[1]) & hasNext()){
                    move_compare = fringe.pop();  // (2) get out from the fringe
                }
            }

            if(pa.PaSha_pair[0].equals(root.PaSha_pair[0]) & pa.PaSha_pair[1].equals(root.PaSha_pair[1])){
                counter = counter+1;
            }

            // Get the pa_sha by pa_tree.PaSha_pair[0](move)
            Commit this_commit = getCommit(move_compare[0]);
            // Pay attention to the pushing requirements
            if(counter != 4){ //**************************trap
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
                            if(!move_compare[1].equals(s) & !getCommit(move_compare[1]).getMetadata()[0].equals("initial commit")) {
                                fringe.push(new String[]{move_compare[1], s});
                            }
                        }
                    }
                    else{
                        if(!move_compare[1].equals(pre_sha[0]) & !getCommit(move_compare[1]).getMetadata()[0].equals("initial commit")){
                            fringe.push(new String[]{move_compare[1],pre_sha[0]}); // (4) put the traced back pair into
                        }
                    }
                    if(!pre_sha[0].equals(move_compare[1]) & !getCommit(pre_sha[0]).getMetadata()[0].equals("initial commit")) {
                        fringe.push(new String[]{pre_sha[0], move_compare[1]}); // (5) put the added tree pair into, so that next time will trace back from here
                    }
                    pa = child_tree;
                }
                addChildHelper(pa, counter);
            }
        }
    }

    /* Returns whether or not we have any more nodes to visit. */
    public boolean hasNext() {
        return !fringe.isEmpty();
    }

    /* Returns the split point */
    public void splitPoint_helper(LimeTree splitPoint){
        // todo: this function is for updating the commit id, if the leaf has shorter height, then update sp and h
        if(splitPoint.level <= h & !splitPoint.equals(sp)){
            h = splitPoint.level;
            sp = splitPoint;
        }
    }

    public LimeTree splitPoint() throws IOException {
        // todo: return the commit id of the split point
        depth_First_Tra_withoutPrint();
        return sp;
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

    // collect files from the split point (leaves) to root ([current_branch, given_branch])
    public static Stack<String> commitSha = new Stack<String>();

    public static NBtable[] latest_files_BranchToSplit(LimeTreeFamily tree, int a) throws IOException { // cannot change NBtable[], must use return
        // a == 0 => HEAD ; a == 1 => OTHER
        // todo: move from splitPoint to the end, all commit on the left[0](right) is for HEAD_nb_files(OTHER_nb_files)
        LimeTree split = tree.splitPoint();
        LimeTree end_tree = tree.getRoot();
        while(!(split.equals(end_tree))) {
            if (!commitSha.contains(split.PaSha_pair[a]) & !split.PaSha_pair[a].equals(end_tree.PaSha_pair[1-a])) {
                commitSha.push(split.PaSha_pair[a]);
            }
            split = split.parent;
        }
        // add the end_tree
        if(!commitSha.contains(tree.getRoot().PaSha_pair[a])){ //********************** trap
            commitSha.push(tree.getRoot().PaSha_pair[a]);
        }
        NBtable[] nb_files = new NBtable[0];
        // Update the HEAD_nb_files & OTHER_nb_files
        while(!commitSha.isEmpty()){// just add the files hasn't exists, as the top object from the stack is the latest
            String commitSha_head = commitSha.pop();
            nb_files = latest_files_BranchToSplit_helper(commitSha_head, nb_files);
        }
        return nb_files;
    }

    public static NBtable[] my_table = new NBtable[0];

    public static NBtable[] latest_files_BranchToSplit_helper(String commitSha, NBtable[] nb_files) throws IOException {
        //todo: add files
        Commit my_commit = getCommit(commitSha);
        if(my_commit.getNB_commit() != null){
            NBtable[] files_add = my_commit.getNB_commit(); // the older commit
            int n = files_add.length;
            if(n!=0){ // there are files waiting for add
                if(nb_files.length != 0){
                    for(NBtable t : files_add){
                        for(NBtable ta : nb_files){ // if the file name already recorded, then it should be delete from the candidate by name, as the current version is the latest one
                            if(ta.getFile_name().equals(t.getFile_name()) & !ta.find_sha1(t.getSha1_file_name()) & files_add.length!=0){ //*********** trap
                                files_add = rm_NBtable_byName(files_add,t);
                                nb_files = rm_NBtable_byName(nb_files,t);  //***************** trap
                            }
                        }
                    }
                }
                my_table = add_NBtables(nb_files,files_add); // the left files from the candidates need to be added
            }
        }
        return my_table;
    }
}
