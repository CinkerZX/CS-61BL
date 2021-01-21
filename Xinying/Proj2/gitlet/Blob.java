package gitlet;

import java.io.File;


public class Blob {

    public String name; // SHA1(filename+content)
    public String content;

    //constructor
    public Blob(File file){
        content = Utils.readContentsAsString(file);
        name = Utils.sha1(file.getName() + content);
    }

}
