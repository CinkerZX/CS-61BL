package gitlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;


public class BranchManager implements Serializable {
    public NBtable[] branches;
    public NBtable head;
    private static final long serialVersionUID = -586838958655118021L;

    public BranchManager(String sha1Commit0){
        NBtable master = new NBtable("master", sha1Commit0);
        NBtable[] b = new NBtable[] {master};
        branches = b;
        head = master;
    }

    public BranchManager(){ }

    /*  adding new braches
    NBtable[] newB = new NBtable[b.length +1]
    for  ....  挨个复制branch
    newB[lb.length] = newBranch

    */

    public void update_branch(String newSHA){
        for (NBtable branch : branches){
            if(branch.findSHA(head.getSHA1Value())){
                branch.setSHA1Value(newSHA);
                break;
            }
        }
        head.setSHA1Value(newSHA);
    }

    // copy current commit and set it as the parent commit
    public Commit NewCommit(String message) throws FileNotFoundException { return new Commit(head.getSHA1Value(),message,FindCommit(head.getSHA1Value()).NBCommit); }

    public static Commit FindCommit(String SHA1Value) throws FileNotFoundException{
        File file = new File("./.gitlet/Commits",SHA1Value);
        Commit thisCommit = Utils.readObject(file, Commit.class);
        return thisCommit;
    }

    public Boolean inCurrentCommit(String filename) throws FileNotFoundException { return NBtable.FileNameinNBArray(filename, FindCommit(head.getSHA1Value()).NBCommit); }

    public static Boolean inCommit(String filename, String commitID) throws FileNotFoundException{ return NBtable.FileNameinNBArray(filename, FindCommit(commitID).NBCommit); }

    public static Commit ParentCommit(Commit CurrentCommit) throws FileNotFoundException { return FindCommit(CurrentCommit.getPaSHA()); }

    public static void writeBM(String workingDirectory,BranchManager BM) throws IOException {
        File file1 = new File(workingDirectory,".gitlet");
        File file = new File(file1,"BrancheManager");
        file.createNewFile();
        Utils.writeObject(file,BM);
    }

    public static Boolean InPastedCommit(String filename, BranchManager BM) throws FileNotFoundException { return InPastedCommitHelper(filename, BM.head.getSHA1Value()); }

    public static Boolean InPastedCommitHelper(String filename, String commitID) throws FileNotFoundException {
        if(FindCommit(commitID).getPaSHA()==null){
            return false;
        }else if(inCommit(filename,commitID)){
            return true;
        }else{
            return InPastedCommitHelper(filename, FindCommit(commitID).getPaSHA());
        }
    }

}
