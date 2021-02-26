package gitlet;
// init inititalize a repository folder under the directory of the object folder
// status check the tracked files(folder) change
// add create the corresponding blobs; add informations in the staging area
// remove //
// commit // construct the commit, accept the string text (messsage)
// update the pointers
// log

import org.apache.commons.lang3.ArrayUtils;

import java.io.File; // for creating file/folder
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable; // for output(writing) files

public class Gitlet implements Serializable{ // class is abstract // tell java this class is Serializable

    // Attributes
    private String working_directory;
    private BranchManage branchManage; // be available to all other functions

    //Constructor
    public Gitlet(String Direct){ // when new an object, we use this method
        working_directory = Direct;
        //branchManage = null;
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
            d_2_1.mkdir(); // Create new folder
            //Create the new file of "Staged for removal"
            File d_2_2 = new File(d_2.getPath(), "Staged for removal");
            d_2_2.mkdir();

            //Create the folder of ".Commits"
            File d_3 = new File(d.getPath(), "Commits");
            d_3.mkdir();

            Commit commit_0 = new Commit(); // Generate the commit_0 object
            //Create the file "commit_0" with name "sha1(commit_0)"
            File commit0 = new File(d_3.getPath(), Utils.sha1(Utils.serialize(commit_0)));
            // write the object commit_0 into the direction
            Utils.writeObject(commit0, commit_0);

            branchManage = new BranchManage(Utils.sha1(Utils.serialize(commit_0)));
            branchManage.wt(working_directory, branchManage); // write the branch management object
            //Create the folder of ".Blobs"
            File d_4 = new File(d.getPath(), "Blobs");
            d_4.mkdir();
        }
    }

    // add a file
    public void add(String Args) throws IOException {
        //Check if the file name exists in working directory
        if (check(Args, working_directory)) {
            File f_workingDirec = new File(working_directory, Args);
            //generate a blob
            Blob blob = new Blob(f_workingDirec);
            File blob_add = new File(".gitlet/Blobs", blob.getBlob_name());
            // judge if this blob already exists by it's sha1
            if (!check(blob.getBlob_name(), ".gitlet/Blobs")) { //not exists
                // if such add operation has already exists in the "staged for addition"
                if (!check(Args, ".gitlet/Staging Area/Staged for addition")) { // not yet
                    // add the sha1(blob) into Staged for addition
                    File f_add = new File(".gitlet/Staging Area/Staged for addition", blob.getBlob_name());
                    f_add.createNewFile();
                    // add the blob into the content
                    blob_add.createNewFile(); // Create the file
                    Utils.writeObject(blob_add, blob); // write the blob object as its content
                }
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
            File d = new File(working_directory,".gitlet");
            File branchMa = new File(d.getPath(), "branch");
            try {
                BranchManage branch = Utils.readObject(branchMa, BranchManage.class);
                if(branch.in_current_commit(Args)){
                    File de_file = new File(working_directory, Args);
                    //generate the blob of the file
                    Blob blob = new Blob(de_file);
                    //add the sha1(blob) into Staged for removal
                    File f_dele = new File(".gitlet/Staging Area/Staged for removal", blob.getBlob_name());
                    f_dele.createNewFile();
                    Utils.writeObject(f_dele, blob); // write the blob object as its content
                    // delete from the working directory
                    de_file.delete();
                } // in this condition, the history of adding file Args has already been saved in gitlet, now it's safe to delete it from the working directory
                else{
                    System.out.println("No reason to remove the file.");
                }
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        }
    }

    // check if the file exists in a certain directory
    public boolean check(String file_name, String dick){
        File file_add = new File(dick, file_name);
        if(file_add.exists()){
            return(true);
        }
        else{
            return(false);
        }
    }

    // construct the commit object
    public void commit(String Args) throws IOException {
        // check if the staging area is empty  &&  args checking  (failure cases
        File file_add = new File(working_directory,".gitlet/Staging Area/Staged for addition");
        File file_remove = new File(working_directory,".gitlet/Staging Area/Staged for removal");
        if(file_add.list() == null & file_remove.list() == null){
            System.out.println("No changes added to the commit");
        }
        else if(Args.isEmpty()){ // E is upper case
            System.out.println("Please enter a commit message");
        }
        else{
            // Read the existing branch
            File d = new File(working_directory,".gitlet");
            File branchMa = new File(d.getPath(), "branch");
            try{
                BranchManage branch = Utils.readObject(branchMa, BranchManage.class);
                // generate a new commit object (newCommit) by coping NBtable from "current commit"
                Commit new_Commit = branch.new_commit(Args);
                // make a list of tracking files combining the info from staging area
                NBtable[] blob_list = new_Commit.getNB_commit();

                File file_blob = new File(working_directory,".gitlet/Blobs"); // we meed to go to the Blobs directory to find the blob

                // staged for addition : append new blobs of "staged for addition" to newCommit's NBtable
                for (String sha_blob : file_add.list()){
                    // find the blob object by its hash name
                    File blob = new File(file_blob, sha_blob);
                    Blob add_blob = Utils.readObject(blob, Blob.class); // read the blob out
                    //generate the new NBtable of this blob
                    NBtable new_blob = new NBtable(add_blob.getBlob_name(), sha_blob);
                    blob_list = ArrayUtils.add(blob_list,new_blob);

                    // clear staging area (clear the file in addition diction)
                    File staging_add = new File(file_add, sha_blob); // delete the file 'sha_blob' in "Staging Area\Staged for addition"
                    staging_add.delete();
                }

                // staged for removal : remove blobs of "staged for removal" from newCommit's NBtable
                for (String sha_blob : file_remove.list()){
                    // find the blob object by its hash name
                    File blob = new File(file_blob, sha_blob);
                    Blob remove_blob = Utils.readObject(blob, Blob.class); // read the blob out
                    //generate the new NBtable of this blob
                    NBtable new_blob = new NBtable(remove_blob.getBlob_name(), sha_blob);
                    blob_list = ArrayUtils.removeElement(blob_list,new_blob);

                    // clear staging area (clear the file in removal diction)
                    File staging_remove = new File(file_remove, sha_blob);
                    staging_remove.delete();
                }

                // write commit object
                File commit_add = new File(".gitlet/Commits", Utils.sha1(Utils.serialize(new_Commit)));
                commit_add.createNewFile();
                Utils.writeObject(commit_add, new_Commit); // write the commit object in file commit_add whose name is 'Utils.sha1(new_Commit)'

                // Update branches and update_head
                branch.update_branches(Utils.sha1(Utils.serialize(new_Commit)));
                branch.wt(working_directory,branch); // write the branch management object
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        }
    }

    // update the head of the branch: reset [commit id]
    public void reset (String commit_id){ // need to check if the commit_id exists in this branch
        //check all the sha1(commits) in this branch

    }

    // log
    public void log() throws IOException {
        File d = new File(working_directory,".gitlet");
        File branchMa = new File(d.getPath(), "branch");
        try{
            BranchManage branch = Utils.readObject(branchMa, BranchManage.class); //BranchManage is a class
            Commit cur_commit = branch.current_commit(); // get the current commit
            System.out.println("===");
            System.out.println("commit" + " " + branch.get_cur_commit_sha1()); // print the sha1
            System.out.println("Date:" + " " + cur_commit.getMetadata()[1]);// print the time
            System.out.println(cur_commit.getMetadata()[0]);// print the message

            while(!cur_commit.getPa_sha().isEmpty()){ // pa_sha = "";   .isEmpty() <=> "" != null)<=> null
                System.out.println("===");
                System.out.println("commit" + " " + cur_commit.getPa_sha()); // print the sha1
                cur_commit = cur_commit.pa_commit(cur_commit.getPa_sha());
                System.out.println("Date:" + " " + cur_commit.getMetadata()[1]);// print the time
                System.out.println(cur_commit.getMetadata()[0]);// print the message
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}

class MyFilenameFilter implements FilenameFilter {

    String initials;

    // constructor to initialize object
    public MyFilenameFilter(String initials)
    {
        this.initials = initials;
    }

    // overriding the accept method of FilenameFilter
    // interface
    public boolean accept(File dir, String name)
    {
        return name.startsWith(initials);
    }
}