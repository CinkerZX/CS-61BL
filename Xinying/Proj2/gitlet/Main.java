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
                    if(args.length == 1){ gitletRe.init(); }
                    else{ System.out.println("Incorrect operands."); }
                    break;  /// must break after each case!!!!!!!!
                case "commit":
                    if(args.length == 2){ gitletRe.commit(args[1]); }
                    else if(args.length == 1){ System.out.println("Please enter a commit message."); }
                    else{ System.out.println("gitlet.Main commit 'commit message'"); }
                    break;
                case "add":
                    if(args.length == 2){ gitletRe.add(workingDirectory,args[1]); }
                    else if(args.length == 1){ System.out.println("Please enter a file name."); }
                    else{ System.out.println("gitlet.Main add [filename]"); }
                    break;
                case "rm":
                    if(args.length == 2){ gitletRe.rm(workingDirectory, args[1]); }
                    else if(args.length == 1){ System.out.println("Please enter a file name."); }
                    else{ System.out.println("gitlet.Main rm [filename]"); }
                    break;
                case "log":
                    if(args.length == 1){ gitletRe.log(); }
                    else{ System.out.println("gitlet.Main log"); }
                    break;
                case "global-log":
                    if(args.length == 1){ gitletRe.global_log(workingDirectory); }
                    else{ System.out.println("gitlet.Main global-log"); }
                    break;
                case "find":
                    if(args.length == 2){ gitletRe.find(args[1]); }
                    else{ System.out.println("gitlet.Main find [commit message]"); }
                    break;
                case "status":
                    if(args.length == 1){ gitletRe.status(workingDirectory); }
                    else{ System.out.println("gitlet.Main status"); }
                    break;
                case "branch":
                    if(args.length == 2){ gitletRe.branch(workingDirectory,args[1]); }
                    else if(args.length == 1){ System.out.println("Please enter a branch name."); }
                    else{ System.out.println("gitlet.Main branch [branch name]"); }
                    break;
                case "rm-branch":
                    if(args.length == 2){ gitletRe.rm_branch(workingDirectory,args[1]); }
                    else if(args.length == 1){ System.out.println("Please enter a branch name."); }
                    else{ System.out.println("gitlet.Main rm-branch [branch name]"); }
                    break;
                default:
                    System.out.println("No command with that name exists.");
            }
        }else{
            System.out.println("Please enter a command.");
        }
    }
}
