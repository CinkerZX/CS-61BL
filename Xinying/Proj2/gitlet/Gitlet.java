package gitlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;  //interface : IO functions to operate object and files
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

public class Gitlet implements Serializable {

    // attributes

    public String workingDirectory;
    public BranchManager branchManager; // be available to all functions in gitlet class

    public Gitlet(String wd){
        //workDirectory
        String workingDirectory = wd;
        BranchManager branchManager = null;
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
            File C0 = new File(C.getPath(),Utils.sha1(Commit0));
            C0.createNewFile();
            Utils.writeObject(C0, Commit0);

            // initial branch manager
            this.branchManager = new BranchManager(Utils.sha1(Commit0));

        }
    }

    public void add(String filename) throws IOException {
        // if the file exist in working directory
        // check if the file has already been committed(in blobs folder),
            // if so, there is no need to staging
        // check if it exists in staging for addition (replicate action)


        File fileW = new File(workingDirectory, filename);


        if(fileW.exists()){ // check if the file exists in working directory
            System.out.println("File does not exist.");
        }else{

            Blob addedFile = new Blob(fileW);  // generate a new blob object
            File fileJudge = new File("./.gitlet/Blobs",addedFile.getBlobID());

            if(!fileJudge.exists()){ // check if the file exists in blobs

                File fileAdd = new File("./.gitlet/Staging Area/Staged for addition", addedFile.getBlobID());

                if(!fileAdd.exists()){
                    fileAdd.createNewFile(); // an empty file with the BlobID as the name
                    fileJudge.createNewFile();
                    Utils.writeObject(fileJudge, addedFile);
                }
            }
        }

    }

    public void rm(String filename) throws IOException {
        // check if it exists in staging for addition

        // if the file exist in working directory (how to locate the file ?????????)
            //maybe we can use "absolute path + name" as the "filename" of blobs attributes

        File fileAdd = new File("./.gitlet/Staging Area/Staged for addition", filename);
        File fileW = new File(workingDirectory, filename);//?????????
        Blob removedFile = new Blob(fileW);  // generate a new blob object
        File fileRem = new File("./.gitlet/Staging Area/Staged for removal", removedFile.getBlobID());


        if(fileAdd.exists()){
            fileAdd.delete();
        }
        if(branchManager.inCurrentCommit(filename)){

            fileRem.createNewFile(); // add to staged for removal
            Utils.writeObject(fileRem,removedFile);

            if(fileW.exists()){
                fileW.delete();  // delete the file from the working directory
                // already been backed up in blobs, less risk of lossing our works
            }
        }
        System.out.println("No reason to remove the file.");
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
        File stagingAdd = new File(this.workingDirectory+"/.gitlet/Staging area/Staged for addition");
        File stagingRem = new File(this.workingDirectory+"/.gitlet/Staging area/Staged for removal");
        if(stagingAdd.list() == null & stagingRem.list() == null){
            System.out.println("No changes added to the commit.");
        }
        else if(arg.isEmpty()){
            System.out.println("Please enter a commit message.");
        }
        else{
            Commit newCommit = branchManager.NewCommit(arg);
            NBtable[] newbloblist = newCommit.NBCommit;

            // What will happen if NBCommit is null

            // staged for addition
            for (String name : stagingAdd.list()){
                File file = new File(stagingAdd, name);
                Blob addition = Utils.readObject(file, Blob.class);
                NBtable add = new NBtable(addition.getfilename(), name);
                newbloblist = ArrayUtils.add(newbloblist, add);
                file.delete();
            }
            // staged for removal
            for (String name : stagingRem.list()){
                File file = new File(stagingRem, name);
                Blob removal = Utils.readObject(file, Blob.class);
                NBtable rem = new NBtable(removal.getfilename(), name);
                newbloblist = ArrayUtils.removeElement(newbloblist, rem);
                file.delete();
            }
            newCommit.NBCommit = newbloblist;

            File C = new File(this.workingDirectory+"/.gitlet/Commits",Utils.sha1(newCommit));
            C.createNewFile();
            Utils.writeObject(C, newCommit);

            branchManager.update_branch(Utils.sha1(newCommit));

        }
    }

    //TO-DO
    public void reset(String Commit_id){
        // check if the id exist in current branch
    }

    //TO-DO
    public void log(){

    }
}