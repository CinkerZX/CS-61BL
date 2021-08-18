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

            String sha_moved = branches[0].getSha1_file_name();
            String sha_remain = branches[1].getSha1_file_name();
            // Construct LimeTreeFamily
            LimeTreeFamily my_tree = new LimeTreeFamily(sha_moved, sha_remain);
            // Fulfill the tree
            my_tree.addChild();
            // Check if the tree is correct
            my_tree.depth_First_Tra();
            // Check if the splitPoint function
            Commit split_commit = getCommit(my_tree.splitPoint().PaSha_pair[0]);
            System.out.println(split_commit.getMetadata()[0]);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
