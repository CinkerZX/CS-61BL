package gitlet;

//* attributes
//    - pa_sha: SHA1(previous commit) of the previous commit object
//    - Metadata
//        - message
//        - timestamp
//    - Files that tracked in the folder
//        - Nb_commit: NBtable[ ] // Name-blob table
//            - The full name of the file
//            - The sha1(Blobs) of the file
//* Commit 0: init
//    - pa_sha: SHA1(previous commit) = NONE
//    - Metadata
//    - Nb_commit: NBtable[ ] = NONE
//* Commit n
//    - pa_sha: SHA1(previous commit)
//    - Metadata
//    - Nb_commit: NBtable[ ] (For recording the blobs, each of the NBtable contains the name of the file, and the sha1 of the entire blob)
//* Function
//    - construction
//        read the file name + blob name information from the staging area
//    - findhead

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Commit implements Serializable {
    // Attributes
    private String pa_sha;// String S need to be upper case
    private String[] metadata; // message & timestep
    private NBtable[] NB_commit;

    // get and format the current time
    private String timestamp(){ //can be used inside this class directly
        String timeStamp = new SimpleDateFormat("HH:mm:ss zzz, EEE, dd MMM yyyy", Locale.UK).format(new Date());
        return timeStamp;
    }

    //Constructor: commit_0
    public Commit(){
        pa_sha = "";
        metadata = new String[] {"initial commit", "00:00:00 UTC, Thu, 1 Jan 1970"}; //create a new array object and assign to metadata
        NB_commit = null; //null n is lower case
    }

    //Constructor: commit n
    public Commit(String Pa_sha, String Message, NBtable[] Nb_commit){
        pa_sha = Pa_sha;
        metadata = new String[] {Message, timestamp()}; //{} array
        NB_commit = Nb_commit;
    }

    // set the pa_sha of the commit
    public void setPa_sha(String sha_name){
        pa_sha = sha_name;
    }

    // set the metadata of the commit
    public void setMetadata(String Arg){
        metadata = new String[] {Arg, timestamp()};
    }

    // get the NBtable list of files
    public NBtable[] getNB_commit(){
        return(NB_commit);
    }

    public String getPa_sha(){
        return(pa_sha);
    }

    public String[] getMetadata (){
        return(metadata);
    }

    // Get the parent commit object
    public Commit pa_commit(String sha_pa){ // name => sha1
        File file = new File(".gitlet/Commits", sha_pa);
        try{
            Commit pa_commit = Utils.readObject(file, Commit.class); //Commit is a class
            return pa_commit;
        } catch (Exception e){
            System.out.println("No parent commit found");
            throw new RuntimeException(e);
        }
    }

    // Fill the blos in
    public void setNB_commit(NBtable[] NB_commit) {
        this.NB_commit = NB_commit;
    }
}