package gitlet;

//import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.io.File;
import java.io.IOException;

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
            // my_tree.depth_First_Tra();
            // Check if the splitPoint function
            Commit split_commit = getCommit(my_tree.splitPoint().PaSha_pair[0]);
            //System.out.println(split_commit.getMetadata()[0]);

            HEAD_nb_files = getCommit(sha_moved).getNB_commit();
            OTHER_nb_files = getCommit(sha_remain).getNB_commit();

            System.out.println("files in "+getCommit(sha_moved).getMetadata()[0]);
            printout_Files_NBCommit(HEAD_nb_files);

            System.out.println("files in "+getCommit(sha_remain).getMetadata()[0]);
            printout_Files_NBCommit(OTHER_nb_files);

            System.out.println("files in "+split_commit.getMetadata()[0]);
            NBtable[] SPLIT_nb_files = split_commit.getNB_commit();
            printout_Files_NBCommit(SPLIT_nb_files);

            System.out.println("case(2) Files added in OTHER (commit3)"); //F.txt
            String[] add_filenames = NBtable.get_names_Compliment(OTHER_nb_files, SPLIT_nb_files);
            printout_Files_fileName(add_filenames);


            System.out.println("case (3) Files removed from OTHER and not changed in HEAD"); //D.txt
            String[] rm_filenames = NBtable.get_names_Compliment(SPLIT_nb_files, OTHER_nb_files);
            for (String s : rm_filenames){
                if (files_sameContent(s,HEAD_nb_files,SPLIT_nb_files)){
                    // if the same, delete the file from working directory
                    System.out.println(s);
                }
            }

            System.out.println("case(1) Files modified in OTHER but not in HEAD");
            String[] set_SOH_Name = NBtable.get_simple_String_Intersection(NBtable.getFile_name_array(SPLIT_nb_files),NBtable.getFile_name_array(OTHER_nb_files));
            set_SOH_Name = NBtable.get_simple_String_Intersection(set_SOH_Name,NBtable.getFile_name_array(HEAD_nb_files));
            String[] files_HO_sameCon = NBtable.get_string_Array(HEAD_nb_files, OTHER_nb_files,"");
            String[] files_HO_diffCon = NBtable.get_names_Compliment(set_SOH_Name, files_HO_sameCon);
            String[] files_SH_sameCon = NBtable.get_string_Array(SPLIT_nb_files, HEAD_nb_files,"");
            String[] update_files = NBtable.get_simple_String_Intersection(files_SH_sameCon, files_HO_diffCon);
            String[] HEAD_files_name = getFile_name_array(HEAD_nb_files);
            String[] update_files_2 = get_names_Compliment(update_files, HEAD_files_name);
            printout_Files_fileName(update_files_2);

            System.out.println("case(4) files have different content in head and other but in split point.");
            String[] SPLIT_files_name = getFile_name_array(SPLIT_nb_files);
            String[] files_SO_diffCon = get_simple_String_Intersection(files_HO_diffCon, SPLIT_files_name);
            printout_Files_fileName(files_SO_diffCon);

            System.out.println("case(5) files have different content in head and other but absent in split point");
            String[] addInHO_filenames_diffCon = NBtable.get_names_Compliment(files_HO_diffCon, SPLIT_files_name);
            printout_Files_fileName(addInHO_filenames_diffCon);

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

    public static void printout_Files_fileName(String[] NamesOfFiles){
        for(String t : NamesOfFiles){
            System.out.println(t);
        }
    }

    // judge if two files with the same name have the same content in two NB_table array
    private static boolean files_sameContent(String filename, NBtable[] NBtableArr_1, NBtable[] NBtableArr_2){
        if(!(myTable(NBtableArr_1,filename) == null)){
            if (myTable(NBtableArr_1,filename).getSha1_file_name().equals(myTable(NBtableArr_2,filename).getSha1_file_name()))
            {
                return(true);
            }else{
                return(false);
            }
        }else {
            return (false);
        }
    }

    // get the NBtable from a NBtable[] by file name
    private static NBtable myTable(NBtable[] NB_table_list, String file_name){
        for (NBtable table : NB_table_list){
            if (table.getFile_name().equals(file_name)){
                return(table);
            }
        }
        return(null);
    }


}
