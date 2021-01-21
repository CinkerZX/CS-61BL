package gitlet;


import java.io.File;

//* Attributes
//   - NBtable[ ]
//        - Name: the name of braches
//        - Hash: SHA1(latest-commit)
//   - head
//        - Hash: NBtable //point out which branch we are in
//* Function
//   - update_head
//   - update_branch
public class BranchManage {
    // attributes
    private NBtable[] branches;
    private NBtable branch_head;

    // Constructor
    public BranchManage (String sha1){
       branches[0] = new NBtable("master", sha1);
       branch_head = branches[0];
    }

    // Function update_head

    // Function current_commit
    public boolean in_current_commit(String file_name){
        Commit cur_commit = current_commit(".gitlet/Commits", branch_head.getSha1_file_name());
        for (NBtable i : cur_commit.getNB_commit()){
            if(i.find(file_name)){
                return(true);
            }
        }
        return(false);
    }

    public Commit current_commit(String dick, String name){
        File file = new File(dick, name);
        if(file.exists()){
            Commit cur_commit = Utils.readObject(file, Commit.class); //Commit is a class
        }
        return(cur_commit);
    }
}
