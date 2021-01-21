package gitlet;

import java.io.File;

public class BranchManager {
    public NBtable[] branches;
    public NBtable head;

    public BranchManager(String sha1Commit0){
        NBtable master = new NBtable("master", sha1Commit0);
        branches[0] = master;
        head = master;
    }

    public void update_head(){

    }


    public Commit CurrentCommit(String filename){

        File file = new File("./.gitlet/Commits",head.getSHA1Value());
        if(file.exists()){
            Commit CurrentCommit = Utils.readObject(file, Commit.class);
            return CurrentCommit;
        }
    }

    public Boolean inCurrentCommit(String filename){
        Commit CurrentCommit = CurrentCommit(filename);
        for(NBtable Blob : CurrentCommit.NBCommit){
            if(Blob.findName(filename)){
                return true;
            }
        }
        return false;
    }


}
