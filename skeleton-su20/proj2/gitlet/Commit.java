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
//    - Nb_commit: NBtable[ ]
//* Function
//    - construction
//        read the file name + blob name information from the staging area
//    - findhead

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Commit {
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
        metadata = new String[] {"init commit", "00:00:00 UTC, Thu, 1 Jan 1970"}; //create a new array object and assign to metadata
        NB_commit = null; //null n is lower case
    }

    //Constructor: commit_0
    public Commit(String Pa_sha, String Message, NBtable[] Nb_commit){
        pa_sha = Pa_sha;
        metadata = new String[] {Message, timestamp()}; //{} array
        NB_commit = Nb_commit;
    }

}