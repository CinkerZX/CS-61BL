package gitlet;

import java.io.*;


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
    public Commit NewCommit(String message) throws FileNotFoundException {
        return new Commit(head.getSHA1Value(),message,new NBtable[0]); }

    public static Commit FindCommit(String SHA1Value) throws FileNotFoundException{
        File file = new File(Gitlet.fileC,SHA1Value);
        return Utils.readObject(file, Commit.class);
    }

    public Boolean inCurrentCommit(String filename) throws FileNotFoundException { return NBtable.FileNameinNBArray(filename, FindCommit(head.getSHA1Value()).NBCommit); }

    public static Boolean inCommit(String filename, String commitID) throws FileNotFoundException{ return NBtable.FileNameinNBArray(filename, FindCommit(commitID).NBCommit); }

    public static Commit ParentCommit(Commit CurrentCommit) throws FileNotFoundException { return FindCommit(CurrentCommit.getPaSHA()[0]); }

    public static void writeBM() throws IOException {
        if(Gitlet.fileBM.exists()){
            PrintWriter writer = new PrintWriter(Gitlet.fileBM);
            writer.print("");
            writer.close();
        }else{
            Gitlet.fileBM.createNewFile();
        }
        Utils.writeObject(Gitlet.fileBM,Gitlet.branchManager);
    }

    public static Boolean InPastedCommit(String filename) throws FileNotFoundException { return InPastedCommitHelper(filename, Gitlet.branchManager.head.getSHA1Value()); }

    public static Boolean InPastedCommitHelper(String filename, String commitID) throws FileNotFoundException {
        if(FindCommit(commitID).getPaSHA()[0].length()<2){
            return false;
        }else if(inCommit(filename,commitID)){
            return true;
        }else{
            return InPastedCommitHelper(filename, FindCommit(commitID).getPaSHA()[0]);
        }
    }

}
