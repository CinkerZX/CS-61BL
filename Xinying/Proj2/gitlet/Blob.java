package gitlet;

import java.io.File;
import java.io.Serializable;

public class Blob implements Serializable {

    private String BlobID; // SHA1(filename+content)
    private String filename;
    public String content;

    //constructor
    public Blob(File file){
        content = Utils.readContentsAsString(file);
        filename = file.getName();
        BlobID = Utils.sha1(file.getName() + content);
    }

    public String getBlobID(){
        return BlobID;
    }

    public String getfilename(){
        return filename;
    }

    public String getcontent(){
        return content;
    }


}
