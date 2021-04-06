package gitlet;


//* attributes
//        - Name: SHA1(the file name + the file content)
//        - Content

import java.io.File;
import java.io.Serializable;

public class Blob implements Serializable {
    // attributes
    private String blobID; //sha1(blob)
    private String fileName; // the original name of the file
    private String content;

    // constructor
    public Blob(File file){
        content = Utils.readContentsAsString(file);
        fileName = file.getName();
        blobID = Utils.sha1(file.getName() + content); // the name of this blob
    }

    public String getBlob_name(){
        return(blobID);
    }

    public String getfileName(){
        return(fileName);
    }

}
