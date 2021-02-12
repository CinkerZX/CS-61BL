package gitlet;


import java.io.File;
import java.io.FileNotFoundException;

//* Attributes
//   - NBtable[ ]
//        - Name: the name of branches
//        - Hash: SHA1(latest-commit)
//   - head
//        - Hash: NBtable //point out which branch we are in
//* Function
//   - update_head
//   - update_branch
public class BranchManage {
    // attributes
    private NBtable[] branches; // the latest_commit of each branches
    private NBtable branch_head;
    private int a=10; // the initial length of branches

    // Constructor
    public BranchManage (String sha1){
        branches = new NBtable[10];
        branches[0] = new NBtable("master", sha1);
        branch_head = branches[0];
    }

    // Function update_head
    public void update_head(Commit latest_commit){
        branch_head.setSha1_file_name(Utils.sha1(latest_commit));
    }

    //Function update_branches
    public void update_branches(Commit latest_commit){
        for (NBtable i: branches){
            if(i.find_sha1(branch_head.getSha1_file_name())){
                i.setSha1_file_name(Utils.sha1(latest_commit));
            }
        }
        update_head(latest_commit);
    }

    // Generate the new_commit by copying from the current commit
    public Commit new_commit(String Arg){
        Commit n_commit = current_commit();
        n_commit.setPa_sha(Utils.sha1(current_commit()));
        n_commit.setMetadata(Arg);
        return n_commit;
    }

    // Judge if the file exists in the current commit(Yes/No)
    public boolean in_current_commit(String file_name){
        Commit cur_commit = current_commit();
        for (NBtable i : cur_commit.getNB_commit()){
            if(i.find(file_name)){
                return(true);
            }
        }
        return(false);
    }

    // Get the current_commit object
    public Commit current_commit(){ // name => sha1
        File file = new File(".gitlet/Commits", branch_head.getSha1_file_name());
        try{
            Commit cur_commit = Utils.readObject(file, Commit.class); //Commit is a class
            return cur_commit;
        } catch (Exception e){
            System.out.println("Current commit file didn't find");
            throw new RuntimeException(e);
        }
    }

    // Return the sha1 of the current commit
    public String get_cur_commit_sha1(){
        return(branch_head.getSha1_file_name());
    }
}
