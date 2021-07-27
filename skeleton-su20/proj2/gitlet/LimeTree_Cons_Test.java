package gitlet;

import java.io.File;

import static gitlet.BranchManage.current_commit;
import static gitlet.BranchManage.getCommit;

public class LimeTree_Cons_Test {
    public static void main(String... args){
        //Todo: check the current branches; construct the tree based on the branches
        String working_directory;
        working_directory = "C:/Users/Cinker/Documents/Doc second year/Course/CS 61BL/skeleton-su20/proj2/testing/test07-buildTree_0";

        BranchManage branchManage;

        File d = new File(working_directory,".gitlet");
        File branchMa = new File(d.getPath(), "branch");
        try {
            BranchManage branch = Utils.readObject(branchMa, BranchManage.class);
            NBtable[] branches = branch.getBranches();
            /*System.out.println(branches[0].getSha1_file_name());
            System.out.println(getCommit(branches[0].getSha1_file_name()).getMetadata()[0]);
            System.out.println(branches[1].getSha1_file_name());
            System.out.println(getCommit(branches[1].getSha1_file_name()).getMetadata()[0]);

            Commit commit_1 = getCommit(branches[0].getSha1_file_name());
            Commit commit_2 = getCommit(branches[1].getSha1_file_name());

            System.out.println(commit_1.getPa_sha()[0]);
            Commit commit_11 = getCommit(commit_1.getPa_sha()[0]);
            System.out.println(commit_11.getMetadata()[0]);

            System.out.println(commit_2.getPa_sha()[0]);
            Commit commit_22 = getCommit(commit_2.getPa_sha()[0]);
            System.out.println(commit_22.getMetadata()[0]);*/


            String sha_moved = branches[0].getSha1_file_name();
            String sha_remain = branches[1].getSha1_file_name();
            // Construct LimeTreeFamily
            LimeTreeFamily my_tree = new LimeTreeFamily(sha_moved, sha_remain);
            // Full fill the tree
            my_tree.addChild();
            // Check if the tree is correct
            my_tree.depth_First_Tra();

/*            NBtable[] branches = branch.getBranches();
            for(NBtable t : branches){
                System.out.println("Branch: " + t.getFile_name()+ " has the following commits");
                Commit commitCon =  getCommit(t.getSha1_file_name());
                System.out.println("sha: "+t.getSha1_file_name());
                while(!commitCon.getPa_sha()[0].equals("")){
                    System.out.println(commitCon.getMetadata()[0]);
                    commitCon = getCommit(commitCon.getPa_sha()[0]);
                    System.out.println("sha: "+commitCon.getPa_sha()[0]);
                }
                System.out.println(commitCon.getMetadata()[0]);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
