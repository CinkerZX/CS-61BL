package gitlet;


import javax.swing.text.AbstractDocument;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

//* Attributes
//   - NBtable[ ]
//        - Name: the name of branches
//        - Hash: SHA1(latest-commit)
//   - head
//        - Hash: NBtable //point out which branch we are in
//* Function
//   - update_head
//   - update_branch
public class BranchManage implements Serializable {
    private static final long serialVersionUID = 3511688074759412198L;
    // attributes
    private NBtable[] branches; // the latest_commit of each branches
    private NBtable branch_head;

    // Constructor
    public BranchManage (String sha1){
        // branches = new NBtable[10];
        branches = new NBtable[]{new NBtable()};
        NBtable head_obj = new NBtable("master", sha1);
        branches[0] = head_obj;
        branch_head = head_obj;
    }

    // Function update_head

    public void update_branches(NBtable new_t){
        int i = 0;
        for (NBtable t : branches){
            if(t.getFile_name().equals(new_t.getFile_name())){
                break;
            }
            else i+=1;
        }
        branches[i] = new_t;
    }

    public void update_head(NBtable new_head){
        branch_head = new_head;
    }

    public void add_branches(String working_directory, NBtable newBranch){
        File d = new File(working_directory,".gitlet"); // Must read from the content again
        File branchMa = new File(d.getPath(), "branch");
        BranchManage branch = Utils.readObject(branchMa, BranchManage.class);
        // get the name of all the branches
        NBtable[] existing_Branches = branch.getBranches();
        NBtable[] branches_new = NBtable.add_NBtable(existing_Branches, newBranch);
        setBranches(branches_new);
    }

    public static void rm_branches(String working_directory, String branch_name){
        File d = new File(working_directory,".gitlet"); // Must read from the content again
        File branchMa = new File(d.getPath(), "branch");
        BranchManage branch = Utils.readObject(branchMa, BranchManage.class);
        // get the name of all the branches
        NBtable[] existing_Branches = branch.getBranches();
        NBtable re_table = new NBtable(branch_name, "");
        NBtable[] branches_new = NBtable.rm_NBtable_byName(existing_Branches, re_table);
        branch.setBranches(branches_new);
    }

    public void setBranches(NBtable[] new_branches){
        branches = new_branches;
    }

    // Generate the new_commit by copying from the current commit
    public Commit new_commit(String Arg, String work_dir){
        Commit n_commit = current_commit(work_dir);
        n_commit.setPa_sha(Utils.sha1(Utils.serialize(n_commit))); // need to serialize the object
        n_commit.setMetadata(Arg);
        return n_commit;
    }

    // Judge if the file exists in the current commit(Yes/No)
    public boolean in_current_commit(String file_name, String work_dir){
        Commit cur_commit = current_commit(work_dir);
        for (NBtable i : cur_commit.getNB_commit()){
            if(i.find(file_name)){
                return(true);
            }
        }
        return(false);
    }

    // get the commit by commit_sha
    public static Commit getCommit(String sha)throws IOException {
        File commit = new File("./.gitlet/Commits",sha);
        //File commit = new File(d.getPath(),sha);
        Commit commit1 = new Commit();
        commit1.setPa_sha("");
        try {
            Commit myCommit = Utils.readObject(commit, Commit.class);
            return(myCommit);
        } catch (Exception e) {
            return (commit1);
        }
    }

    // Get the current_commit object
    public static Commit current_commit(String working_directory){ // name => sha1
        File d = new File(working_directory,".gitlet");
        File branchMa = new File(d.getPath(), "branch");
        try{
            BranchManage branch = Utils.readObject(branchMa, BranchManage.class);
            return (getCommit(branch.get_cur_commit_sha1()));
        } catch (Exception e){
            System.out.println("Current commit file didn't find");
            throw new RuntimeException(e);
        }
    }

    // Return the sha1 of the current commit
    public String get_cur_commit_sha1(){
        return(branch_head.getSha1_file_name());
    }

    // Return the name of the current branch
    public String get_cur_branch(){
        return(branch_head.getFile_name());
    }

    // Write the branchmanage
    public void wt(String work_direct, BranchManage branch) throws IOException {
        File d = new File(work_direct,".gitlet");
        File branchMa = new File(d.getPath(), "branch");
        if(branchMa.exists()){
            branchMa.delete(); // if file already exists, delete it
        }
        branchMa.createNewFile(); // create the file
        Utils.writeObject(branchMa, branch); // since branchManagement contains NB table, when write, the NBtable need "implements Serializable"
    }

    // Read the branchmanage
    public static BranchManage rd(String work_direct) throws IOException {
        File d = new File(work_direct,".gitlet");
        File branchMa = new File(d.getPath(), "branch");
        try{
            BranchManage branch = Utils.readObject(branchMa, BranchManage.class); //BranchManage is a class
            System.out.println("98");
            return branch;
        } catch (Exception e){
            System.out.println("No branch found");
            throw new RuntimeException(e);
        }
    }

    public NBtable[] getBranches(){
        return branches;
    }

    public NBtable getBranch_head(){
        return branch_head;
    }
}
