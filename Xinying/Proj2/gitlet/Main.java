package gitlet;

import java.io.File;
import java.io.IOException;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) throws IOException {
        // FILL THIS IN
        String workingDirectory;
        workingDirectory = System.getProperty("user.dir");

        Gitlet gitletRe = new Gitlet(workingDirectory);

        // if args[0] == XXX do XXX  init
        if(args.length >0){
            switch(args[0]){
                case "init":
                    if(args.length > 1){
                        System.out.println("Incorrect operands.");
                    }else{gitletRe.init();}
                case "commit":
                    // try to construct a new commit from gitlet packge
                    if(args.length > 1){
                        System.out.println("Incorrect operands.");
                    }else{gitletRe.commit(args[1]);}
                default:
                    System.out.println("No command with that name exists.");
            }
        }else{
            System.out.println("Please enter a command.");
        }

    }




}
