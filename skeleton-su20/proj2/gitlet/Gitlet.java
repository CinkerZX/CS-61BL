package gitlet;
// init inititalize a repository folder under the directory of the object folder
// status check the tracked files(folder) change
// add create the corresponding blobs; add informations in the staging area
// remove //
// commit // construct the commit, accept the string text (messsage)
// update the pointers
// log

import java.io.File; // for creating file/folder
import java.io.IOException;

public class Gitlet{ // class is abstract

    // Attributes
    private String working_directory;

    //Constructor
    public Gitlet(String Direct){ // when new an object, we use this method
        working_directory = Direct;
    }

    public void init() throws IOException {
        File d = new File(working_directory,".gitlet"); // File(Parents' path, child folder/file name)
        if (d.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        }
        else {
            //Create the folder of ".gitlet"
            d.mkdir(); // Create a new folder

            //Create the folder of ".Staging Area"
            File d_2 = new File(d.getPath(), ".Staging Area");
            d_2.mkdir();
            //Create the new file of "Staged for addition"
            File d_2_1 = new File(d_2.getPath(), ".Staged for addition");
            d_2_1.createNewFile(); // Create a new file
            //Create the new file of "Staged for removal"
            File d_2_2 = new File(d_2.getPath(), ".Staged for removal");
            d_2_2.createNewFile();

            //Create the folder of ".Commits"
            File d_3 = new File(d.getPath(), ".Commits");
            d_3.mkdir();

            Commit commit_0 = new Commit();
            //write the commit_0 object into the .commits folder
            //write the NBtable object
            //write the branch object


            //Create the folder of ".Blobs"
            File d_4 = new File(d.getPath(), ".Blobs");
            d_4.mkdir();
        }
    }

    // construct the commit object
    public void commit(String... Args){
        // input the parameter, the constructor can be selected automatically

    }


}
