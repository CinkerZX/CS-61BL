package gitlet;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.io.File;

import static gitlet.BranchManage.current_commit;
import static gitlet.BranchManage.getCommit;
import static gitlet.LimeTreeFamily.*;
import static gitlet.Gitlet.*;
import static gitlet.NBtable.*;

public class LimeTree_Cons_Test {
    public static void main(String... args){
        //Todo: check the current branches; construct the tree based on the branches
        String working_directory;
        working_directory = "C:/Users/Cinker/Documents/Doc second year/Course/CS 61BL/skeleton-su20/proj2/testing/test09-merge_0";

        BranchManage branchManage;

        File d = new File(working_directory,".gitlet");
        File branchMa = new File(d.getPath(), "branch");

        NBtable[] HEAD_nb_files;
        NBtable[] OTHER_nb_files;

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

            HEAD_nb_files = getCommit(sha_moved).getNB_commit();
            OTHER_nb_files = getCommit(sha_remain).getNB_commit();

            System.out.println("files in "+getCommit(sha_moved).getMetadata()[0]);
            for(NBtable t : HEAD_nb_files){
                Blob myBlob = getBlob(t.getSha1_file_name());
                System.out.println(myBlob.getContent());
            }

            System.out.println("files in "+getCommit(sha_remain).getMetadata()[0]);
            for(NBtable t : OTHER_nb_files){
                Blob myBlob = getBlob(t.getSha1_file_name());
                System.out.println(myBlob.getContent());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
