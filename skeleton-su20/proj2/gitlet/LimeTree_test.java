package gitlet;

import java.io.IOException;

public class LimeTree_test {
    public static void main(String... args) throws IOException {
        LimeTreeFamily my_tree = new LimeTreeFamily("0", "3");
        LimeTreeFamily.LimeTree tree_1 = new LimeTreeFamily.LimeTree("1","3", my_tree.getRoot());
        LimeTreeFamily.LimeTree tree_2 = new LimeTreeFamily.LimeTree("2","0", my_tree.getRoot());
        my_tree.expandLimeTree(my_tree.getRoot(), tree_1);
        my_tree.expandLimeTree(my_tree.getRoot(), tree_2);
        LimeTreeFamily.LimeTree tree_3 = new LimeTreeFamily.LimeTree("2","3", my_tree.getRoot());
        my_tree.expandLimeTree(tree_1, tree_3);
        LimeTreeFamily.LimeTree tree_4 = new LimeTreeFamily.LimeTree("2","2", my_tree.getRoot());
        my_tree.expandLimeTree(tree_3, tree_4);
        LimeTreeFamily.LimeTree tree_5 = new LimeTreeFamily.LimeTree("1","2", my_tree.getRoot());
        my_tree.expandLimeTree(tree_2, tree_5);
        LimeTreeFamily.LimeTree tree_6 = new LimeTreeFamily.LimeTree("2","2", my_tree.getRoot());
        my_tree.expandLimeTree(tree_5, tree_6);

        my_tree.depth_First_Tra();
    }
}
