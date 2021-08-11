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
            NBtable[] newbloblist = newCommit.NBCommit;

            // staged for addition
            for (String name : stagingAdd.list()) {
                File file = new File(fileB, name);  // use the filename stored in staging area locate blob in Blobs directory
                File file1 = new File(stagingAdd,name);  // but delete the file stored in staging area instead of Blobs directory
                Blob addition = Utils.readObject(file, Blob.class);
                newbloblist = NBtable.add(newbloblist, new NBtable(addition.getfilename(), name));
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
            branchManager.branches = NBtable.add(branchManager.branches,new NBtable(branchName,""));
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
            // CheckoutCommit
            NBtable[] blobsInCOC = branchManager.FindCommit(Commit_id).NBCommit;
            // DefaultCommit == CurrentCommit (blobsInCC)
            NBtable[] blobsInDC = branchManager.FindCommit(branchManager.head.getSHA1Value()).NBCommit;
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
                    if(blobsInCOC==null){NameinCOC = false;}else {NameinCOC = NBtable.FileNameinNBArray(tempFileName, blobsInCOC);}
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
                    } else if (NameinCC) {
                        file.delete(); //tracked in current branch
                    }
                }
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
        if(stagingAdd.list().length > 0 || stagingRem.list().length >0 ){ // Failure case 1: staged additions or removals present
            System.out.println("You have uncommitted changes.");
        }else if(!NBtable.FileNameinNBArray(branchName,branchManager.branches)){ // Failure case 2
            System.out.println("A branch with that name does not exist.");
        }else if(branchName.equals(branchManager.head.getFullName())){
            System.out.println("Cannot merge a branch with itself."); // Failure case 3
        }else{
            // generate a Id-pair tree
            LimeFamily LimeTree = generateLimeTree(branchName);
            // from left to right, the first leaf node with same ID in pair is the ancestor node
            Commit splitPoint = LimeTree.SplitPoint();
            //TODO: justify the position of split point

            // files operations
            String OTHER_SHA = NBtable.FindSHAinNBArray(branchName,branchManager.branches);
            Commit OTHER = BranchManager.FindCommit(OTHER_SHA);
            Commit HEAD = BranchManager.FindCommit(branchManager.head.getSHA1Value());
            // TODO: two filelists(NBtable[]) along the path from sp to head and other should be generate
            //  base on lime tree , from SP along the path( constantly calling parent) till root(latest Commits pair)
            //  OBS: keep the latest version of files


            // case1 : File modified in Other but not modified in Current ---- checkout OTHER filename && add filename
            // case3 : modified in different way ---- CONFLICT
            String[] inter_1 = NBtable.intersection(OTHER.NBCommit,HEAD.NBCommit,"FullName");
            String[] inter_0 = NBtable.intersection(inter_1,NBtable.NBtoString(splitPoint.NBCommit,"FullName"));
            for(String name : inter_0){
                if(CompareID(name,OTHER,HEAD)==false){ //modified in OTHER
                    if(CompareID(name,splitPoint,HEAD)==true){  // case1 not modified in HEAD
                        checkout(new String[]{"checkout",OTHER_SHA,"--",name});
                        add(name);
                    }else{ // modified in HEAD
                        if(CompareID(name,splitPoint,OTHER)==false){ // case 3 conflict
                            //TODO
                            Conflict();
                            return;
                        }
                    }
                }
            }
            // TODO : the file was absent at the split point and has different contents in the given and current branches. ---CONFLICT

            // case5 : File added in Other ---- checkout OTHER filename && add filename
            String[] case_5 = NBtable.complement(OTHER.NBCommit,splitPoint.NBCommit,"FullName");
            for(String name: case_5){
                checkout(new String[]{"checkout",OTHER_SHA,"--",name});
                add(name);
            }
            // case6 : File removed from Other and not changed in Current ---- REMOVE
            String[] case_6 = NBtable.complement(splitPoint.NBCommit,OTHER.NBCommit,"FullName");
            for(String name: case_6){
                if (CompareID(name,OTHER,HEAD)==true) {
                    File file = new File(fileWD, name);
                    if(file.exists()){file.delete();}// removed and untracked
                }
            }
            // automatically commit with the log message "Merged [given branch name] into [current branch name]"
            // records as parents both the head of CB (the first parent) and GB
            String message = "Merged " + branchName + " into " + branchManager.head.getFullName();
            try {
                commit(message, OTHER_SHA);
            }catch (Exception e){
                System.out.println("Encountered a merge conflict.");
            }

            //TODO:  now we have finish merge function, next will be test
        }
    }

    //help-functions
    public Commit createNewCommit(String arg,String SecondParent){
        Commit newCommit;
        String Firstparent = branchManager.head.getSHA1Value();
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
    private static Boolean CompareID(String fileName,Commit c1,Commit c2){
        String ID1 = NBtable.FindSHAinNBArray(fileName,c1.NBCommit);
        String ID2 = NBtable.FindSHAinNBArray(fileName,c2.NBCommit);
        // in both commit with same ID. if it equals to "Wrong", it doesn't exist in Commit
        return ID1==ID2 && ID1 != "Wrong";
    }
    private static void Conflict(String name,Commit Other,Commit HEAD) throws IOException {
        String content_HEAD = Utils.readContentsAsString(new File(fileB,NBtable.FindSHAinNBArray(name,HEAD.NBCommit)));
        String content_OTHER = Utils.readContentsAsString(new File(fileB,NBtable.FindSHAinNBArray(name,OTHER.NBCommit)));
        File Confilct_file = new File(fileWD,name);
        String content = "<<<<<<< HEAD"+"/n" +
                content_HEAD + "/n" +
                content_OTHER + "/n" +
                ">>>>>>>";
        writeFile(Confilct_file,content);
    }
    //test-functions
    public void numOfBranch() throws FileNotFoundException {
        for(NBtable branch :branchManager.branches){
            System.out.println(branch.getFullName());
            File fileTemp = new File(fileC,branch.getSHA1Value());
            Commit commit = Utils.readObject(fileTemp,Commit.class);
            System.out.println("parent:");
            System.out.println(commit.getPaSHA()[0]);
            System.out.println("Blobs:");
            for(NBtable blob:commit.NBCommit){
                System.out.println(blob.getFullName());
                System.out.println(blob.getSHA1Value());
            }
            System.out.println("*************************");
        }
    }
/*    public static void MakeACommitTree() throws FileNotFoundException {
        BranchManager.CommitTree CT = Gitlet.MakeACT("master","main");
        CT.print();
    }*/
    public void printSet(Set<String> sets){
        printString(NBtable.SetToString(sets));
    }

}