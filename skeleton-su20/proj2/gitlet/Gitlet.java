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
import java.util.Arrays;

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
            // check if this blob already exists by it's sha1
            if (!check(Args, ".gitlet/Staging Area/Staged for addition")){ //not exists
                // add the sha1(blob) into Staged for addition
                File f_add = new File(".gitlet/Staging Area/Staged for addition", blob.getBlob_name());
                f_add.createNewFile();
                Utils.writeContents(f_add, blob.getfileName());
            }
            // check if such add operation has already exists in the "staged for addition"
            if (!check(blob.getBlob_name(), ".gitlet/Blobs")) { // not yet
                // add the blob into the content
                blob_add.createNewFile(); // Create the file
                Utils.writeObject(blob_add, blob); // write the blob object as its content
            }
        }
        else{
            System.out.println("File does not exist.");
        }
    }

    // remove a file
    public void rm(String Args) throws IOException {
        File f_workingDirec = new File(working_directory, Args);
        //generate a blob
        Blob blob = new Blob(f_workingDirec);
        //Check if the file exists in gitlet
        if(!check(Args, working_directory)){
            //Check if the file name exists in Staged for addition
            if (check(blob.getBlob_name(), ".gitlet/Staging Area/Staged for addition")) {
                File f_rm = new File(".gitlet/Staging Area/Staged for addition", blob.getBlob_name());
                f_rm.delete();// delete the file from Staged for addition
                //add the sha1(blob) into Staged for removal
                File f_dele = new File(".gitlet/Staging Area/Staged for removal", blob.getBlob_name());
                f_dele.createNewFile();
                Utils.writeContents(f_dele, blob.getfileName()); // write the file name as its content
            }
        }// in this condition, the "added" file hasn't been saved by gitlet, there is no record of Args
        else{ // the file is in the current working directory
            // check if the file is tracked in the current commit
            File d = new File(working_directory,".gitlet");
            File branchMa = new File(d.getPath(), "branch");
            try {
                BranchManage branch = Utils.readObject(branchMa, BranchManage.class);
                if(branch.in_current_commit(Args, working_directory)){
                    //add the sha1(blob) into Staged for removal
                    File f_dele = new File(".gitlet/Staging Area/Staged for removal", blob.getBlob_name());
                    f_dele.createNewFile();
                    Utils.writeContents(f_dele, blob.getfileName()); // write the file name as its content
                    // delete from the working directory
                    f_workingDirec.delete();
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
        if(file_add.list().length == 0 & file_remove.list().length == 0){ // if file_add.list() is empty, [] is not null, here must use the length of the list to judge
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
                Commit new_Commit = branch.new_commit(Args, working_directory);
                // make a list of tracking files combining the info from staging area
                NBtable[] blob_list = new_Commit.getNB_commit();

                File file_blob = new File(working_directory,".gitlet/Blobs"); // we meed to go to the Blobs directory to find the blob

                // staged for addition : append new blobs of "staged for addition" to newCommit's NBtable
                for (String sha_blob : file_add.list()){
                    // find the blob object by its hash name
                    File blob = new File(file_blob, sha_blob);
                    Blob add_blob = Utils.readObject(blob, Blob.class); // read the blob out
                    //generate the new NBtable of this blob
                    NBtable new_blob = new NBtable(add_blob.getfileName(), sha_blob);
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
                    NBtable new_blob = new NBtable(remove_blob.getfileName(), sha_blob);
                    blob_list = ArrayUtils.removeElement(blob_list,new_blob);

                    // clear staging area (clear the file in removal diction)
                    File staging_remove = new File(file_remove, sha_blob);
                    staging_remove.delete();
                }

                // Don't forget to write blobs into the commit
                new_Commit.setNB_commit(blob_list);
                System.out.println("already add the blob"+ blob_list.length);

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
            Commit cur_commit = branch.current_commit(working_directory); // get the current commit
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

    public void globalLog() throws IOException {
        String path = working_directory+"/.gitlet/Commits";
        File commit_fold = new File(path);
        try{
            File[] commits = commit_fold.listFiles();
            for (File commit_file : commits){
                Commit commit_read = Utils.readObject(commit_file, Commit.class);
                System.out.println("===");
                System.out.println("commit" + " " + commit_read.getPa_sha()); // print the sha1
                System.out.println("Date:" + " " + commit_read.getMetadata()[1]);// print the time
                System.out.println(commit_read.getMetadata()[0]);// print the message
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void find(String Args){
        String path = working_directory+"/.gitlet/Commits";
        File commit_fold = new File(path);
        int i = 0;
        try{
            File[] commits = commit_fold.listFiles();
            for (File commit_file : commits){
                Commit commit_read = Utils.readObject(commit_file, Commit.class);
                if (commit_read.getMetadata()[0].equals(Args)){ // == means if these two are the same object; .equals if the two object has the same value
                    System.out.println(Utils.sha1(Utils.serialize(commit_read))); // print the sha1
                    i = i+1;
                }
            }
            if(i == 0){
                System.out.println("Found no commit with that message.");
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void status(){
        //=== Branches ===
        //      *master
        //      other-branch

        //Print out all branches
        System.out.println("=== Branches ===");
        File d = new File(working_directory,".gitlet");
        File branchMa = new File(d.getPath(), "branch");
        try{
            BranchManage branch = Utils.readObject(branchMa, BranchManage.class);
            NBtable[] existing_Branches = branch.getBranches();
            String[] branch_Names = new String[existing_Branches.length];
            int i = 0;
            for (NBtable t : existing_Branches){
                branch_Names[i] = t.getFile_name(); // Print out the name of the branch
                i = i+1;
            }
            //Order the string by lexicographic order
            Arrays.sort(branch_Names);
            for (int j = 0; j < branch_Names.length; j++) {
                if(branch_Names[j].equals(branch.getBranch_head().getFile_name())){
                    branch_Names[j] = '*'+branch_Names[j];
                }
                System.out.println(branch_Names[j]);
            }
        } catch (Exception e){
            System.out.println("*******************");
            throw new RuntimeException(e);
        }

        //Print out staged files
        //Staged for addition
        System.out.println("=== Staged Files ===");
        String path_1 = working_directory+"/.gitlet/Staging Area/Staged for addition";
        File addition = new File(path_1);
        File[] file_addition = addition.listFiles();
        String[] additional_File_Names = new String[file_addition.length];
        NBtable[] nbtable_stage_add = new NBtable[file_addition.length];
        try{
            int i = 0;
            for (File f_addition : file_addition){
                additional_File_Names[i] = Utils.readContentsAsString(f_addition); // Read content needs to use Utils.readContentsAsstring, if use object.toStraing, the file object including its direction will be print out
                nbtable_stage_add[i] = new NBtable(additional_File_Names[i], f_addition.getName());
                i = i+1;
            }
            //Order the string by lexicographic order
            Arrays.sort(additional_File_Names);
            for (int j = 0; j < additional_File_Names.length; j++) {
                System.out.println(additional_File_Names[j]);
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        //Staged for removal
        System.out.println("=== Removed Files ===");
        String path_2 = working_directory+"/.gitlet/Staging Area/Staged for removal";
        File removal = new File(path_2);
        File[] file_removal = removal.listFiles();
        String[] file_removal_File_Names = new String[file_removal.length];
        NBtable[] nbtable_stage_removal = new NBtable[file_removal.length];
        try{
            int i = 0;
            for (File f_addition : file_removal){
                file_removal_File_Names[i] = Utils.readContentsAsString(f_addition); // Read content needs to use Utils.readContentsAsstring, if use object.toStraing, the file object including its direction will be print out
                nbtable_stage_removal[i] = new NBtable(file_removal_File_Names[i], f_addition.getName());
                i = i+1;
            }
            //Order the string by lexicographic order
            Arrays.sort(file_removal_File_Names);
            for (int j = 0; j < file_removal_File_Names.length; j++) {
                System.out.println(file_removal_File_Names[j]);
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }

        //Files in the working direction
        String path = working_directory;
        File file_WorkingDir = new File(path);
        File[] files_Working = file_WorkingDir.listFiles();
        NBtable[] nbtable_working = new NBtable[files_Working.length-1];
        int i = 0;
        for (File file_Working : files_Working) {
            String file_name = file_Working.getName();
            if(!file_name.equals(".gitlet")) {
                String sha_blob = Utils.sha1(file_Working.getName() + Utils.readContentsAsString(file_Working)); // Read as string!!!!
                nbtable_working[i] = new NBtable(file_name, sha_blob);
                i = i+1;
            }
        }

        //Files in the current commit
        BranchManage branch = Utils.readObject(branchMa, BranchManage.class);
        Commit cur_commit = branch.current_commit(working_directory);
        NBtable[] nbtable_cur_commit = cur_commit.getNB_commit();

        // Print out modified
        // names_cur_direction intersection with cur_commit (names_array_1);
        String[] names_array_1 = NBtable.get_string_Array(nbtable_working, nbtable_cur_commit, "name");
        // sha_blob_cur_direction intersection with sha_blob_cur_commit (names_array_2);
        String[] names_array_2 = NBtable.get_string_Array(nbtable_working, nbtable_cur_commit, "bold_hash");
//        System.out.println("names_array_2");
//        for (String a : names_array_2) {
//            System.out.println(a);
//        }

        // Tracked in the current commit, changed in the working directory
        String[] cond_1 = NBtable.get_names_Compliment(names_array_1, names_array_2);

        // but not staged(staged: additional_File_Names + file_removal_File_Names)
        cond_1 = NBtable.get_names_Compliment(cond_1, NBtable.get_String_Array_Union(additional_File_Names,file_removal_File_Names));

        // names_cur_direction intersection with staged_addition (names_array_3);
        String[] names_array_3 = NBtable.get_string_Array(nbtable_working, nbtable_stage_add, "name");
        // sha_blob_cur_direction intersection with staged_addition (names_array_4);
        String[] names_array_4 = NBtable.get_string_Array(nbtable_working, nbtable_stage_add, "bold_hash");
        // Staged for addition, but with different contents than in the working directory
        String[] cond_2 = NBtable.get_names_Compliment(names_array_3, names_array_4);

        // Staged for addition, but deleted in the working directory;
        String[] cond_3 = NBtable.get_names_Compliment(additional_File_Names, NBtable.getFile_name_array(nbtable_working));

        // Not staged for removal, but tracked in the current commit and deleted from the working directory.
        String[] cond_4 = NBtable.get_names_Compliment(NBtable.getFile_name_array(nbtable_cur_commit), NBtable.getFile_name_array(nbtable_working));
        cond_4 = NBtable.get_names_Compliment(cond_4,file_removal_File_Names);

        String[] modified_files = NBtable.get_String_Array_Union(cond_1,cond_2);

        System.out.println("=== Modifications Not Staged For Commit ===");
        for (String s : modified_files) {
            System.out.println(s + " (modified)");
        }

        // Print out delete
        String[] deleted_files = NBtable.get_String_Array_Union(cond_3,cond_4);
        for (String j : deleted_files) {
            System.out.println(j + " (deleted)");
        }

        // Print out untracked Files
        // blobs_working_direc - all_blobs
        // Read all blobs
        String path_blob = working_directory + "/.gitlet/Blobs"; // the slash before .gitley cannot miss
        File file_blob = new File(path_blob);
        File[] all_blobs = file_blob.listFiles();
        NBtable[] nbtable_blobs = new NBtable[all_blobs.length];
        i = 0;
        for (File blob : all_blobs) {
            Blob b = Utils.readObject(blob, Blob.class);
            nbtable_blobs[i] = new NBtable(b.getfileName(),b.getBlob_name());
            i = i+1;
        }

        String[] Untracked = NBtable.get_names_Compliment(nbtable_working,nbtable_blobs); //　NBtable. indicate the method is under which class
        System.out.println("=== Untracked Files ===");
        for (String j : Untracked) {
            System.out.println(j);
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