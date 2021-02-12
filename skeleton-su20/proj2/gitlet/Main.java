package gitlet;

import java.io.IOException;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author
 */
public class Main {
    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) throws IOException {
        // FILL THIS IN
        String working_directory;
        working_directory = System.getProperty("user.dir");

        Gitlet mygitlet = new Gitlet(working_directory);

        if(args.length>0) {
            // if args[0] == XXX do XXX
            switch (args[0]) {
                case "init":
                    if(args.length == 1){
                        mygitlet.init();
                    }
                    else{
                        System.out.println("Incorrect operands.");
                    }
                    break;
                case "commit":
                    // construct the commit object by gitlet method ""
                    mygitlet.commit(args[1]);
                default:
                    System.out.println("No command with that name exists.");
            }
        }
        else {
            System.out.println("Please enter a command.");
        }
    }

}
