package gitlet;

import java.awt.geom.AffineTransform;
import java.io.*;
import java.util.*;

public class Gitlet implements Serializable {

    public static String workingDirectory;
    public static BranchManager branchManager; // be available to all functions in gitlet class
    public static File fileWD;
    public static File fileG;
    public static File fileC;
    public static File fileB;
    public static File fileS;
    public static File fileBM;
    public static File stagingAdd;
    public static File stagingRem;

    public Gitlet(String wd){
        this.workingDirectory = wd;
        this.fileWD = new File(workingDirectory);
        this.fileG = new File(fileWD,".gitlet");
        this.fileB = new File(fileG,"Blobs");
        this.fileC = new File(fileG,"Commits");
        this.fileS = new File(fileG,"Staging Area");
        this.fileBM = new File(workingDirectory+"/.gitlet/BrancheManager");
        this.stagingAdd = new File(fileS,"Staged for addition");
        this.stagingRem = new File(fileS,"Staged for removal");
    }

    public void init() throws IOException {
        /* create Gitlet Repository
                - .gitlet (folder)
                   -- Staging Area (folder)
                       --- Staged for addition (folder)
                       --- Staged for removal (folder)
                   -- Commits (folder)
                   -- Blobs (folder)
                   -- BrancheManager (file)  */
        if(!fileG.exists()){
            fileG.mkdir();
            fileS.mkdir();
            stagingAdd.mkdir();
            stagingRem.mkdir();
            fileC.mkdir();
            fileB.mkdir();
            // initial commit0
            Commit Commit0 = new Commit();
            File C0 = new File(fileC.getPath(),Utils.sha1(Utils.serialize(Commit0)));
            C0.createNewFile();
            Utils.writeObject(C0, Commit0);
            // initial branch manager
            this.branchManager = new BranchManager(Utils.sha1(Utils.serialize(Commit0)));
            branchManager.writeBM();
        }else{
            System.out.println("A Gitlet version-control system already exists in the current directory.");

        }
    }

    public void add(String filename) throws IOException {
        // if the file exist in working directory
        // check if the file has already been committed(in blobs folder),
            // if so, there is no need to staging
        // check if it exists in staging for addition (replicate action)
        File file = new File(fileWD, filename);
        if(!file.exists()){ // check if the file exists in working directory
            System.out.println("File does not exist.");
        }else{
            Blob addedFile = new Blob(file);  // generate a new blob object
            File fileJudge = new File(fileB,addedFile.getBlobID());

            if(!fileJudge.exists()){ // check if the file exists in blobs
                fileJudge.createNewFile();
                Utils.writeObject(fileJudge, addedFile);
            }
            File fileAdd = new File(stagingAdd, addedFile.getBlobID());
            if(!fileAdd.exists()){
                fileAdd.createNewFile(); // an empty file with the BlobID as the name
                Utils.writeContents(fileAdd,filename);
            }
        }
    }

    public void rm(String filename) throws IOException {
        File file = new File(fileWD, filename);
        String SHA_name = Utils.sha1(file.getName()+Utils.readContentsAsString(file));
        File fileAdd = new File(stagingAdd, SHA_name);

        Blob removedFile = new Blob(file);  // generate a new blob object
        // sava blob
        File fileRem = new File(stagingRem, removedFile.getBlobID());

        if(fileAdd.exists()){
            fileAdd.delete();
        }else if(branchManager.inCurrentCommit(filename)){
            fileRem.createNewFile(); // add to staged for removal
            Utils.writeContents(fileRem,filename);
            if(file.exists()){
                file.delete();  // delete the file from the working directory
                // already been backed up in blobs, less risk of lossing our works
            }
        }else{ System.out.println("No reason to remove the file.");}
    }

    public void commit(String arg,String SecondParent) throws IOException {
        // gitlet.Main commit 'commit message'"
        // check if the staging area is empty  &&  args checking  (failure cases
        // generate a new commit object (newCommit) by coping NBtable from "current commit"
        // make a list of tracking files combining the info from staging area
            // staged for addition : append new blobs of "staged for addition" to newCommit's NBtable
            // staged for removal : remove blobs of "staged for removal" from newCommit's NBtable
            // clear staging area
        // write commit object
        // update branches and head
        if(stagingAdd.list().length == 0 && stagingRem.list().length == 0){    // stagingAdd.list() == null 无效  因为list() returns list object, it couldn't be null it's []
            System.out.println("No changes added to the commit.");
        }
        else if(arg.isEmpty()){
            System.out.println("Please enter a commit message.");
        }
        else {
            Commit newCommit = createNewCommit(arg, SecondParent);
            //NBtable[] newbloblist = newCommit.NBCommit;
            NBtable[] newbloblist = BranchManager.FindCommit(branchManager.head.getSHA1Value()).NBCommit;
            if(newbloblist == null){newbloblist = newCommit.NBCommit;}

            // staged for addition
            for (String name : stagingAdd.list()) {
                File file = new File(fileB, name);  // use the filename stored in staging area locate blob in Blobs directory
                File file1 = new File(stagingAdd,name);  // but delete the file stored in staging area instead of Blobs directory
                Blob addition = Utils.readObject(file, Blob.class);
                newbloblist = NBtable.update(newbloblist, new NBtable(addition.getfilename(), name));
                //newbloblist = NBtable.add(newbloblist, new NBtable(addition.getfilename(), name));
                file1.delete();
            }
            // staged for removal
            for (String name : stagingRem.list()) {
                File file = new File(fileB, name);
                File file2 = new File(stagingRem, name);
                Blob removal = Utils.readObject(file, Blob.class);
                newbloblist = NBtable.remove(newbloblist, new NBtable(removal.getfilename(), name));
                file2.delete();
            }
            newCommit.NBCommit = newbloblist;
            File C = new File(fileC, Utils.sha1(Utils.serialize(newCommit)));
            C.createNewFile();
            Utils.writeObject(C, newCommit);

            branchManager.update_branch(Utils.sha1(Utils.serialize(newCommit)));
            branchManager.writeBM();
        }
    }

    public void log() throws IOException {
        ///merged node of two branches, The first parent is located in the current branch

        //TO-DO different printing format of merge node

        Commit CurrentCommit = branchManager.FindCommit(branchManager.head.getSHA1Value());
        PrintCommit(CurrentCommit,branchManager.head.getSHA1Value());
        //while(!cur_commit.getPa_sha().isEmpty()){ // pa_sha = "";
        while(!CurrentCommit.getPaSHA()[0].equals("")){  // Commit0.paSHA = null
            Commit parentCommit = branchManager.ParentCommit(CurrentCommit);
            PrintCommit(parentCommit,CurrentCommit.getPaSHA()[0]);
            CurrentCommit = parentCommit;
        }
    }

    public void global_log(){
        for (File file1 : fileC.listFiles()){
            Commit commit = Utils.readObject(file1,Commit.class);
            PrintCommit(commit, Utils.sha1(Utils.serialize(commit)));
        }
    }

    public void find(String message){
        Boolean someCommits = false;
        for (File file : fileC.listFiles()) {
            // but delete the file stored in staging area instead of Blobs directory
            Commit commit = Utils.readObject(file,Commit.class);
            if(commit.Metadata[0].equals(message)){
                System.out.println(file.getName());
                someCommits = true;
            }
        }
        if (someCommits == false){
            System.out.println("Found no commit with that message.");
        }
    }

    public void status() throws FileNotFoundException {
        int Addlength = stagingAdd.list().length;
        int Remlength = stagingRem.list().length;

        NBtable[] nbCC =  branchManager.FindCommit(branchManager.head.getSHA1Value()).NBCommit; // NBtables(real file name,blobID) in current commit
        NBtable[] nbAdd = new NBtable[Addlength]; // NBtables(real file name,blobID) in staged for addition
        NBtable[] nbRem = new NBtable[Remlength]; // NBtables(real file name,blobID) in staged for removal
        //NBtables(real file name,blobID) in workingdirectory
        NBtable[] nbWD = new NBtable[fileWD.list().length-1];

        System.out.println("=== Branches ===");
        if(true){
            String[] branches = new String[branchManager.branches.length];
            String CurrentBranch = branchManager.head.getFullName();
            for(int i=0;i < branchManager.branches.length;i++){
                branches[i] = branchManager.branches[i].getFullName();
            }
            ////lexicographic order
            Arrays.sort(branches);
            //print
            for(String branch : branches){
                if(branch.equals(CurrentBranch)){
                    System.out.println("*"+CurrentBranch);
                }else{ System.out.println(branch); }
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ==="); //nbAdd
        if(true){
            if(Addlength != 0){
                String[] additions = new String[Addlength];
                File[] additionFile = stagingAdd.listFiles();
                for(int i = 0; i < Addlength;i++){
                    additions[i] = Utils.readContentsAsString(additionFile[i]);
                    NBtable newAdd = new NBtable(additions[i],additionFile[i].getName());
                    nbAdd[i] = newAdd;
                }
                printString(additions);
            }
        }
        System.out.println();
        System.out.println("=== Removed Files ==="); // nbRem
        if(true){
            if(Remlength != 0){
                String[] removals = new String[Remlength];
                File[] removalFile = stagingRem.listFiles();
                for(int i = 0; i < Remlength;i++){
                    removals[i] = Utils.readContentsAsString(removalFile[i]);
                    NBtable newRem = new NBtable(removals[i],removalFile[i].getName());
                    nbRem[i] = newRem;
                }
                printString(removals);
            }
        }
        System.out.println();
        /////////filtering subdirectory! optional
        int i = 0;// nbWD
        for(File file : fileWD.listFiles()){
            if(!file.equals(fileG)){
                NBtable newWD = new NBtable(file.getName(),Utils.sha1(file.getName(),Utils.readContentsAsString(file)));
                nbWD[i] = newWD;
                i++;
            }
        }
        if(i == fileWD.list().length-1){
            System.out.println("=== Modifications Not Staged For Commit ===");
            if(true){
                //Tracked in the current commit, changed in the working directory, but not staged;
                //Staged for addition, but with different contents than in the working directory;
                //Staged for addition, but deleted in the working directory;
                //Not staged for removal, but tracked in the current commit and deleted from the working directory.

                // deleted
                String[] com_CC_WD = NBtable.complement(nbCC,nbWD,"FullName");
                String[] com_Add_WD = NBtable.complement(nbAdd, nbWD,"FullName");

                ///////!!!!untested
                String[] deleted = NBtable.complement(NBtable.union(com_CC_WD,com_Add_WD),NBtable.NBtoString(nbRem,"FullName"));

                for(int j = 0; j <deleted.length;j++){
                    deleted[j] = deleted[j] + " (deleted)";
                }
                //modified
                Set<String> sameNameCC = new HashSet<>();
                Set<String> sameNameAdd = new HashSet<>();
                for(String name : NBtable.intersection(nbCC,nbWD,"FullName")){
                    String SHAinWD = NBtable.FindSHAinNBArray(name,nbWD);
                    if(!NBtable.FindSHAinNBArray(name,nbCC).equals(SHAinWD)){
                        if(!NBtable.SHAinNBArray(SHAinWD,nbAdd)){
                            sameNameCC.add(name);
                        }
                    }
                }
                for(String name : NBtable.intersection(nbAdd,nbWD,"FullName")){
                    if(!NBtable.FindSHAinNBArray(name,nbAdd).equals(NBtable.FindSHAinNBArray(name,nbWD))){
                        sameNameAdd.add(name);
                    }
                }

                sameNameCC.addAll(sameNameAdd);
                String[] SCC = NBtable.SetToString(sameNameCC);
                for(int n = 0; n <SCC.length;n++){
                    SCC[n] = SCC[n] + " (modified)";
                }
                //combine
                int length = deleted.length;
                deleted = Arrays.copyOf(deleted, length+ SCC.length);
                System.arraycopy(SCC, 0, deleted, length, SCC.length);
                printString(deleted);
            }
            System.out.println();
        }else{ System.out.println("Warning! Don't operate on working directory while status"); }
        System.out.println("=== Untracked Files ===");
        if(true){
            // not in all commits
            Set<String> sameNameWD = new HashSet<>();
            for(String filename : NBtable.NBtoString(nbWD,"FullName")){
                if(!BranchManager.InPastedCommit(filename)){
                    sameNameWD.add(filename);
                }
            }
            String[] untrackfinal = NBtable.complement(NBtable.SetToString(sameNameWD),NBtable.NBtoString(nbAdd,"FullName"));// not in Staged for adition
            printString(untrackfinal);
        }
        System.out.println();
    }

    public void branch(String branchName) throws IOException {
        if(NBtable.FileNameinNBArray(branchName,branchManager.branches)){
            System.out.println("A branch with that name already exists.");
        }else{
            branchManager.branches = NBtable.update(branchManager.branches,new NBtable(branchName,""));

            NBtable b = NBtable.UseNameFindNBtable(branchName, branchManager.branches);
            if(b.getSHA1Value().equals("")){
                b.setSHA1Value(branchManager.head.getSHA1Value());
            }
            branchManager.writeBM();
        }
    }

    public void rm_branch(String branchName) throws IOException {
        if(!NBtable.FileNameinNBArray(branchName,branchManager.branches)){
            System.out.println("A branch with that name does not exist.");
        }else if(branchManager.head.getFullName().equals(branchName)){
            System.out.println("Cannot remove the current branch.");
        }else{
            branchManager.branches = NBtable.remove(branchManager.branches,new NBtable(branchName,""));
            branchManager.writeBM();
        }

    }

    public void checkout(String[] args) throws IOException {
        //args :
        // checkout -- [filename]  3  modify file with the version in the current commit
        // checkout [commit id] -- [file name]  4 modify file with the version in certain commit
        // checkout [branch name]  2 switch branch
        switch(args.length){
            case 3:
                NBtable[] blobsInCC = branchManager.FindCommit(branchManager.head.getSHA1Value()).NBCommit;
                if(!NBtable.FileNameinNBArray(args[2],blobsInCC)){ System.out.println("File does not exist in that commit.");}
                else{
                    File file = new File(fileWD,args[2]);
                    File blob = new File(fileB,NBtable.FindSHAinNBArray(args[2],blobsInCC));
                    writeFile(file,Utils.readObject(blob,Blob.class).getcontent());
                }
                break;
            case 4:
                File CommitFile = new File(fileC,args[1]);
                if(!CommitFile.exists()){System.out.println("No commit with that id exists.");}
                else{
                    Commit commit = branchManager.FindCommit(args[1]);
                    NBtable[] blobsInC = commit.NBCommit;
                    if(!NBtable.FileNameinNBArray(args[3],blobsInC)){ System.out.println("File does not exist in that commit.");}
                    else{
                        File file = new File(fileWD,args[3]);
                        File blob = new File(fileB,NBtable.FindSHAinNBArray(args[3],blobsInC));
                        writeFile(file,Utils.readObject(blob,Blob.class).getcontent());
                    }
                }
                break;
            case 2:
                if(!NBtable.FileNameinNBArray(args[1],branchManager.branches)){System.out.println("No such branch exists.");}
                else if(args[1].equals(branchManager.head.getFullName())){System.out.println("No need to checkout the current branch.");}
                else {
                    String CIDinCheckoutBranch = NBtable.FindSHAinNBArray(args[1], branchManager.branches);
                    cover(CIDinCheckoutBranch);
                    // change current branch
                    branchManager.head = NBtable.UseNameFindNBtable(args[1],branchManager.branches);
                    BranchManager.writeBM();
                    }
                break;
        }
    }

    public void cover(String Commit_id) throws IOException {
        if(Commit_id.isEmpty()){
            for (File file : fileWD.listFiles()) {
                file.delete();
            }
        }else {
            // CheckoutCommit -- other branch
            NBtable[] blobsInCOC = branchManager.FindCommit(Commit_id).NBCommit;
            //Result result = FileFromInitial(new NBtable[0],new NBtable[0],Commit_id);  DELETE
            //NBtable[] blobsInCOC = result.OTHER; DELETE

            // DefaultCommit == CurrentCommit (blobsInCC)  -- current branch
            NBtable[] blobsInDC = branchManager.FindCommit(branchManager.head.getSHA1Value()).NBCommit;
            //NBtable[] blobsInDC = result.HEAD; DELETE
            for (File file : fileWD.listFiles()) {
                if (!file.equals(fileG)) {
                    String tempFileName = file.getName();
                    //in checkout branch or not
                    //  in:
                    //      not in Current branch : failure case 3
                    //      has different content with the file in WD : overwrite
                    //      has the same content with the file in WD : pass
                    //  not in:
                    //      in Current branch : delete
                    Boolean NameinCOC;
                    if(blobsInCOC==null){NameinCOC = false;}
                    else {
                        NameinCOC = NBtable.FileNameinNBArray(tempFileName, blobsInCOC);
                    }
                    Boolean NameinCC = NBtable.FileNameinNBArray(tempFileName, blobsInDC);
                    if (NameinCOC) {
                        //failure case : in checkout branch && untracked in current branch
                        if (!NameinCC) {
                            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                            break;
                        }
                        //needs to be processed : the file in WD && in checkout branch
                        String SHAinWD = Utils.sha1(tempFileName, Utils.readContentsAsString(file));
                        String SHAinCOC = NBtable.FindSHAinNBArray(tempFileName, blobsInCOC);
                        // same name && different content
                        if (!SHAinWD.equals(SHAinCOC)) {
                            File blob = new File(fileB, SHAinCOC);
                            writeFile(file, Utils.readObject(blob, Blob.class).getcontent());
                        }
                    }
                    if (!NameinCOC && NameinCC) {
                        file.delete(); //tracked in current branch
                    }
                }
            }
            String[] compl_ = NBtable.complement(NBtable.NBtoString(blobsInCOC,"FullName"),fileWD.list());
            for(String name : compl_){
                String ID = NBtable.FindSHAinNBArray(name,blobsInCOC);
                Blob b = Utils.readObject(new File(fileB,ID),Blob.class);
                File file = new File(fileWD, b.getfilename());
                writeFile(file, b.getcontent());
            }
        }
        // clear staging area
        clearFolder(stagingAdd);
        clearFolder(stagingRem);
    }

    public void reset(String Commit_id) throws IOException {
        Boolean flag = false;
        for(String commitid : fileC.list()){
            if(Commit_id.equals(commitid)){
                flag = true;
                cover(Commit_id);
                // change current head
                branchManager.head.setSHA1Value(Commit_id);
                BranchManager.writeBM();
            }
        }
        if(flag == false){System.out.println("No commit with that id exists.");}
    }

    public void merge(String branchName) throws IOException {
        if(stagingAdd.list().length > 0 || stagingRem.list().length >0 ){
            System.out.println("You have uncommitted changes."); return;}// Failure case 1: staged additions or removals present
        if(!NBtable.FileNameinNBArray(branchName,branchManager.branches)){
            System.out.println("A branch with that name does not exist."); return;}// Failure case 2
        if(branchName.equals(branchManager.head.getFullName())){
            System.out.println("Cannot merge a branch with itself."); return;}// Failure case 3
        LimeFamily LimeTree = generateLimeTree(branchName); // generate a Id-pair tree
        LimeFamily.LimeTree node = LimeTree.SplitPoint();  // from left to right, the first leaf node with same ID in pair is the ancestor node
        Commit splitPoint = node.Parents_pair[0];
        //justify the position of split point
        String OTHER_SHA = NBtable.FindSHAinNBArray(branchName,branchManager.branches);
        Commit OTHER = BranchManager.FindCommit(OTHER_SHA);
        Commit HEAD = BranchManager.FindCommit(branchManager.head.getSHA1Value());
        if(splitPoint.Metadata[0].equals(OTHER.Metadata[0])){
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }// SP == OTHER
        if(splitPoint.Metadata[0].equals(HEAD.Metadata[0])){
            checkout(new String[]{"checkout",branchName});
            System.out.println("Current branch fast-forwarded.");
            return;
        } // SP == HEAD
        // two file lists(NBtable[]) along the path from sp to head and other should be generated
        //  base on lime tree , from SP along the path( constantly calling parent) till root(the latest Commits pair)
        //  OBS: All files are in the latest versions
        NBtable[] Files_SP = splitPoint.NBCommit;
        Boolean SPisNULL = true; // if Split Point is the initial commit
        NBtable[] Files_HEAD = HEAD.NBCommit;
        NBtable[] Files_OTHER = OTHER.NBCommit;
        /*NBtable[] Files_HEAD = new NBtable[0];   DELETE
        NBtable[] Files_OTHER = new NBtable[0];
        if(Files_SP != null){
            SPisNULL = false;
            int Length = splitPoint.NBCommit.length;
            Files_OTHER = new NBtable[Length]; System.arraycopy(Files_SP,0,Files_OTHER,0,Length);
            Files_HEAD = new NBtable[Length]; System.arraycopy(Files_SP,0,Files_HEAD,0,Length);
        }
        Result result;
        if(!SPisNULL){result = FileListsAlongPath(Files_HEAD,Files_OTHER,node,LimeTree);}else{result = FileFromInitial(Files_HEAD,Files_OTHER,OTHER_SHA);}
        Files_HEAD = result.HEAD; Files_OTHER = result.OTHER;   DELETE*/


        // Files Operations
        // case1 : File modified in Other but not modified in Current ---- checkout OTHER filename && add filename

        if(!SPisNULL){
            String[] inter_1 = NBtable.intersection(Files_OTHER,Files_HEAD,"FullName");
            String[] inter_0 = NBtable.intersection(inter_1,NBtable.NBtoString(Files_SP,"FullName"));
            for(String name : inter_0){
                if(!CompareID(name,Files_OTHER,Files_HEAD)){ //modified in OTHER
                    if(CompareID(name,Files_SP,Files_HEAD)){  // case1 not modified in HEAD
                        updateFile(name,OTHER_SHA,Files_OTHER,OTHER);
                    }else{ // modified in HEAD
                        if(!CompareID(name,Files_SP,Files_OTHER)){ // case 3 conflict
                            Conflict(name,Files_SP,Files_OTHER);
                            add(name);
                        }
                    }
                }
            }
        }
        else{
            String[] compl_1 = NBtable.complement(Files_OTHER,Files_HEAD,"FullName");
            for(String name : compl_1){
                updateFile(name,OTHER_SHA,Files_OTHER,OTHER);
            }
        } // if SP is empty, case 1. also check case 3
        // case3 : the file was absent at the split point and has different contents in the given and current branches. ---CONFLICT
        // case5 : File added in Other ---- checkout OTHER filename && add filename
        if(!SPisNULL) {
            String[] case_3_5 = NBtable.complement(Files_OTHER,Files_SP,"FullName");
            String[] files_head = NBtable.NBtoString(Files_HEAD,"FullName");
            String[] case_5 = NBtable.complement(case_3_5,files_head);
            String[] case_3_C = NBtable.intersection(case_3_5,files_head);
            for(String name : case_5){
                updateFile(name,OTHER_SHA,Files_OTHER,OTHER);
            }
            for(String name : case_3_C){
                if(!CompareID(name,Files_HEAD,Files_OTHER)){ // case 3 conflict
                    Conflict(name,Files_HEAD,Files_OTHER);
                    add(name);
                }
            }
        }// if SP is empty, then skip case 5 and case 3 in this step
        // case6 : File removed from Other and not changed in Current ---- REMOVE
        if(!SPisNULL){
            String[] case_6 = NBtable.complement(Files_SP,Files_OTHER,"FullName");
            for(String name: case_6){
                if (CompareID(name,Files_OTHER,Files_HEAD)) {
                    File file = new File(fileWD, name);
                    if(file.exists()){file.delete();}// removed and untracked
                }
            }
        }// if SP is empty, skip
        // automatically commit with the log message "Merged [given branch name] into [current branch name]"
        // records as parents both the head of CB (the first parent) and GB
        String message = "Merged " + branchName + " into " + branchManager.head.getFullName();
        try {
            commit(message, OTHER_SHA);
        }catch (Exception e){
            System.out.println("Encountered a merge conflict.");
        }
    }

    //help-functions
    public Commit createNewCommit(String arg,String SecondParent){
        Commit newCommit;
        String Firstparent = branchManager.head.getSHA1Value();
        // TODO: empty String  isEmpty or ==
        if(SecondParent.isEmpty()){
            newCommit = new Commit(Firstparent, arg, new NBtable[0]);
        }else{
            newCommit = new Commit(Firstparent, SecondParent, arg, new NBtable[0]);
        }
        return newCommit;
    }
    public static void writeFile(File oriented_file, String fileContent ) throws IOException {
        if(oriented_file.exists()){
            PrintWriter writer = new PrintWriter(oriented_file);
            writer.print("");
            writer.close();
        }else{ oriented_file.createNewFile(); }
        Utils.writeContents(oriented_file,fileContent);
    }
    public void clearFolder(File folder){ if(folder.list().length >= 1){ for(File file: folder.listFiles()){ file.delete(); } } }
    public void PrintCommit(Commit commit,String Sha){
        System.out.println("===");
        System.out.println("commit "+Sha);
        System.out.println("Date: "+ commit.Metadata[1]);
        System.out.println(commit.Metadata[0]);
        System.out.println();
    }
    public static void printString(String[] strings){
        Arrays.sort(strings);//lexicographic order
        for(String item : strings){ System.out.println(item); }
    }
    public static BranchManager.CommitTree MakeACT(String defaultbranch,String otherbranch) throws FileNotFoundException {
        return BranchManager.MakeACommitTree(defaultbranch,otherbranch,branchManager.branches);
    }
    public static LimeFamily generateLimeTree(String branchName) throws FileNotFoundException {
        BranchManager.CommitTree CT = Gitlet.MakeACT(branchManager.head.getFullName(),branchName);
        //String sha = NBtable.FindSHAinNBArray(branchName,branchManager.branches);
        LimeFamily Lime = new LimeFamily(new Commit[] {CT.branchHeads[0].self, CT.branchHeads[1].self});
        BranchManager.CommitTree.CommitTreeNode movedNode = CT.branchHeads[0];
        BranchManager.CommitTree.CommitTreeNode remainedNode = CT.branchHeads[1];
        generateLimeTreeHelper(movedNode,remainedNode,Lime.root,Lime,CT);
        return Lime;
    }
    private static void generateLimeTreeHelper(BranchManager.CommitTree.CommitTreeNode movedNode, BranchManager.CommitTree.CommitTreeNode remainedNode, LimeFamily.LimeTree node, LimeFamily Lime, BranchManager.CommitTree CT) throws FileNotFoundException {
        /*ancestor node*/
        if(movedNode.self.Metadata[0].equals(remainedNode.self.Metadata[0])){return;}
        /*leaf node (Commit 0)*/
        else if(movedNode.self.Metadata[0].equals(CT.root.self.Metadata[0])||remainedNode.self.Metadata[0].equals(CT.root.self.Metadata[0])){return;}
        /*recursion*/
        else{
            /* M1 : find the parents of the commit node from current branch first*/
            int i = 0;
            for(BranchManager.CommitTree.CommitTreeNode parent: movedNode.getParent()){
                Lime.addLeftChild(node,parent.self);
                generateLimeTreeHelper(parent,remainedNode,node.child(i),Lime,CT);
                i++;

            }
            /* M2 : find the parents of the commit node from merged branch later*/
            int j = i;
            for(BranchManager.CommitTree.CommitTreeNode parent: remainedNode.getParent()){
                Lime.addRightChild(node,parent.self);
                generateLimeTreeHelper(movedNode,parent,node.child(j),Lime,CT);
                j++;
            }
        }
    }
    private static Boolean CompareID(String fileName,NBtable[] n1,NBtable[] n2){
        String ID1 = NBtable.FindSHAinNBArray(fileName,n1);
        String ID2 = NBtable.FindSHAinNBArray(fileName,n2);
        // in both commit with same ID. if it equals to "Wrong", it doesn't exist in Commit
        return ID1==ID2 && ID1 != "Wrong";
    }
    private static void Conflict(String name,NBtable[] Other,NBtable[] Head) throws IOException {
        String content_HEAD = Utils.readObject(new File(fileB,NBtable.FindSHAinNBArray(name,Head)),Blob.class).content;
        String content_OTHER = Utils.readObject(new File(fileB,NBtable.FindSHAinNBArray(name,Other)),Blob.class).content;
        File Confilct_file = new File(fileWD,name);
        String content = "<<<<<<< HEAD"+"/n" +
                content_HEAD + "/n" +
                content_OTHER + "/n" +
                ">>>>>>>";
        writeFile(Confilct_file,content);
    }
    private Result FileListsAlongPath(NBtable[] HEAD_List, NBtable[] OTHER_List, LimeFamily.LimeTree node,LimeFamily tree){
        Result result = new Result(HEAD_List,OTHER_List);
        if(node.equals(tree.root)){return result;}
        if(!node.Parents_pair[0].Metadata[0].equals(node.parent.Parents_pair[0].Metadata[0])){
            HEAD_List = updateFileList(node.parent.Parents_pair[0].NBCommit,HEAD_List);
        } // update Current Branch
        else{
            OTHER_List = updateFileList(node.parent.Parents_pair[1].NBCommit,OTHER_List);
        } // update Given Branch
        return FileListsAlongPath(HEAD_List,OTHER_List,node.parent,tree);
    }
    private static NBtable[] updateFileList(NBtable[] parentList,NBtable[] dest_List){
        for(NBtable element : parentList){
            NBtable oldElement = NBtable.UseNameFindNBtable(element.getFullName(),dest_List);
            if(oldElement.getSHA1Value().length() > 5){ // in dest_List otherwise "noid".length() == 4
                oldElement.setSHA1Value(element.getSHA1Value());
            }else{
                dest_List = NBtable.update(dest_List,element);
            }
        }
        return dest_List;
    }
    private Result FileFromInitial(NBtable[] HEAD_List, NBtable[] OTHER_List,String branchID) throws FileNotFoundException {
        Commit CurrentCommit = branchManager.FindCommit(branchManager.head.getSHA1Value());
        HEAD_List = FileFromInitialHelper(CurrentCommit,HEAD_List);
        CurrentCommit = branchManager.FindCommit(branchID);
        if(CurrentCommit.Metadata[0].equals("initial commit")){
            OTHER_List = new NBtable[0];
        }else{
            OTHER_List = FileFromInitialHelper(CurrentCommit,OTHER_List);
        }
        return new Result(HEAD_List,OTHER_List);
    }
    private NBtable[] FileFromInitialHelper(Commit CurrentCommit,NBtable[] File_List) throws FileNotFoundException {
        File_List = updateFileList(CurrentCommit.NBCommit,File_List);
        while(!CurrentCommit.Metadata[0].equals("initial commit")){  // Commit0.paSHA = null
            Commit parentCommit = branchManager.ParentCommit(CurrentCommit);
            File_List = updateFileList(CurrentCommit.NBCommit,File_List);
            CurrentCommit = parentCommit;
        }
        return File_List;
    }
    private void updateFile(String name,String destID,NBtable[] srcList,Commit srcCommit) throws IOException {
        if(NBtable.FileNameinNBArray(name,srcCommit.NBCommit)){
            checkout(new String[]{"checkout",destID,"--",name});
        }else{ // file in given branch but not in the latest commit
            File file = new File(fileWD,name);
            String blobID = NBtable.FindSHAinNBArray(name,srcList);
            Blob fileBlob = Utils.readObject(new File(fileB,blobID),Blob.class);
            writeFile(file,fileBlob.getcontent());
        }
        add(name);
    }
    //test-functions
    public void numOfBranch() throws FileNotFoundException {
        for(NBtable branch :branchManager.branches){
            System.out.println(branch.getFullName());
            File fileTemp = new File(fileC,branch.getSHA1Value());
            Commit commit = Utils.readObject(fileTemp,Commit.class);
            System.out.println("Blobs:");
            for(NBtable blob:commit.NBCommit){
                if(blob == null){
                    continue;
                }
                System.out.println(blob.getFullName());
                //System.out.println(blob.getSHA1Value());
            }
            System.out.println("*************************");
        }
        for(NBtable bran :branchManager.branches ){
            System.out.println(bran.getFullName());
            //System.out.println(bran.getSHA1Value());
        }

    }
    public void printSet(Set<String> sets){
        printString(NBtable.SetToString(sets));
    }
    class Result{
        NBtable[] HEAD;
        NBtable[] OTHER;

        public Result(NBtable[] a,NBtable[] b){
            HEAD = a;
            OTHER = b;
        }
    }
}
