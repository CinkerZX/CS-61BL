package gitlet;


//* attributes
//        - Name: SHA1(the file name + the file content)
//        - Content

import java.io.File;

public class Blob {
    // attributes
    private String name;
    private String content;

    // constructor
    public Blob(File file){
        content = Utils.readContentsAsString(file);
        name = Utils.sha1(file.getName() + content); // the name of this blob
    }

}
