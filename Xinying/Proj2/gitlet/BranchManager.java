package gitlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.apache.commons.lang3.ArrayUtils;




public class BranchManager  {
    public NBtable[] branches;
    public NBtable head;

    public BranchManager(String sha1Commit0){
        NBtable master = new NBtable("master", sha1Commit0);
        branches[0] = master;
        head = master;
    }

    public void update_branch(String newSHA){
        for (NBtable branch : branches){
            if(branch.findSHA(head.getSHA1Value())){
                branch.setSHA1Value(newSHA);
            }
        }
        head.setSHA1Value(newSHA);
    }

    public Commit NewCommit(String message) throws FileNotFoundException {
        // copy current commit and set it as the parent commit
        Commit newCommit = new Commit(head.getSHA1Value(),message,CurrentCommit().NBCommit);
        return newCommit;
    }

    public Commit CurrentCommit() throws FileNotFoundException {
        try{
            File file = new File("./.gitlet/Commits",head.getSHA1Value());
            Commit CurrentCommit = Utils.readObject(file, Commit.class);
            return CurrentCommit;
        } catch(Exception e){
            System.out.println("Current Commit File Not Found");
            throw new FileNotFoundException();
        }
    }

    public Boolean inCurrentCommit(String filename) throws FileNotFoundException {
        Commit CurrentCommit = CurrentCommit();
        return NBtable.inNBArray(filename, CurrentCommit.NBCommit);
    }


}
