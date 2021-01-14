package gitlet;

import java.io.File;
import java.io.IOException;

public class Gitlet {

    // attributes

    public String workingDirectory;
    public Commit commit;
    public Blob blob;
    public BranchManeger branchManeger;

    public Gitlet(String wd){
        //workdirectory
        String workingDirectory = wd;

    }


    public void init() throws IOException {
        // initialize .gitlet folders

        File d = new File(this.workingDirectory,".gitlet");

        if(d.exists()){
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        }else{
            /* creat Gitlet Repository
                - .gitlet
                -- Staging Area
                --- Staged for addition.txt
                --- Staged for removal.
                -- Commits
             */
            d.mkdir();
            File SA= new File(d.getPath(),"Staging Area");
            SA.mkdir();
            File SAA = new File(SA.getPath(),"Staged for addition.txt");
            SAA.createNewFile();
            File SAR = new File(SA.getPath(),"Staged for removal.txt");
            SAR.createNewFile();
            File C = new File(d.getPath(),"Commits");
            C.mkdir();

            // initial commit0
            Commit Commit0 = new Commit();
            // Serialize commit0
            // Complete NBtable class
            // Complete brach manager class
            // initial branch manager

        }
    }


    public void commit(String arg){
        //
    }
}