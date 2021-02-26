package gitlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;


public class BranchManager implements Serializable {
    public NBtable[] branches;
    public NBtable head;

    public BranchManager(String sha1Commit0){
        NBtable master = new NBtable("master", sha1Commit0);
        NBtable[] b = new NBtable[] {master};
        branches = b;
        head = master;
    }

    public BranchManager(){

    }

    /*  adding new braches
    NBtable[] newB = new NBtable[b.length +1]
    for  ....  挨个复制branch
    newB[lb.length] = newBranch

    */


    public void update_branch(String newSHA){
        System.out.println(head.getSHA1Value());

        for (NBtable branch : branches){
            if(branch.findSHA(head.getSHA1Value())){
                branch.setSHA1Value(newSHA);
                break;
            }
        }
        head.setSHA1Value(newSHA);
    }

    public Commit NewCommit(String message) throws FileNotFoundException {
        // copy current commit and set it as the parent commit
        Commit newCommit = new Commit(head.getSHA1Value(),message,FindCommit(head.getSHA1Value()).NBCommit);
        return newCommit;
    }

    public Commit FindCommit(String SHA1Value){
        /*try{*/
        File file = new File("./.gitlet/Commits",SHA1Value);
        Commit thisCommit = Utils.readObject(file, Commit.class);
        return thisCommit;
        /*} catch(Exception e){
            System.out.println("Current Commit File Not Found");
            throw new FileNotFoundException();
        }*/
    }

    public Boolean inCurrentCommit(String filename) throws FileNotFoundException {
        Commit CurrentCommit = FindCommit(head.getSHA1Value());
        return NBtable.inNBArray(filename, CurrentCommit.NBCommit);
    }

    public Commit ParentCommit(Commit CurrentCommit) throws FileNotFoundException {
        return FindCommit(CurrentCommit.getPaSHA());
    }

    public void writeBM(String workingDirectory,BranchManager BM) throws IOException {
        File file1 = new File(workingDirectory,".gitlet");
        File file = new File(file1,"BrancheManager");
        file.createNewFile();
        Utils.writeObject(file,BM);
    }

}
