package gitlet;

import org.junit.Assert;
import ucb.junit.textui;
import org.junit.Test;
import static org.junit.Assert.*;

/** The suite of all JUnit tests for the gitlet package.
 *  @author
 */
public class UnitTest {

    /** Run the JUnit tests in the loa package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        //System.exit(textui.runClasses(UnitTest.class));
    }

    /** LimeTree test */
    @Test
    public void PrintLimeTreeTest() {

        LimeFamily family = new LimeFamily(new String[]{"0","4"});
        family.addLeftChild(family.root,"1");
        family.addLeftChild(family.root,"3");
        family.addRightChild(family.root,"3");
        family.addLeftChild(family.root.child(0),"2");
        family.addRightChild(family.root.child(0),"3");

        System.out.println("Here's the family:");
        //family.print();
        family.printDFS();
        family.printBFS();

    }

    @Test
    public void AddLimeTreeTest() {
        String[] root = new String[]{"0","4"};
        String[] Lchild1 = new String[]{"1","4"};
        String[] Lchild2 = new String[]{"3","4"};
        String[] Rchild1 = new String[]{"0","3"};
        String[] L2child1 = new String[]{"2","4"};
        String[] R2child1 = new String[]{"1","3"};


        LimeFamily family = new LimeFamily(root);

        assertArrayEquals(root,family.root.PaSHA_pair);

        family.addLeftChild(family.root,"1");
        family.addLeftChild(family.root,"3");
        family.addRightChild(family.root,"3");

        assertArrayEquals(Lchild1,family.root.child(0).PaSHA_pair);
        assertArrayEquals(Lchild2,family.root.child(1).PaSHA_pair);
        assertArrayEquals(Rchild1,family.root.child(2).PaSHA_pair);

        family.addLeftChild(family.root.child(0),"2");
        family.addRightChild(family.root.child(0),"3");

        assertArrayEquals(L2child1,family.root.child(0).child(0).PaSHA_pair);
        assertArrayEquals(R2child1,family.root.child(0).child(1).PaSHA_pair);

    }

    @Test
    public void GenerateLimeTreeTest() {
        String[] root = new String[]{"0","4"};
        String[] Lchild1 = new String[]{"1","4"};
        String[] Lchild2 = new String[]{"3","4"};
        String[] Rchild1 = new String[]{"0","3"};
        String[] L2child1 = new String[]{"2","4"};
        String[] R2child1 = new String[]{"1","3"};


        LimeFamily family = new LimeFamily(root);

        assertArrayEquals(root,family.root.PaSHA_pair);

        family.addLeftChild(family.root,"1");
        family.addLeftChild(family.root,"3");
        family.addRightChild(family.root,"3");

        assertArrayEquals(Lchild1,family.root.child(0).PaSHA_pair);
        assertArrayEquals(Lchild2,family.root.child(1).PaSHA_pair);
        assertArrayEquals(Rchild1,family.root.child(2).PaSHA_pair);

        family.addLeftChild(family.root.child(0),"2");
        family.addRightChild(family.root.child(0),"3");

        assertArrayEquals(L2child1,family.root.child(0).child(0).PaSHA_pair);
        assertArrayEquals(R2child1,family.root.child(0).child(1).PaSHA_pair);

    }

    /** CommitTreeTest*/
    @Test
    public void GenerateCommitTreeTest(){

    }

}


