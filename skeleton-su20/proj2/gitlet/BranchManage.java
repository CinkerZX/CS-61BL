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
    public void update_head(Commit latest_commit){
        branch_head.setSha1_file_name(Utils.sha1(latest_commit));
    }

    public void update_branches(String newSHA){
        for (NBtable branch : branches){
            if(branch.find_sha1(branch_head.getSha1_file_name())){
                branch.setSha1_file_name(newSHA);
                break;
            }
        }
        branch_head.setSha1_file_name(newSHA);
    }

    public void add_branches(String working_directory, NBtable newBranch){
        File d = new File(working_directory,".gitlet"); // Must read from the content again
        File branchMa = new File(d.getPath(), "branch");
        BranchManage branch = Utils.readObject(branchMa, BranchManage.class);
        // get the name of all the branches
        NBtable[] existing_Branches = branch.getBranches();
        int n = existing_Branches.length + 1;
        NBtable[] branches_new = new NBtable[n];
        int i = 0;
        for(NBtable s : existing_Branches){
            branches_new[i++] = s;
        }
        branches_new[n-1] = newBranch;
        setBranches(branches_new);
    }

    public void rm_branches(String working_directory, String branch_name){
        File d = new File(working_directory,".gitlet"); // Must read from the content again
        File branchMa = new File(d.getPath(), "branch");
        BranchManage branch = Utils.readObject(branchMa, BranchManage.class);
        // get the name of all the branches
        NBtable[] existing_Branches = branch.getBranches();
        int n = existing_Branches.length - 1;
        NBtable[] branches_new = new NBtable[n];
        int i = 0;
        for(NBtable s : existing_Branches){
            if (!s.getFile_name().equals(branch_name)){
                branches_new[i++] = s;
            }
        }
        setBranches(branches_new);
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

    // Get the current_commit object
    public Commit current_commit(String working_directory){ // name => sha1
        File d = new File(working_directory,".gitlet");

        File branchMa = new File(d.getPath(), "branch");
        try{
            BranchManage branch = Utils.readObject(branchMa, BranchManage.class);
            String sha_commit = branch.branch_head.getSha1_file_name();

            File commit_folder = new File(d.getPath(), "Commits");
            File cur_file = new File(commit_folder.getPath(), sha_commit);
            Commit cur_commit = Utils.readObject(cur_file, Commit.class);
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
