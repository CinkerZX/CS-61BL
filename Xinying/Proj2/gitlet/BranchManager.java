package gitlet;

import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;


public class BranchManager implements Serializable {
    public NBtable[] branches;
    public NBtable head;
    private static final long serialVersionUID = -586838958655118021L;

    public BranchManager(String sha1Commit0){
        NBtable master = new NBtable("master", sha1Commit0);
        NBtable[] b = new NBtable[] {master};
        branches = b;
        head = master;
    }

    public void update_branch(String newSHA){
        for (NBtable branch : branches){
            if(branch.findSHA(head.getSHA1Value())){
                branch.setSHA1Value(newSHA);
                break;
            }
        }
        head.setSHA1Value(newSHA);
    }
    public static void writeBM() throws IOException {
        if(Gitlet.fileBM.exists()){
            PrintWriter writer = new PrintWriter(Gitlet.fileBM);
            writer.print("");
            writer.close();
        }else{
            Gitlet.fileBM.createNewFile();
        }
        Utils.writeObject(Gitlet.fileBM,Gitlet.branchManager);
    }
    // copy current commit and set it as the parent commit
    public Commit NewCommit(String message) throws FileNotFoundException {return new Commit(head.getSHA1Value(),message,new NBtable[0]); }
    public static Commit FindCommit(String SHA1Value) throws FileNotFoundException{
        if (SHA1Value== null){
            return null;
        }
        File file = new File(Gitlet.fileC,SHA1Value);
        return Utils.readObject(file, Commit.class);
    }
    public Boolean inCurrentCommit(String filename) throws FileNotFoundException { return NBtable.FileNameinNBArray(filename, FindCommit(head.getSHA1Value()).NBCommit); }
    public static Boolean inCommit(String filename, String commitID) throws FileNotFoundException{ return NBtable.FileNameinNBArray(filename, FindCommit(commitID).NBCommit); }
    public static Commit ParentCommit(Commit CurrentCommit) throws FileNotFoundException { return FindCommit(CurrentCommit.getPaSHA()[0]); }
    public static Boolean InPastedCommit(String filename) throws FileNotFoundException { return InPastedCommitHelper(filename, Gitlet.branchManager.head.getSHA1Value()); }
    public static Boolean InPastedCommitHelper(String filename, String commitID) throws FileNotFoundException {
        if(FindCommit(commitID).getPaSHA()[0].length()<2){
            return false;
        }else if(inCommit(filename,commitID)){
            return true;
        }else{
            return InPastedCommitHelper(filename, FindCommit(commitID).getPaSHA()[0]);
        }
    }

    public static CommitTree MakeACommitTree(String branchname1,String branchname2,NBtable[] branches) throws FileNotFoundException {
        //root & ends
        CommitTree commitTree = new CommitTree(new Commit(),new Commit[]{FindCommit(NBtable.FindSHAinNBArray(branchname1,branches)),FindCommit(NBtable.FindSHAinNBArray(branchname2,branches))});
        //fill in the whole tree
        for(CommitTree.CommitTreeNode head:commitTree.branchHeads){
            commitTree.enlargeTree(head, head.self);
        }
        return commitTree;
    }
    public static class CommitTree{
        public CommitTreeNode root;
        public CommitTreeNode[] branchHeads;

        public CommitTree(Commit root){
            this.root = new CommitTreeNode(root);
        }
        public CommitTree(Commit root,Commit[] branchheads){
            this.root = new CommitTreeNode(root);
            branchHeads = new CommitTreeNode[]{new CommitTreeNode(branchheads[0]),new CommitTreeNode(branchheads[1])};
        }
        public void enlargeTree(CommitTreeNode node,Commit nodeCommit) throws FileNotFoundException{
            node.enlargeParent();
            for(CommitTreeNode parent : node.parents){
                if(parent.self.Metadata[0].equals(this.root.self.Metadata[0])){
                    this.root.children.add(node);
                }
                enlargeTreeHelper(parent,nodeCommit);
            }
        }
        public void enlargeTreeHelper(CommitTreeNode node,Commit nodeCommit) throws FileNotFoundException{
            if(nodeCommit.equals(this.root)){

            }else{
                node.enlargeParent();
                for(CommitTreeNode parent : node.parents){
                    if(parent.self.Metadata[0].equals(this.root.self.Metadata[0])){
                        this.root.children.add(node);
                    }
                    enlargeTreeHelper(parent,nodeCommit);
                }
                node.enlargeChildren(nodeCommit);
            }
        }

        public static class CommitTreeNode{
            public Commit self;
            private ArrayList<CommitTree.CommitTreeNode> parents;
            private ArrayList<CommitTree.CommitTreeNode> children;

            public CommitTreeNode(Commit node){
                self = node;
                children = new ArrayList<CommitTree.CommitTreeNode>();
            }

            public ArrayList<CommitTree.CommitTreeNode> getParent() {
                return parents;
            }
            public ArrayList<CommitTree.CommitTreeNode> getChildren() {
                return children;
            }
            public int arity(){ return children.size();}
            public CommitTree.CommitTreeNode child(int k){ return children.get(k);}

            public void enlargeParent() throws FileNotFoundException{
                parents = new ArrayList<CommitTree.CommitTreeNode>();
                for(String paSHA : self.getPaSHA()){
                    if(!paSHA.isEmpty()){
                        parents.add(new CommitTreeNode(FindCommit(self.getPaSHA()[0])));
                    }
                }
            }
            public void enlargeChildren(Commit child) throws FileNotFoundException{
                children = new ArrayList<CommitTree.CommitTreeNode>();
                children.add(new CommitTreeNode(child));
            }
        }

        public void print() {
            System.out.print(printHelper(root));
        }
        private String printHelper(CommitTree.CommitTreeNode node){
            if(node.arity() == 0) return printNode(node);
            else{
                String R;
                R = printNode(node);
                for(int i = 0;i < node.arity();i +=1){
                    R += "    " + "/n"+ printHelper(node.child(i));
                    return R ;
                }
            }
            return null;
        }
        private String printNode(CommitTree.CommitTreeNode node){
            return "{"+ node.self.Metadata[0] + "}";
        }
    }
}
