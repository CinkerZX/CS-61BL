package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;  //interface : IO functions to operate object and files

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
                --- Staged for addition.txt
                --- Staged for removal.
                -- Commits
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
        // check if it exists in staging for addition

        File fileAdd = new File("./.gitlet/staged for addition", filename)
        File fileW = new File(workingDirectory, filename);

        if(fileW.exists()){
            System.out.println("File does not exist.");
        }else{
            if(fileAdd.exists()){
                fileAdd.createNewFile();
            }
        }

    }

    public void rm(String filename) throws IOException {
        // check if it exists in staging for addition
        // check if it exists in staging for removal
        // if the file exist in working directory
        File fileAdd = new File("./.gitlet/staged for addition", filename);
        File fileRem = new File("./.gitlet/staged for removal", filename);
        File fileW = new File(workingDirectory, filename);

        if(fileAdd.exists()){
            fileAdd.delete();
        }
        if(branchManager.inCurrentCommit(filename)){

            fileRem.createNewFile(); // add to staged for removal

            if(fileW.exists()){
                fileW.delete();  // delete the file from the working directory
                // already been backed up in blobs, less risk of lossing our works
            }
        }
        System.out.println("No reason to remove the file.");
    }


//    public Boolean fileExists(String filepath,String filename){
//        File file = new File(filepath, filename);
//        if(file.exists()){
//            return true;
//        }else{
//            return false;
//        }
//    }

//TO-DO
    public void commit(String arg){
        //  initial blobs from staged for addition/removal



    }

    public void reset(String Commit_id){
        // check if the id exist in current branch
    }
}