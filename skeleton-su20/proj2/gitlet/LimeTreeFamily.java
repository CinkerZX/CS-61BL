package gitlet;


import java.util.ArrayList;

public class LimeTreeFamily {
    private LimeTree root = null;

    public LimeTreeFamily(String Sha_Moved, String Sha_Remained){
        root = new LimeTree(Sha_Moved, Sha_Remained);
    }

    public void addChild(String Sha_Moved_chil, String Sha_Remained_chil, String Sha_Moved_pa, String Sha_Remained_pa){
        if (root != null){
            root.addChildHelper(Sha_Moved_chil, Sha_Remained_chil, Sha_Moved_pa, Sha_Remained_pa);
        }
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

        // Adds child
        public void addChildHelper(String Sha_Moved_chil, String Sha_Remained_chil, String Sha_Moved_pa, String Sha_Remained_pa) {
            if (PaSha_pair.equals(new String[]{Sha_Moved_pa, Sha_Remained_pa})) {
                LimeTree child = new LimeTree(Sha_Moved_chil, Sha_Remained_chil, this);
                children.add(child);
            } else {
                for (LimeTree t : children) {
                    t.addChildHelper(Sha_Moved_chil, Sha_Remained_chil, Sha_Moved_pa, Sha_Remained_pa);
                }
            }
        }
    }




}

