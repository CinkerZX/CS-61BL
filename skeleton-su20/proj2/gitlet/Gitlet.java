package gitlet;
// init inititalize a repository folder under the directory of the object folder
// status check the tracked files(folder) change
// add create the corresponding blobs; add informations in the staging area
// remove //
// commit // construct the commit, accept the string text (messsage)
// update the pointers
// log

import java.io.File; // for creating file/folder
import java.io.IOException;
import java.io.Serializable; // for output(writing) files

public class Gitlet implements Serializable{ // class is abstract // tell java this class is Serializable

    // Attributes
    private String working_directory;
    private BranchManage branchManage; // be available to all other functions

    //Constructor
    public Gitlet(String Direct){ // when new an object, we use this method
        working_directory = Direct;
        branchManage = null;
    }

    public void init() throws IOException {
        File d = new File(working_directory,".gitlet"); // File(Parents' path, child folder/file name)
        if (d.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        }
        else {
            //Create the folder of ".gitlet"
            d.mkdir(); // Create a new folder

            //Create the folder of ".Staging Area"
            File d_2 = new File(d.getPath(), "Staging Area");
            d_2.mkdir();
            //Create the new file of "Staged for addition"
            File d_2_1 = new File(d_2.getPath(), "Staged for addition");
            d_2_1.createNewFile(); // Create a new file
            //Create the new file of "Staged for removal"
            File d_2_2 = new File(d_2.getPath(), "Staged for removal");
            d_2_2.createNewFile();

            //Create the folder of ".Commits"
            File d_3 = new File(d.getPath(), "Commits");
            d_3.mkdir();

            Commit commit_0 = new Commit(); // Generate the commit_0 object
            //Create the file "commit_0" with name "sha1(commit_0)"
            File commit0 = new File(d_3.getPath(), Utils.sha1(commit_0));
            // write the object commit_0 into the direction
            Utils.writeObject(commit0, commit_0);

            branchManage = new BranchManage(Utils.sha1(commit_0));
            //Create the folder of ".Blobs"
            File d_4 = new File(d.getPath(), "Blobs");
            d_4.mkdir();
        }
    }

    // add a file
    public void add(String Args) throws IOException {
        //Check if the file name exists in working directory
        if (check(Args, working_directory)){
            if (!check(Args, ".gitlet/Staging Area/Staged for addition")) {
                File f_add = new File(".gitlet/Staging Area/Staged for addition",Args);
                f_add.createNewFile(); // add the file into Staged for addition
            }
        }
        else{
            System.out.println("File does not exist.");
        }
    }

    // remove a file
    public void rm(String Args) throws IOException {
        //Check if the file name exists in Staged for addition
        if (check(Args, ".gitlet/Staging Area/Staged for addition")) {
            File f_rm = new File(".gitlet/Staging Area/Staged for addition", Args);
            f_rm.delete();// delete the file from Staged for addition
        } // in this condition, the "added" file hasn't been saved by gitlet, there is no record of Args
        else{
            // check if the file is tracked in the current commit
            if(branchManage.in_current_commit(Args)){
                File de_file = new File(working_directory, Args);
                de_file.delete(); // delete from the working directory
                File rm_stage_file = new File(".gitlet/Staging Area/Staged for removal", Args);
                rm_stage_file.createNewFile(); // adding this operation into the staged for removal
            } // in this condition, the history of adding file Args has already been saved in gitlet, now it's safe to delete it from the working directory
            else{
                System.out.println("No reason to remove the file.");
            }
        }
    }

    // check if the file exists in a certain directory
    public boolean check(String file_name, String dick){
        File file = new File(dick,file_name);
        if(file.exists()){
            return(true);
        }
        else{
            return(false);
        }
    }


    // construct the commit object
    public void commit(String... Args){
        // input the parameter, the constructor can be selected automatically

        // if the sha1(name + content) equals to existing blobs
        // new blob

    }

    // update the head of the branch: reset [commit id]
    public void reset (String commit_id){ // need to check if the commit_id exists in this branch
        //check all the sha1(commits) in this branch

    }


}
