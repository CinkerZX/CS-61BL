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
                    break; // need break each case
                case "commit":
                    // construct the commit object by gitlet method ""
                    if(args.length == 2){
                        mygitlet.commit(args[1], "", "");
                    }else if(args.length > 2){
                        System.out.println("Please enter in such formation: commit 'XXXXX'.");
                    }
                    else{
                        System.out.println("Please enter a commit message.");
                    }
                    break;
                case "log":
                    // print the log
                    mygitlet.log();
                    break;
                case "global-log":
                    mygitlet.globalLog();
                    break;
                case "find":
                    if(args.length == 2){
                        mygitlet.find(args[1]);
                    }
                    break;
                case "status":
                    mygitlet.status();
                    break;
                case "add":
                    // add
                    if(args.length == 2){
                        mygitlet.add(args[1]);
                    }
                    else if(args.length > 2){
                        System.out.println("Please enter in such formation: add 'XXXXX'.");
                    }
                    else{
                        System.out.println("Please enter the added file name.");
                    }
                    break;
                // remove
                case "rm":
                    if(args.length == 2){
                        mygitlet.rm(args[1]);
                    }
                    else if(args.length > 2){
                        System.out.println("Please enter in such formation: rm 'XXXXX'.");
                    }
                    else{
                        System.out.println("Please enter the name of the removed file.");
                    }
                    break;
                // branch
                case "branch":
                    if(args.length == 2){
                        mygitlet.branch(args[1]);
                    }else{
                        System.out.println("Please enter in such formation: branch 'XXXXX'.");
                    }
                    break;
                // rm-branch
                case "rm-branch":
                    if(args.length == 2){
                        mygitlet.rm_branch(args[1]);
                    }else{
                        System.out.println("Please enter in such formation: branch 'XXXXX'.");
                    }
                    break;
                //check out
                case "checkout":
                    if(args.length == 2){ //checkout branch name
                        mygitlet.checkoutBranch(args[1]);
                    }
                    else if(args.length == 3){ //checkout -- filename
                        mygitlet.checkoutFile(args[2]);
                    }
                    else if(args.length == 4){ //checkout commit_id -- file_name
                        mygitlet.checkoutCommitFilename(args[1],args[3]);
                    }
                    else{
                        System.out.println("Please enter in one of the following formation: checkout -- [file name]; checkout -- [commit id] -- [file name]; checkout [branch name]");
                    }
                    break;
                // reset
                case "reset":
                    if (args.length == 2){ // reset commit id
                        mygitlet.reset(args[1]);
                    }
                    else{
                        System.out.println("Please enter in the formation: reset [commit id]");
                    }
                    break;
                // merge
                /*case "merge":
                    if (args.length == 2){ //merge [branch name]
                        mygitlet.merge(args[1]);
                    }
                    else{
                        System.out.println("Please enter in the formation: merge [branch name]");
                    }
                    break;*/
                default:
                    System.out.println("No command with that name exists.");
                    break;
            }
        }
        else {
            System.out.println("Please enter a command.");
        }
    }

}
