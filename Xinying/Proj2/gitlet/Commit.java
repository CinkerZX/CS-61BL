package gitlet;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*attributes

        -paSHA : SHA1(previous commit) of the previous commit object
        -Metadata
            -message
            -timestamp
        (Files that tracked in the folder)
        -NBCommit : NBtable[ ] // Name-blob table
            -The full name of the file
            -The sha1(Blobs) of the file
        -Commit 0: init

            -SHA1(previous commit) = NONE
            -Metadata
            -NBtable[ ] = NONE
        -Commit n

            -SHA1(previous commit)
            -Metadata
            -NBtable[ ]
        Function
            construction read the file name + blob name information from the staging area
        findhead
*/
public class Commit implements Serializable {
    //public String mySHA;
    private String[] paSHA;
    public String[] Metadata;  //include message and timestamp
    public NBtable[] NBCommit;
    private static final long serialVersionUID = 8319805659239996826L;

    //Commit 0 constructor
    public Commit(){
        paSHA = new String[] {""};
        Metadata = new String[] {"initial commit","Thur Jan 1 00:00:00 1970 -0800"};
        NBCommit = null;
    }
    //Commit n constructor
    public Commit(String parentSHA,String message,NBtable[] NBcommit){
        paSHA = new String[] {parentSHA};
        Metadata = new String[] {message,timestamp()};
        NBCommit = NBcommit;
    }
    //Merged Commit constructor
    public Commit(String FparentSHA,String SparentSHA,String message,NBtable[] NBcommit){
        paSHA = new String[] {FparentSHA,SparentSHA};
        Metadata = new String[] {message,timestamp()};
        NBCommit = NBcommit;
    }

    private String timestamp(){
        DateFormat timestamp = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z", Locale.UK);
        return timestamp.format(new Date());
    }
    public void setpaSHA(String[] paSHA){ this.paSHA = paSHA; }
    public String[] getPaSHA(){ return paSHA; }


}
