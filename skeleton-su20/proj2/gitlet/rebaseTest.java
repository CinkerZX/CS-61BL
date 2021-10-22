package gitlet;

//import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.io.File;
import java.io.IOException;

import static gitlet.BranchManage.current_commit;
import static gitlet.BranchManage.getCommit;
import static gitlet.LimeTreeFamily.*;
import static gitlet.Gitlet.*;
import static gitlet.NBtable.*;

public class rebaseTest {
    public static void main(String... args) throws IOException {
        String working_directory;
        working_directory = System.getProperty("user.dir");
        File d = new File(working_directory,".gitlet");
        File branchMa = new File(d.getPath(), "branch");

        try {
            BranchManage branch = Utils.readObject(branchMa, BranchManage.class);
            NBtable[] branches = branch.getBranches();

            String sha_moved = branches[0].getSha1_file_name();
//            String sha_remained = branches[1].getSha1_file_name();

            Commit commit_4_2 = getCommit(sha_moved);
            NBtable[] head_nb_files = commit_4_2.getNB_commit();
            System.out.println("Commit -4'-");
            System.out.println(commit_4_2.getMetadata()[0]);
            printout_Files_NBCommit(head_nb_files);

            Commit commit_3_2 = getCommit(commit_4_2.getPa_sha()[0]);
            head_nb_files = commit_3_2.getNB_commit();
            System.out.println("Commit -3'-");
            System.out.println(commit_3_2.getMetadata()[0]);
            printout_Files_NBCommit(head_nb_files);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // helper function
    public static void printout_Files_NBCommit(NBtable[] file_commits_NBtable){
        for(NBtable t : file_commits_NBtable){
            Blob myBlob = getBlob(t.getSha1_file_name());
            System.out.println(myBlob.getContent());
        }
    }
}