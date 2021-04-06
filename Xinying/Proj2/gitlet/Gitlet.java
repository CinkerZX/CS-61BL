package gitlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;  //interface : IO functions to operate object and files
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

public class Gitlet implements Serializable {

    // attributes

    public String workingDirectory;
    public BranchManager branchManager; // be available to all functions in gitlet class

    public Gitlet(String wd){
        //workDirectory
        String workingDirectory = wd;

        ////!!!!!!!!!!!!!!!  how to handle BM IO and


        BranchManager branchManager = new BranchManager();
    }

    public static BranchManager readBM(String workingDirectory) throws FileNotFoundException {
        File fileG = new File(workingDirectory,".gitlet");
        File fileBM = new File(fileG,"BrancheManager");
        try{
            BranchManager branch = Utils.readObject(fileBM, BranchManager.class);
            return branch;
        }catch(Exception e){
            throw new FileNotFoundException();
        }
    }

    public void init() throws IOException {
        // initialize .gitlet folders

        File d = new File(this.workingDirectory,".gitlet");

        if(d.exists()){
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        }else{
            /* create Gitlet Repository
                - .gitlet
                    -- Staging Area
                        --- Staged for addition
                        --- Staged for removal
                    -- Commits
                    -- Blobs

             */
            d.mkdir();
            File SA= new File(d.getPath(),"Staging Area");
            SA.mkdir();
            File SAA = new File(SA.getPath(),"Staged for addition");
            SAA.mkdir();
            //SAA.createNewFile();
            File SAR = new File(SA.getPath(),"Staged for removal");
            SAR.mkdir();
            //SAR.createNewFile();
            File C = new File(d.getPath(),"Commits");
            C.mkdir();
            File B = new File(d.getPath(),"Blobs");
            B.mkdir();

            // initial commit0
            Commit Commit0 = new Commit();
            File C0 = new File(C.getPath(),Utils.sha1(Utils.serialize(Commit0)));
            C0.createNewFile();
            Utils.writeObject(C0, Commit0);

            // initial branch manager
            this.branchManager = new BranchManager(Utils.sha1(Utils.serialize(Commit0)));
            branchManager.writeBM(this.workingDirectory,branchManager);
        }
    }

    public void add(String workingDirectory, String filename) throws IOException {
        // if the file exist in working directory
        // check if the file has already been committed(in blobs folder),
            // if so, there is no need to staging
        // check if it exists in staging for addition (replicate action)

        File fileW = new File(workingDirectory, filename);

        if(!fileW.exists()){ // check if the file exists in working directory
            System.out.println("File does not exist.");
        }else{
            Blob addedFile = new Blob(fileW);  // generate a new blob object
            File fileJudge = new File("./.gitlet/Blobs",addedFile.getBlobID());

            if(!fileJudge.exists()){ // check if the file exists in blobs
                fileJudge.createNewFile();
                Utils.writeObject(fileJudge, addedFile);
            }

            File fileAdd = new File("./.gitlet/Staging Area/Staged for addition", addedFile.getBlobID());
            if(!fileAdd.exists()){
                fileAdd.createNewFile(); // an empty file with the BlobID as the name
                Utils.writeContents(fileAdd,filename);
            }
        }
    }

    public void rm(String workingDirectory, String filename) throws IOException {
        // check if it exists in staging for addition

        // if the file exist in working directory (how to locate the file ?????????)
            //maybe we can use "absolute path + name" as the "filename" of blobs attributes

        File fileW = new File(workingDirectory, filename);//?????????
        String SHA_name = Utils.sha1(fileW.getName()+Utils.readContentsAsString(fileW));

        File fileAdd = new File("./.gitlet/Staging Area/Staged for addition", SHA_name);

        Blob removedFile = new Blob(fileW);  // generate a new blob object

        // blob 存储

        File fileRem = new File("./.gitlet/Staging Area/Staged for removal", removedFile.getBlobID());
        File fileBM = new File(workingDirectory+"/.gitlet","BrancheManager");

        BranchManager branch = Utils.readObject(fileBM, BranchManager.class);


        if(fileAdd.exists()){
            fileAdd.delete();
        }else if(branch.inCurrentCommit(filename)){

            fileRem.createNewFile(); // add to staged for removal
            //Utils.writeObject(fileRem,removedFile);
            Utils.writeContents(fileRem,filename);

            if(fileW.exists()){
                fileW.delete();  // delete the file from the working directory
                // already been backed up in blobs, less risk of lossing our works
            }
        }else{ System.out.println("No reason to remove the file.");}
    }

    public void commit(String arg) throws IOException {
        // useage : java gitlet.Main commit [message]

        // check if the staging area is empty  &&  args checking  (failure cases
        // generate a new commit object (newCommit) by coping NBtable from "current commit"
        // make a list of tracking files combining the info from staging area
            // staged for addition : append new blobs of "staged for addition" to newCommit's NBtable
            // staged for removal : remove blobs of "staged for removal" from newCommit's NBtable
            // clear staging area
        // write commit object
        // update branches and head
        File fileG = new File(workingDirectory,".gitlet");
        File fileS = new File(fileG,"Staging Area");
        File fileB = new File(fileG,"Blobs");
        File fileC = new File(fileG,"Commits");

        File stagingAdd = new File(fileS,"Staged for addition");
        File stagingRem = new File(fileS,"Staged for removal");

        File fileBM = new File(fileG,"BrancheManager");
        try{
            BranchManager branch = Utils.readObject(fileBM, BranchManager.class);
            if(stagingAdd.list().length == 0 && stagingRem.list().length == 0){    // stagingAdd.list() == null 无效  因为list() returns list object, it couldn't be null it's []
                System.out.println("No changes added to the commit.");
            }
            else if(arg.isEmpty()){
                System.out.println("Please enter a commit message.");
            }
            else {
                Commit newCommit = branch.NewCommit(arg);
                NBtable[] newbloblist = newCommit.NBCommit;

                // What will happen if NBCommit is null

                // staged for addition
                for (String name : stagingAdd.list()) {
                    File file = new File(fileB, name);  // use the filename stored in staging area locate blob in Blobs directory
                    File file1 = new File(stagingAdd,name);  // but delete the file stored in staging area instead of Blobs directory
                    Blob addition = Utils.readObject(file, Blob.class);
                    NBtable add = new NBtable(addition.getfilename(), name);
                    newbloblist = ArrayUtils.add(newbloblist, add);
                    file1.delete();
                }
                // staged for removal
                for (String name : stagingRem.list()) {
                    File file = new File(fileB, name);
                    File file2 = new File(stagingRem, name);
                    Blob removal = Utils.readObject(file, Blob.class);
                    NBtable rem = new NBtable(removal.getfilename(), name);
                    newbloblist = ArrayUtils.removeElement(newbloblist, rem);
                    file2.delete();
                }

                newCommit.NBCommit = newbloblist;

                File C = new File(fileC, Utils.sha1(Utils.serialize(newCommit)));
                C.createNewFile();
                Utils.writeObject(C, newCommit);

                branch.update_branch(Utils.sha1(Utils.serialize(newCommit)));
                branch.writeBM(this.workingDirectory, branch);
            }
        }catch(Exception e1){
            throw new RuntimeException(e1);
        }
    }


    //TO-DO
    public void reset(String Commit_id) throws IOException {
        // check if the id exist in current branch

    }


    public void log() throws IOException {

        File fileG = new File(workingDirectory,".gitlet");
        File fileBM = new File(fileG.getPath(),"BrancheManager");
        try{
            BranchManager branch = Utils.readObject(fileBM, BranchManager.class);
            ///TO-DO merged node of two branches

            Commit CurrentCommit = branch.FindCommit(branch.head.getSHA1Value());
            PrintCommit(CurrentCommit,branch.head.getSHA1Value());
            //while(!cur_commit.getPa_sha().isEmpty()){ // pa_sha = "";
            while(CurrentCommit.getPaSHA() != null){  // Commit0.paSHA = null
                Commit parentCommit = branch.ParentCommit(CurrentCommit);
                PrintCommit(parentCommit,CurrentCommit.getPaSHA());
                CurrentCommit = parentCommit;
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }


    }

    public void global_log(String workingDirectory){
        File fileC = new File(workingDirectory+"/.gitlet/Commits");
        for (File file1 : fileC.listFiles()){
            Commit commit = Utils.readObject(file1,Commit.class);
            PrintCommit(commit, Utils.sha1(Utils.serialize(commit)));
        }
    }

    public void PrintCommit(Commit commit,String Sha){
        System.out.println("===");
        System.out.println("commit "+Sha);
        System.out.println("Date: "+ commit.Metadata[1]);
        System.out.println(commit.Metadata[0]);
    }

    public void find(String message){
        File fileG = new File(workingDirectory,".gitlet");
        File fileC = new File(fileG.getPath(),"Commits");
        Boolean someCommits = false;
        for (String name : fileC.list()) {
            File file1 = new File(fileC,name);  // but delete the file stored in staging area instead of Blobs directory
            Commit commit = Utils.readObject(file1,Commit.class);
            if(commit.Metadata[0].equals(message)){
                System.out.println(Utils.sha1(Utils.serialize(commit)));
                someCommits = true;
            }
        }
        if (someCommits == false){
            System.out.println("Found no commit with that message.");
        }
    }

    //TO-DO
    public void status(String workingDirectory){
        File WD = new File(workingDirectory);
        File fileG = new File(workingDirectory,".gitlet");
        File fileBM = new File(fileG,"BrancheManager");
        BranchManager branchManager = Utils.readObject(fileBM, BranchManager.class);
        File fileS = new File(fileG,"Staging Area");
        File fileB = new File(fileG,"Blobs");
        File fileC = new File(fileG,"Commits");
        File stagingAdd = new File(fileS,"Staged for addition");
        File stagingRem = new File(fileS,"Staged for removal");
        int Addlength = stagingAdd.list().length;
        int Remlength = stagingRem.list().length;

        NBtable[] nbCC =  branchManager.FindCommit(branchManager.head.getSHA1Value()).NBCommit; // NBtables(real file name,blobID) in current commit
        NBtable[] nbAdd = new NBtable[Addlength]; // NBtables(real file name,blobID) in staged for addition
        NBtable[] nbRem = new NBtable[Remlength]; // NBtables(real file name,blobID) in staged for removal
        //NBtables(real file name,blobID) in workingdirectory
        NBtable[] nbWD = new NBtable[WD.list().length-1];


        System.out.println("=== Branches ===");
        if(true){
            String[] branches = new String[branchManager.branches.length];
            String CurrentBranch = branchManager.head.getFullName();
            for(int i=0;i < branchManager.branches.length;i++){
                branches[i] = branchManager.branches[i].getFullName();
            }
            ////lexicographic order
            Arrays.sort(branches);
            //print
            for(String branch : branches){
                if(branch.equals(CurrentBranch)){
                    System.out.println("*"+CurrentBranch);
                }else{
                System.out.println(branch);
                }
            }
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        if(true){
            if(Addlength != 0){
                String[] additions = new String[Addlength];
                File[] additionFile = stagingAdd.listFiles();
                for(int i = 0; i < Addlength;i++){
                    additions[i] = Utils.readContentsAsString(additionFile[i]);
                    nbAdd[i].setSHA1Value(additionFile[i].getName());
                    nbAdd[i].setFullName(additions[i]);
                }
                ////lexicographic order
                Arrays.sort(additions);
                //print
                for(String add : additions){
                        System.out.println(add);
                }
            }
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        if(true){
            if(Remlength != 0){
                String[] removals = new String[Remlength];
                File[] removalFile = stagingRem.listFiles();
                for(int i = 0; i < Remlength;i++){
                    removals[i] = Utils.readContentsAsString(removalFile[i]);
                    nbRem[i].setSHA1Value(removalFile[i].getName());
                    nbRem[i].setFullName(removals[i]);
                }
                ////lexicographic order
                Arrays.sort(removals);
                //print
                for(String rem : removals){
                    System.out.println(rem);
                }
            }
        }
        System.out.println();

        int i = 0;
        for(File file : WD.listFiles()){
            if(!file.equals(fileG)){
                nbWD[i].setFullName(file.getName());
                nbWD[i].setSHA1Value(Utils.sha1(file.getName(),Utils.readContentsAsString(file)));
                i++;
            }
        }
        if(i == WD.list().length-2){
            System.out.println("=== Modifications Not Staged For Commit ===");
            if(true){
                //Tracked in the current commit, changed in the working directory, but not staged;
                //Staged for addition, but with different contents than in the working directory;
                //Staged for addition, but deleted in the working directory;
                //Not staged for removal, but tracked in the current commit and deleted from the working directory.
///////TO-DO
            }
            System.out.println();

            System.out.println("=== Untracked Files ===");
            System.out.println();
        }else{
            System.out.println("Warning! Don't operate on working directory while status");
        }
    }



    //TO-DO
    public void branch(){
    }

    //TO-DO
    public void rm_branch(){
    }

}