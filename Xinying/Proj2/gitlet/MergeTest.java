package gitlet;

import java.io.FileNotFoundException;

public class MergeTest {

    public static LimeFamily MakeALimeTree(){
        //TODO: this method generate a LimeTree from a CommitTree
        // using generateLimeTree and generateLimeTreeHelper method in Gitlet


        return new LimeFamily(new String[] {"0","0"});
    }

    /** this method generate a CommitTree from scratch */
    public static BranchManager.CommitTree MakeACommitTree() throws FileNotFoundException {
        return Gitlet.MakeACT("master","main");
    }



    public static void main(String[] args) throws FileNotFoundException {
        BranchManager.CommitTree CT = MakeACommitTree();

        //LimeFamily lime = MakeALimeTree();
        //lime.printBFS();



    }
}
