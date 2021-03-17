package gitlet;

//* attributes
//        - The full name of the file
//        - The sha1(Blobs) of the file
//        * function
//        - find: input = SHA1; output = true/false

import com.sun.media.sound.RIFFInvalidDataException;
import com.sun.xml.internal.bind.v2.runtime.output.StAXExStreamWriterOutput;
import org.apache.commons.lang3.ArrayUtils;

import java.io.Serializable;

public class NBtable implements Serializable {
    //attributes
    private String file_name;
    private String sha1_file_name;

    //Constructor
    public NBtable(){
        file_name = "";
        sha1_file_name = "";
    }

    //Constructor
    public NBtable(String name, String sha1){
        file_name = name;
        sha1_file_name = sha1;
    }

    // function find by file name
    public boolean find(String name){
        if (file_name.equals(name)){
            return(true);
        }
        else
            return(false);
    }

    // function find by sha1
    public boolean find_sha1(String sha1){
        if (sha1_file_name.equals(sha1)){
            return(true);
        }
        else
            return(false);
    }

    public void setSha1_file_name(String sha1){
        sha1_file_name = sha1;
    }

    public String getSha1_file_name() {
        return (sha1_file_name);
    }

    public String getFile_name(){
        return (file_name);
    }
}
