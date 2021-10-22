package gitlet;
// init inititalize a repository folder under the directory of the object folder
// status check the tracked files(folder) change
// add create the corresponding blobs; add informations in the staging area
// remove //
// commit // construct the commit, accept the string text (messsage)
// update the pointers
// log

//import com.sun.org.apache.xpath.internal.Arg;
import org.apache.commons.lang3.ArrayUtils;
//import sun.text.normalizer.NormalizerBase;

import java.io.*;
import java.util.Arrays;
import java.util.Stack;

import static gitlet.BranchManage.getCommit;
import static gitlet.LimeTreeFamily.*;
import static gitlet.NBtable.*;
import static gitlet.NBtable.get_simple_String_Intersection;

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
            // check if such add operation has already exists in the "staged for addition"
            if (!check(Args, ".gitlet/Staging Area/Staged for addition")){ //not exists
                // add the sha1(blob) into Staged for addition
                File f_add = new File(".gitlet/Staging Area/Staged for addition", blob.getBlob_name());
                f_add.createNewFile();
                Utils.writeContents(f_add, blob.getfileName());
            }
            // check if this blob already exists by it's sha1
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
                    Utils.writeContents(f_dele, blob.getfileName());
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
    public void commit(String Args, String pa_sha_1, String pa_sha_2) throws IOException {
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
                Commit cur_Commit = branch.current_commit(working_directory);
                // generate a new commit object (newCommit) by coping NBtable from "current commit"
                Commit new_Commit = branch.new_commit(Args, working_directory);
                if (!pa_sha_1.isEmpty() & !getCommit(pa_sha_1).getMetadata()[0].equals("initial commit")){
                    new_Commit.addPa_sha(pa_sha_1, pa_sha_2);
                }
                // make a list of tracking files combining the info from staging area
                NBtable[] blob_list = cur_Commit.getNB_commit();

                File file_blob = new File(working_directory,".gitlet/Blobs"); // we meed to go to the Blobs directory to find the blob

                // staged for addition : append new blobs of "staged for addition" to newCommit's NBtable
                for (String sha_blob : file_add.list()){
                    int add_check = 0; // -------------trap need to be initialized in each for loop, or new generated file cannot be added
                    // find the blob object by its hash name
                    File blob = new File(file_blob, sha_blob);
                    Blob add_blob = Utils.readObject(blob, Blob.class); // read the blob out
                    //generate the new NBtable of this blob
                    NBtable new_blob = new NBtable(add_blob.getfileName(), sha_blob);
                    if (blob_list==null){
                        blob_list = ArrayUtils.add(blob_list,new_blob); // add
                    }
                    else{
                        //Compare the blobs in add_blob with in blob_list, if the file name is different, then add, else, remove the blob from blob_list first, add
                        for (NBtable t : blob_list){
                            if (add_blob.getfileName().equals(t.getFile_name())){
                                add_check = 1; // have the same name, update(remove first, then add)
                                blob_list = rm_NBtable_byName(blob_list,t); // ------------------- trap, ArrayUtils.removeElement doesn't work
                                blob_list = add_NBtable(blob_list, new_blob);
                            }
                        }
                        if (add_check == 0){ // is a new generated file, add into the blob_list directly
                            blob_list = add_NBtable(blob_list,new_blob); // add
                        }
                    }
                    // clear staging area (clear the file in addition diction)
                    File staging_add = new File(file_add, sha_blob); // delete the file 'sha_blob' in "Staging Area\Staged for addition"
                    staging_add.delete();
                }

                // staged for removal : remove blobs of "staged for removal" from newCommit's NBtable
                //System.out.println(file_remove.list().length);
                for (String sha_blob : file_remove.list()){
                    // find the blob object by its hash name
                    File blob = new File(file_blob, sha_blob);
                    Blob remove_blob = Utils.readObject(blob, Blob.class); // read the blob out
                    //generate the new NBtable of this blob
                    NBtable new_blob = new NBtable(remove_blob.getfileName(), sha_blob);
                    blob_list = rm_NBtable_byName(blob_list,new_blob);
                    // clear staging area (clear the file in removal diction)
                    File staging_remove = new File(file_remove, sha_blob);
                    staging_remove.delete();
                }
                // Don't forget to write blobs into the commit
                new_Commit.setNB_commit(blob_list);
                // write commit object
                write_commit(new_Commit);

                // Update branches and head
                NBtable new_head = new NBtable(branch.getBranch_head().getFile_name(), Utils.sha1(Utils.serialize(new_Commit)));
                branch.update_head(new_head);
                branch.update_branches(new_head);      // ********************************* trap
                branch.wt(working_directory,branch); // write the branch management object
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        }
    }

    // log
    public void log() throws IOException {
        File d = new File(working_directory,".gitlet");
        File branchMa = new File(d.getPath(), "branch");
        try{
            BranchManage branch = Utils.readObject(branchMa, BranchManage.class); //BranchManage is a class
            Commit cur_commit = branch.current_commit(working_directory); // get the current commit
            print_commit(cur_commit, branch);

            while(!cur_commit.getPa_sha()[0].isEmpty()){ // pa_sha = "";   .isEmpty() <=> "" != null)<=> null
                cur_commit = cur_commit.pa_commit(cur_commit.getPa_sha()[0]);
                print_commit(cur_commit, branch);
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
                System.out.println("commit" + " " + commit_read.getPa_sha()[0]); // print the sha1
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
            System.out.println(); // change into another new line
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
            // Print out in order
            printInOrder(additional_File_Names, "");
            System.out.println(); // change into another new line
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
            // Print out in order
            printInOrder(file_removal_File_Names, "");
            System.out.println(); // change into another new line
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        //Files in the working direction (WD)
        File[] files_Working = get_all_files(working_directory);
        NBtable[] nbtable_working = new NBtable[files_Working.length-1];
        int i = 0;
        for (File file_Working : files_Working) {
            String file_name = file_Working.getName();
            if(!file_name.equals(".gitlet") & !file_name.equals("runner.py") & !file_name.equals("python.exe")) {
                String sha_blob = Utils.sha1(file_Working.getName() + Utils.readContentsAsString(file_Working)); // Read as string!!!!
                nbtable_working[i] = new NBtable(file_name, sha_blob);
                i = i+1;
            }
        }

        //Files in the current commit (CC)
        BranchManage branch = Utils.readObject(branchMa, BranchManage.class);
        Commit cur_commit = branch.current_commit(working_directory);
        NBtable[] nbtable_cur_commit = cur_commit.getNB_commit();

        // Print out modified:  WD intersect with (CC+ADD) by name - WD intersect with (CC+ADD) by hash
        String[] CC_ADD = NBtable.get_String_Array_Union(NBtable.getFile_name_array(nbtable_cur_commit), additional_File_Names);
        String[] WD_inter_CC_ADD = NBtable.get_simple_String_Intersection(NBtable.getFile_name_array(nbtable_working),CC_ADD);

        String[] WD_inter_CC_HASH = NBtable.get_string_Array(nbtable_working,nbtable_cur_commit,"hash");
        String[] WD_inter_ADD_HASH = NBtable.get_string_Array(nbtable_working,nbtable_stage_add,"hash");
        String[] WD_inter_CC_ADD_HASH = NBtable.get_String_Array_Union(WD_inter_CC_HASH,WD_inter_ADD_HASH);
        String[] modified_files = NBtable.get_names_Compliment(WD_inter_CC_ADD,WD_inter_CC_ADD_HASH);
        // WD intersect with CC by name INTERACT WD intersect with CC by hash \ ADD (hash)    changed in working directory, commit, then change back in working directory without stage in addition, still counted as modified
        String[] WD_inter_CC = NBtable.get_simple_String_Intersection(NBtable.getFile_name_array(nbtable_working),NBtable.getFile_name_array(nbtable_cur_commit)); //by name
        String[] WD_inter_CC_name_inter_hash = NBtable.get_simple_String_Intersection(WD_inter_CC, WD_inter_CC_HASH);
        String[] object_set = NBtable.get_names_Compliment(WD_inter_CC_name_inter_hash, WD_inter_ADD_HASH);

        System.out.println("=== Modifications Not Staged For Commit ===");
        // Print out in order
        printInOrder(NBtable.get_String_Array_Union(modified_files,object_set), " (modified)");

        // Print out delete: CC + ADD - WD - Rem by name
        String[] deleted_files = NBtable.get_names_Compliment(CC_ADD, NBtable.getFile_name_array(nbtable_working));
        deleted_files = NBtable.get_names_Compliment(deleted_files, file_removal_File_Names);
        // Print out in order
        printInOrder(deleted_files, " (deleted)");
        System.out.println(); // change into another new line

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

        String[] Untracked = NBtable.get_names_Compliment(nbtable_working,nbtable_blobs); //NBtable indicate the method is under which class
        System.out.println("=== Untracked Files ===");
        for (String j : Untracked) {
            System.out.println(j);
        }
    }

    public void printInOrder(String[] array, String args){
        Arrays.sort(array);
        for (String s : array) {
            System.out.println(s + args);
        }
    }

    // new a pointer
    public void branch(String Args) throws IOException {
        // Check if the name of the branch has already exists: get the list of all the branch names
        File d = new File(working_directory,".gitlet");
        File branchMa = new File(d.getPath(), "branch");
        try{
            BranchManage branch = Utils.readObject(branchMa, BranchManage.class);
            // get the name of all the branches
            NBtable[] existing_Branches = branch.getBranches();
            int i = 0;
            for (NBtable t : existing_Branches){
                if (Args.equals(t.getFile_name())){
                    System.out.println("A branch with that name already exists.");
                    i = 1; // if the name already exists, break
                    break;
                }
            }
            if (i == 0){ // the name doesn't exist, just generate a new pointer with this name
                NBtable new_branch_head = new NBtable(Args, "");
                branch.add_branches(working_directory, new_branch_head);
                branch.wt(working_directory, branch); // write the branch management object
                NBtable b = getBranch(Args);
                if (b.getSha1_file_name().equals("")){
                    NBtable new_head = new NBtable(Args, branch.getBranch_head().getSha1_file_name());
                    branch.update_branches(new_head);
                    branch.wt(working_directory, branch); // write the branch management object
                }
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    // delete a pointer
    public void rm_branch(String Args) throws IOException {
        // Check if the name of the branch has already exists: get the list of all the branch names
        File d = new File(working_directory,".gitlet");
        File branchMa = new File(d.getPath(), "branch");
        try{
            BranchManage branch = Utils.readObject(branchMa, BranchManage.class);
            // get the name of all the branches
            NBtable[] existing_Branches = branch.getBranches();
            String[] branch_Names = new String[existing_Branches.length];
            int i = 0;
            for (NBtable t : existing_Branches){
                if (Args.equals(t.getFile_name())){
                    i = 1;
                    if(Args.equals(branch.get_cur_branch())){ // remove the branch you’re currently on, aborts
                        System.out.println("Cannot remove the current branch");
                        break;
                    }
                    else{ // need to remove the branch
                        branch.rm_branches(working_directory, Args);
                        branch.wt(working_directory, branch); // write the branch management object
                    }
                }
            }
            if (i == 0){
                System.out.println("A branch with that name does not exist.");
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    //  Checks out all the files tracked by the given commit.
    public void checkout_by_commit_id(Commit Object_commit, Commit Cur_commit) throws IOException {
        // get the NBtable list of files of branches by branch name
        NBtable[] object_nb_files = Object_commit.getNB_commit();
        NBtable[] cur_nb_files = Cur_commit.getNB_commit();
        // name of the files in object_nb_files but untraccted by cur_nb_files
        String[] prepare_add_files = NBtable.get_names_Compliment(object_nb_files, cur_nb_files);

        //The blob of files in the working direction (nbtable_working)
        String path = working_directory;
        File file_WorkingDir = new File(path);
        File[] files_Working = file_WorkingDir.listFiles();
        NBtable[] nbtable_working = new NBtable[files_Working.length-1]; // if not test by python, need -3
        int i = 0;
        String[] file_name_WD = new String[files_Working.length-1]; // if not test by python, need -3
        for (File file_Working : files_Working) {
            String file_name = file_Working.getName();
            file_name_WD[i] = file_name;
            if(!file_name.equals(".gitlet") & !file_name.equals("runner.py") & !file_name.equals("python.exe")) {
                String sha_blob = Utils.sha1(file_Working.getName() + Utils.readContentsAsString(file_Working)); // Read as string!!!!
                nbtable_working[i] = new NBtable(file_name, sha_blob);
                i = i+1;
            }
        }

        // Intersection of file names in working direction & prepare_add_files
        String[] check_blob_file_name = NBtable.get_simple_String_Intersection(file_name_WD,prepare_add_files);
        // write the files in object_nb_files into the direction
        for(NBtable file_add : object_nb_files) {
            // if the file is untracked in the current branch and would be overwritten by the checkout
            // if the name of this file is in "check_blob_file_name"
            if (Arrays.asList(check_blob_file_name).contains(file_add.getFile_name())){
                NBtable blob_WD = myTable(nbtable_working,file_add.getFile_name());
                if (file_add.getSha1_file_name().equals(blob_WD.getSha1_file_name())){
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                }
            }
            else{
                writeFile(file_add);
            }
        }
        // get the name of files in cur_nb_files \ object_nb_files
        String[] delete_file_names = NBtable.get_names_Compliment(cur_nb_files, object_nb_files);
        // delete these files by name
        for(String file_delete : delete_file_names) {
            deleteFile(file_delete);
        }
    }

    // checkout branchname
    public void checkoutBranch(String Args) throws IOException {
        // get the head with the branch name Args
        NBtable object_branch = getBranch(Args);  //NBtable: branch name | latest commit sha
        Commit object_commit = getCommit(object_branch.getSha1_file_name());

        BranchManage cur_branch = getCurrentBranch();
        Commit cur_commit = getCommit(cur_branch.get_cur_commit_sha1());

        // if new branch doesn't have any commit
        if(object_commit.getPa_sha()[0].equals("")){
            File[] files_Working = get_all_files(working_directory);
            for (File file_Working : files_Working) {
                String file_name = file_Working.getName();
                if(!file_name.equals(".gitlet") & !file_name.equals("runner.py") & !file_name.equals("python.exe")) {
//                    System.out.println(file_name);
                    deleteFile(file_name);
                }
            }
        }
        else {
            checkout_by_commit_id(object_commit, cur_commit);
        }
        cur_branch.update_head(object_branch); //update head
        cur_branch.wt(working_directory, cur_branch);
        // Clean the staging area
        clean_staging();
    }

    // checkout file name
    public void checkoutFile(String file_name) throws IOException {
        BranchManage cur_branch = getCurrentBranch();
        String cur_commit_sha = cur_branch.get_cur_commit_sha1();
        checkoutCommitFilename(cur_commit_sha, file_name);
    }

    // checkout commit_id file_name
    public void checkoutCommitFilename(String commit_id, String file_name)throws IOException {
        Commit cur_commit = getCommit(commit_id);
        if (cur_commit.getPa_sha()[0].equals("")){
            System.out.println("No commit with that id exists.");
        }
        else{
            // Read the files in this commit
            NBtable[] nb_files = cur_commit.getNB_commit();
            // check if the file_name exists in the commit of the head of current branch
            int exist = 0;
            for (NBtable file : nb_files) {
                if (file.getFile_name().equals(file_name)) {
                    exist = 1;
                    writeFile(file);
                }
            }
            if (exist == 0) {
                System.out.println("File does not exist in that commit.");
            }
        }
    }

    // reset [commit id]
    public void reset(String commit_id) throws IOException {
        Commit object_commit = getCommit(commit_id);
        if (object_commit.getPa_sha()[0].equals("")){ // no such commit_id
            System.out.println("No commit with that id exists.");
        }
        else{
            //  Checks out all the files tracked by the given commit.
            BranchManage cur_branch = getCurrentBranch();
            Commit cur_commit = getCommit(cur_branch.get_cur_commit_sha1());
            checkout_by_commit_id(object_commit, cur_commit);
            //  Moves the current branch’s head to that commit node.
            NBtable new_head = new NBtable(cur_branch.getBranch_head().getFile_name(), commit_id);
            cur_branch.update_branches(new_head);
            cur_branch.wt(working_directory, cur_branch);
            // clean the staging area
            clean_staging();
        }
    }

    // merge [branch name]
    public void merge(String branch_name) throws IOException {
        NBtable[] OTHER_nb_files = new NBtable[0];// a == 0 => HEAD ; a == 1 => OTHER
        NBtable[] HEAD_nb_files = new NBtable[0];
        //false case 1: If there are staged additions or removals present, print the error message, and exist
        if (staging_empty()) {
            System.out.println("You have uncommitted changes.");
            return;
        }
        //false case 2: If a branch with the given name does not exist, print the error message, and exist
        else {
            NBtable object_branch = getBranch(branch_name);
            Commit object_commit = getCommit(object_branch.getSha1_file_name());
            if (object_commit.getPa_sha()[0].equals("")) {
                System.out.println("A branch with that name does not exist.");
                return;
            }
            // false case 3: If attempting to merge a branch with itself, print the error message
            else {
                BranchManage cur_branch = getCurrentBranch();
                if (cur_branch.getBranch_head().equals(branch_name)) {
                    System.out.println("Cannot merge a branch with itself.");
                    return;
                } else {
                    // get the split_commit
                    try {
                        NBtable[] branches = cur_branch.getBranches();
                        String sha_moved = cur_branch.get_cur_commit_sha1(); // sha1 of *master
                        Commit HEAD = getCommit(sha_moved);// latest commit of the current branch
                        String sha_remain = myTable(branches, branch_name).getSha1_file_name();// sha1 of xinxin
                        Commit OTHER = getCommit(sha_remain); // latest commit of the merged branch

                        // Check if the splitPoint function
                        Commit split_commit = getSplit_Point(sha_moved,sha_remain);
                        if(split_commit.getMetadata()[0].equals(OTHER.getMetadata()[0])) {
                            System.out.println("Given branch is an ancestor of the current branch.");
                            return; // end up the entire function
                        }
                        if(split_commit.getMetadata()[0].equals(HEAD.getMetadata()[0])){
                            checkoutBranch(branch_name);
                            System.out.println("Current branch fast-forwarded");
                            return;
                        }

                        // case(2) Files added in OTHER
                        HEAD_nb_files = getCommit(sha_moved).getNB_commit();
                        OTHER_nb_files = getCommit(sha_remain).getNB_commit();

                        NBtable[] SPLIT_nb_files = split_commit.getNB_commit();
                        String[] add_filenames = NBtable.get_names_Compliment(OTHER_nb_files, SPLIT_nb_files);
                        if(!(add_filenames == null)){ //**********************trap
                            for (String s : add_filenames){
                                checkoutNBtableArrFilename(OTHER_nb_files, s); // sha_remain := sha1 of OTHER
                            }
                        }

                        // case (3) Files removed from OTHER and not changed in HEAD
                        String[] rm_filenames = NBtable.get_names_Compliment(SPLIT_nb_files, OTHER_nb_files);
                        if(!(rm_filenames == null)){
                            for (String s : rm_filenames){
                                if (files_sameContent(s,HEAD_nb_files,SPLIT_nb_files)){
                                    // if the same, delete the file from working directory
                                    deleteFile(s);
                                }
                            }
                        }

                        // SPLIT_nb_files intersect OTHER_nb_files intersect HEAD_nb_files_new
                        String[] set_SOH_Name = NBtable.get_simple_String_Intersection(NBtable.getFile_name_array(SPLIT_nb_files),NBtable.getFile_name_array(OTHER_nb_files));
                        set_SOH_Name = NBtable.get_simple_String_Intersection(set_SOH_Name,NBtable.getFile_name_array(HEAD_nb_files));
                        String[] files_HO_sameCon = NBtable.get_string_Array(HEAD_nb_files, OTHER_nb_files,"");
                        String[] files_HO_diffCon = NBtable.get_names_Compliment(set_SOH_Name, files_HO_sameCon);

                        // case(1) Files modified in OTHER but not in HEAD
                        String[] files_SH_sameCon = NBtable.get_string_Array(SPLIT_nb_files, HEAD_nb_files,"");
                        String[] update_files = NBtable.get_simple_String_Intersection(files_SH_sameCon, files_HO_diffCon);
                        String[] HEAD_files_name = getFile_name_array(HEAD_nb_files);
                        String[] update_files_2 = get_names_Compliment(update_files, HEAD_files_name);
                        if(!(update_files_2 == null)) {
                            for (String s : update_files_2){
                                checkoutNBtableArrFilename(OTHER_nb_files, s); // sha_remain := sha1 of OTHER
                            }
                        }

                        // case(4) files have different content in head and other but in split point.
                        String[] SPLIT_files_name = getFile_name_array(SPLIT_nb_files);
                        String[] files_SO_diffCon = get_simple_String_Intersection(files_HO_diffCon, SPLIT_files_name);
                        if(!(files_SO_diffCon == null)){
                            for (String s : files_SO_diffCon){
                                new_file(s, HEAD_nb_files, OTHER_nb_files);
                            }
                        }

                        // case(5) files have different content in head and other but absent in split point
                        String[] addInHO_filenames_diffCon = NBtable.get_names_Compliment(files_HO_diffCon, SPLIT_files_name);
                        if(!(addInHO_filenames_diffCon == null)){
                            for (String s : addInHO_filenames_diffCon){
                                new_file(s, HEAD_nb_files, OTHER_nb_files);
                            }
                        }

                        //commit
                        String commit_m = "Merged "+ branch_name + " into " + cur_branch.getBranch_head().getFile_name()+".";
                        commit(commit_m, sha_moved, sha_remain);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Encountered a merge conflict.");
                    }
                }
            }
        }
    }

    // rebase [branch name]
    public void rebase(String branch_name) throws IOException {
        //TODO rebase: when files are conflict, their version in "current commit" is base
        //TODO test:
        // Init
        // SP: ABCD
        // Current:  -1-  !A B C    -2-  !A !B C   -RPLAYED  -3'-   !A !B  -4'-  !A !B
        // Given:    -3-  A !B !D   -4-  ?B !C ?D
        NBtable[] OTHER_nb_files = new NBtable[0];// a == 0 => HEAD ; a == 1 => OTHER
        NBtable[] HEAD_nb_files = new NBtable[0];
        //false case 1: If a branch with the given name does not exist, print the error message, and exist
        NBtable object_branch = getBranch(branch_name);
        Commit object_commit = getCommit(object_branch.getSha1_file_name());
        if (object_commit.getPa_sha()[0].equals("")) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        // false case 2: If the given branch name is the same as the current branch name, print the error message
        else {
            BranchManage cur_branch = getCurrentBranch();
            if (cur_branch.getBranch_head().equals(branch_name)) {
                System.out.println("Cannot rebase a branch onto itself.");
                return;
            } else {
                // get the split_commit
                try {
                    NBtable[] branches = cur_branch.getBranches();
                    String sha_moved = cur_branch.get_cur_commit_sha1(); // sha1 of *master (current branch)
                    Commit HEAD = getCommit(sha_moved);// latest commit of the current branch
                    String sha_remain = myTable(branches, branch_name).getSha1_file_name();// sha1 of xinxin (given branch)
                    Commit OTHER = getCommit(sha_remain); // latest commit of the merged branch (given branch)

                    // If the input branch’s head is in the history of the current branch’s head
                    Commit split_commit = getSplit_Point(sha_moved,sha_remain);
                    if(split_commit.getMetadata()[0].equals(OTHER.getMetadata()[0])) {
                        System.out.println("Already up-to-date.");
                        return; // end up the entire function
                    }
                    // Special case: only need to change the pointer of the latest commit of the current branch to that of given branch.
                    if(split_commit.getMetadata()[0].equals(HEAD.getMetadata()[0])){
                        cur_branch.update_head(object_branch);
                        NBtable branchName_latestComSha = new NBtable(branch_name, Utils.sha1(Utils.serialize(OTHER)));
                        cur_branch.update_branches(branchName_latestComSha);
                        cur_branch.wt(working_directory, cur_branch);
                        return;
                    }
                    // Copy the commits from the given branch by stack
                    Stack<Commit> fringe = new Stack<Commit>();
                    Commit pushin = OTHER; // The latest commit of given branch
                    fringe.push(pushin); // Push it into fringe
                    while(!pushin.getPa_sha()[0].equals(Utils.sha1(Utils.serialize(split_commit)))){ // push in, until meet split point
                        pushin = getCommit(pushin.getPa_sha()[0]);
                        fringe.push(pushin);
                    }
                    Commit commit_0_given = fringe.pop();

                    // For files didn't change in current branch, but changed in the given branch, should copy the changing history
                    HEAD_nb_files = getCommit(sha_moved).getNB_commit();  // current branch
                    OTHER_nb_files = commit_0_given.getNB_commit(); // given branch

                    NBtable[] SPLIT_nb_files = split_commit.getNB_commit(); //spilt point
                    String[] replace_CurBase;
                    replace_CurBase = files_pending_replace(HEAD_nb_files, SPLIT_nb_files, OTHER_nb_files);
                    String[] delete_files;
                    delete_files = files_pending_detele(HEAD_nb_files, SPLIT_nb_files, OTHER_nb_files);
                    String pa_sha = update_commit(replace_CurBase, delete_files, commit_0_given, HEAD,sha_moved);
                    Commit commit_given;
                    HEAD = getCommit(pa_sha);
                    HEAD_nb_files = HEAD.getNB_commit();
                    while(!fringe.isEmpty()){
                        commit_given = fringe.pop();
                        OTHER_nb_files = commit_given.getNB_commit();
                        replace_CurBase = files_pending_replace(HEAD_nb_files, SPLIT_nb_files, OTHER_nb_files);
                        delete_files = files_pending_detele(HEAD_nb_files, SPLIT_nb_files, OTHER_nb_files);
                        pa_sha = update_commit(replace_CurBase, delete_files, commit_given, HEAD, pa_sha);
                        HEAD = getCommit(pa_sha); //********************trap
                        HEAD_nb_files = HEAD.getNB_commit();;
                    }
                    //Remove the old branch
                    BranchManage.rm_branches(working_directory,branch_name);
//                    cur_branch.wt(working_directory,cur_branch); //***********************trap
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Encountered a merge conflict.");
                }
            }
        }
    }

    // Helping function
    // get current branch
    public BranchManage getCurrentBranch(){
        File d = new File(working_directory,".gitlet");
        File branchMa = new File(d.getPath(), "branch");
        BranchManage branch = Utils.readObject(branchMa, BranchManage.class);
        return(branch);
    }

    // get the branch by branch name
    public NBtable getBranch(String Args){
        File d = new File(working_directory,".gitlet");
        File branchMa = new File(d.getPath(), "branch");
        BranchManage branch = Utils.readObject(branchMa, BranchManage.class);
        NBtable[] branches = branch.getBranches();
        for(NBtable aim_branch: branches) {
            if (aim_branch.getFile_name().equals(Args)) {
                return(aim_branch);
            }
        }
        NBtable branch0 = new NBtable();
        System.out.println("No such branch exists.");
        return(branch0);
    }

    // get the commit by commit_sha
    public Commit getCommit(String sha)throws IOException {
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

    // get the blob by bolb_sha
    public static Blob getBlob(String sha){
        File blob = new File("./.gitlet/Blobs", sha);
        Blob myBlob = Utils.readObject(blob, Blob.class);
        return(myBlob);
    }

    // write a file into working directory by NBtable (blob)
    public void writeFile(NBtable file_table) throws IOException {
        //Get the sha(blob) of this file, find the blob by sha name
        Blob blob = getBlob(file_table.getSha1_file_name());
        // put it in the working directory
        File file_overWriting = new File("./", blob.getfileName());
        if (file_overWriting.exists()) {
            file_overWriting.delete(); // if file already exists, delete it
        }
        file_overWriting.createNewFile();
        Utils.writeContents(file_overWriting, blob.getContent()); // write the file name as its content
    }

    // delete a file from the working directory by file name
    public void deleteFile(String f_name) throws IOException {
        File file_delete = new File(working_directory, f_name);
        if (file_delete.exists()) {
            file_delete.delete(); // if file already exists, delete it
        }
    }

    // get the NBtable from a NBtable[] by file name
    public NBtable myTable(NBtable[] NB_table_list, String file_name){
        for (NBtable table : NB_table_list){
            if (table.getFile_name().equals(file_name)){
                return(table);
            }
        }
        return(null); //**********************trap
    }

    // Clean the staging area
    public void clean_staging() throws IOException {
        File[] files_add = get_all_files(".gitlet/Staging Area/Staged for addition");
        for (File f_add : files_add) {
            String file_name = f_add.getName();
            deleteFile(file_name);
        }
        File[] files_rm = get_all_files(".gitlet/Staging Area/Staged for removal");
        for (File f_rm : files_rm) {
            String file_name = f_rm.getName();
            deleteFile(file_name);
        }
    }

    // Check if there are files in staging
    public boolean staging_empty(){
        File[] files_add = get_all_files(".gitlet/Staging Area/Staged for addition");
        File[] files_rm = get_all_files(".gitlet/Staging Area/Staged for removal");
        if(files_add == null & files_rm == null){
            return(true);
        }
        else return(false);
    }

    // get all the files from certain directory
    public File[] get_all_files(String direc){
        String path = direc;
        File file_WorkingDir = new File(path);
        File[] files_Working = file_WorkingDir.listFiles();
        return(files_Working);
    }

    // judge if two files with the same name have the same content in two NB_table array
    public boolean files_sameContent(String filename, NBtable[] NBtableArr_1, NBtable[] NBtableArr_2){
        if(!(myTable(NBtableArr_1,filename) == null)){ //**********************trap
            if (myTable(NBtableArr_1,filename).getSha1_file_name().equals(myTable(NBtableArr_2,filename).getSha1_file_name()))
            {
                return(true);
            }else{
                return(false);
            }
        }else {
            return (false);
        }
    }

    // checkout NBtable[] file_name
    public void checkoutNBtableArrFilename(NBtable[] NBtableArr, String file_name)throws IOException {
        for (NBtable file : NBtableArr) {
            if (file.getFile_name().equals(file_name)) {
                writeFile(file);
                // stage
                File f_workingDirec = new File(working_directory, file_name);
                Blob blob = new Blob(f_workingDirec);
                File blob_add = new File(".gitlet/Blobs", blob.getBlob_name());
                if (!check(file_name, ".gitlet/Staging Area/Staged for addition")) { //not exists
                    // add the sha1(blob) into Staged for addition
                    File f_add = new File(".gitlet/Staging Area/Staged for addition", blob.getBlob_name());
                    f_add.createNewFile();
                    Utils.writeContents(f_add, blob.getfileName());
                }
            }
        }
    }

    // Create a new file to compare the difference
    public void new_file(String file_name, NBtable[] NBtableArr_1, NBtable[] NBtableArr_2) throws IOException {
        NBtable table_1 = myTable(NBtableArr_1,file_name);
        NBtable table_2 = myTable(NBtableArr_2,file_name);
        //Get the sha(blob) of this file, find the blob by sha name
        Blob blob_1 = getBlob(table_1.getSha1_file_name());
        Blob blob_2 = getBlob(table_2.getSha1_file_name());

        // put it in the working directory
        File file_overWriting = new File("./",file_name);
        if (file_overWriting.exists()) {
            file_overWriting.delete(); // if file already exists, delete it
        }
        file_overWriting.createNewFile();
        String newContent =  "<<<<<<< HEAD" + "\r\n" + blob_1.getContent() + "\r\n" + "=======" + "\r\n" + blob_2.getContent() + "\r\n" + ">>>>>>>";
        Utils.writeContents(file_overWriting, newContent); // write the file name as its content

        // add this new file
        add(file_name); // generate blob, staged
    }

    // Get split point
    public Commit getSplit_Point(String cur_commitSha, String aim_commitSha) throws IOException {
        // Construct LimeTreeFamily
        LimeTreeFamily my_tree = new LimeTreeFamily(cur_commitSha, aim_commitSha);
        // Fulfill the tree
        my_tree.addChild();
        // Check if the splitPoint function
        Commit split_commit = getCommit(my_tree.splitPoint().PaSha_pair[0]);
        return split_commit;
    }

    // print commit
    public void print_commit(Commit cur_commit, BranchManage branch){
        if(cur_commit.getPa_sha().length == 1){ // *************** trap if use getPa_sha()[1] will have unexpected pointer error, thus need to use length to judge
            System.out.println("===");
            System.out.println("commit" + " " + branch.get_cur_commit_sha1()); // print the sha1
            System.out.println("Date:" + " " + cur_commit.getMetadata()[1]);// print the time
            System.out.println(cur_commit.getMetadata()[0]);// print the message
            System.out.println();
        }
        else {
            System.out.println("===");
            System.out.println("commit" + " " + branch.get_cur_commit_sha1()); // print the sha1
            System.out.println("Merge: " + cur_commit.getPa_sha()[0].substring(0, Math.min(cur_commit.getPa_sha()[0].length(), 7)) + " " + cur_commit.getPa_sha()[1].substring(0, Math.min(cur_commit.getPa_sha()[0].length(), 7)));
            System.out.println("Date:" + " " + cur_commit.getMetadata()[1]);// print the time
            System.out.println(cur_commit.getMetadata()[0]);// print the message
            System.out.println();
        }
    }

    // generate the deleted and replace files
    public String [] files_pending_replace(NBtable[] HEAD_files, NBtable[] SPLIT_files, NBtable[] OTHER_files){
        String[] replace_CurBase;
        if(SPLIT_files != null){
            String[] files_HS_sameCon = NBtable.get_string_Array(HEAD_files,SPLIT_files,"");  //C
            String[] files_HO_sameName = NBtable.get_string_Array(HEAD_files,OTHER_files,"name");  //B C
            replace_CurBase = NBtable.get_names_Compliment(files_HO_sameName,files_HS_sameCon);  //same name, different content, B
        }
        else{// If split point is the initial point
            replace_CurBase = NBtable.get_string_Array(HEAD_files,OTHER_files,"name");
        }
        return replace_CurBase;
    }

    // generate the deleted and replace files
    public String [] files_pending_detele(NBtable[] HEAD_files, NBtable[] SPLIT_files, NBtable[] OTHER_files){
        String[] files_H = getFile_name_array(HEAD_files);
        String[] delete_files = new String[0];
        if(SPLIT_files != null){
            String[] files_HS_sameCon = NBtable.get_string_Array(HEAD_files,SPLIT_files,"");
            String[] files_HS_sameName = NBtable.get_string_Array(HEAD_files,SPLIT_files,"name");
            String[] files_HS_difCon = NBtable.get_names_Compliment(files_HS_sameName,files_HS_sameCon);
            String[] delete_CurBase = NBtable.get_names_Compliment(SPLIT_files, HEAD_files); // A
            String[] delete_OthfromSP = NBtable.get_names_Compliment(SPLIT_files, OTHER_files); // deleted from Other
            delete_OthfromSP = get_simple_String_Intersection(delete_OthfromSP, files_H);// deleted from Other but exists in H
            delete_OthfromSP = NBtable.get_names_Compliment(delete_OthfromSP, files_HS_difCon); //*********************trap
            delete_files = get_String_Array_Union(delete_CurBase, delete_OthfromSP);
        }
        return delete_files;
    }

    // blob replace, with the base of cur, replace the file with same name in given
    public NBtable[] replace_blob(String filename, Commit cur, NBtable[] blobList_given){
        NBtable[] blobList_Cur = cur.getNB_commit();
        for(NBtable t : blobList_Cur){
            if(t.find(filename)){  //******************trap
                for (NBtable tt : blobList_given){
                    if(tt.find(filename)){
                        tt.setSha1_file_name(t.getSha1_file_name());
                    }
                }
            }
        }
        return blobList_given;
    }

    // blob delete
    public NBtable[] delete_blob(String filename, NBtable[] blobList_given){
        int n = blobList_given.length;
        for(NBtable s : blobList_given){ //move by sha
            if (s.getFile_name().equals(filename)){
                n = n-1;
            }
        }
        NBtable[] blobList_new = new NBtable[n];
        int i = 0;
        for(NBtable s : blobList_given){ //move by sha
            if (!s.getFile_name().equals(filename)){
                blobList_new[i++] = s;
            }
        }
        return(blobList_new);
    }

    // write commit into file space
    public void write_commit(Commit commit_new) throws IOException {
        // write commit object
        File commit_add = new File(".gitlet/Commits", Utils.sha1(Utils.serialize(commit_new)));
        commit_add.createNewFile();
        Utils.writeObject(commit_add, commit_new); // write the commit object in file commit_add whose name is 'Utils.sha1(new_Commit)'
    }

    // update commit and return pasha
    public String update_commit(String[] file_names, String[] del_files, Commit give, Commit head, String pasha) throws IOException {
        NBtable[] updated_blobList = head.getNB_commit(); // head is the base **************************trap
        if(del_files.length!=0){ // delete the files
            for (String s : del_files){
                updated_blobList = delete_blob(s,updated_blobList);
            }
        }
        if(file_names != null){
            for (String s : file_names){
                updated_blobList = replace_blob(s, head, updated_blobList);
            }
        }
        // copy the commit
        Commit commit_new = new Commit(pasha, give.getMetadata()[0], updated_blobList);
        write_commit(commit_new);
        // Update branches  ****************************trap
        BranchManage cur_branch = getCurrentBranch();
        NBtable new_branch_head = new NBtable(cur_branch.getBranch_head().getFile_name(), Utils.sha1(Utils.serialize(commit_new)));
        cur_branch.update_branches(new_branch_head);
        cur_branch.wt(working_directory,cur_branch);
        return(Utils.sha1(Utils.serialize(commit_new)));
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