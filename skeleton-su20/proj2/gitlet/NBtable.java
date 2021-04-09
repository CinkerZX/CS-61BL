package gitlet;

//* attributes
//        - The full name of the file
//        - The sha1(Blobs) of the file
//        * function
//        - find: input = SHA1; output = true/false

import com.sun.media.sound.RIFFInvalidDataException;
import com.sun.xml.internal.bind.v2.runtime.output.StAXExStreamWriterOutput;
import org.apache.commons.lang3.ArrayUtils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class NBtable implements Serializable {
    //attributes
    private String file_name;
    private String sha1_file_name;
    private static final long serialVersionUID = 1433824995074707548L;

    //Constructor
    public NBtable(){
        file_name = "";
        sha1_file_name = "";
    }

    //Constructor
    public NBtable(String name, String sha1){
        file_name = name;
        sha1_file_name = sha1;
    }

    // function find by file name
    public boolean find(String name){
        if (file_name.equals(name)){
            return(true);
        }
        else
            return(false);
    }

    // function find by sha1
    public boolean find_sha1(String sha1){
        if (sha1_file_name.equals(sha1)){
            return(true);
        }
        else
            return(false);
    }

    public void setSha1_file_name(String sha1){
        sha1_file_name = sha1;
    }

    public String getSha1_file_name() {
        return (sha1_file_name);
    }

    public String getFile_name(){
        return (file_name);
    }

    // Return all the names in the form of array from a NBtable array
    public static String[] getFile_name_array(NBtable[] table_array){
        int n = table_array.length;
        String[] name_array = new String[n];
        int i = 0;
        for(NBtable a : table_array){
            name_array[i++] = a.getFile_name();
        }
        return name_array;
    }

    // Return all the hashes of bolds in the form of array from a NBtable array
    public static String[] getHash_name_array(NBtable[] table_array){
        int n = table_array.length;
        String[] name_array = new String[n];
        int i = 0;
        for(NBtable a : table_array){
            name_array[i++] = a.getSha1_file_name();
        }
        return name_array;
    }

    // Transfer the NBtable array into hasSet array, and use the getCompliment function
    // tableArray1 \ tableArray2
    public static String[] get_names_Compliment(NBtable[] tableArray1, NBtable[] tableArray2){
        String[] name_array_1 = getFile_name_array(tableArray1);
        String[] name_array_2 = getFile_name_array(tableArray2);
        return get_names_Compliment(name_array_1, name_array_2);
    }

    //stringArray1 - stringArray2
    public static String[] get_names_Compliment(String[] stringArray1, String[] stringArray2){ // Method overload
        Set<String> set1 = new HashSet<String>();
        Set<String> set2 = new HashSet<String>();
        for(String s : stringArray1){
            set1.add(s);
        }
        for(String s : stringArray2){
            set2.add(s);
        }
        return getCompliment(set1, set2);
    }

    public static String[] getCompliment(Set<String> file_names_current_dir, Set<String> file_names_commit){
        file_names_current_dir.removeAll(file_names_commit);
        String[] names_compliment = new String[file_names_current_dir.size()];
        int i = 0;
        for (String s : file_names_current_dir){
            names_compliment[i++] = s;
        }
        return names_compliment;
    }

    // Transfer the string array into hasSet array, and then get the union of them
    public static String[] get_String_Array_Union(String[] str1, String[] str2){
        Set<String> set1 = new HashSet<String>();
        Set<String> set2 = new HashSet<String>();
        for(String s : str1){
            set1.add(s);
        }
        for(String s : str2){
            set2.add(s);
        }
        set1.addAll(set2);
        String[] names_union = new String[set1.size()];
        int i = 0;
        for (String s : set1){
            names_union[i++] = s;
        }
        return names_union;
    }

    // Intersection of names
    // Get the array of file_names(bold_hash)
    public static String[] get_string_Array(NBtable[] tableArray1, NBtable[] tableArray2, String arg){
        String[] name_array_1;
        String[] name_array_2;
        if(arg.equals("name")){
            name_array_1 = getFile_name_array(tableArray1);
            name_array_2 = getFile_name_array(tableArray2);
        }
        else{
            name_array_1 = getHash_name_array(tableArray1);
            name_array_2 = getHash_name_array(tableArray2);
        }
        return get_Names_Intersection(name_array_1, name_array_2, tableArray1, arg);
    }

    // Return the intersected name array
    public static String[] get_Names_Intersection(String[] strArray1, String[] strArray2, NBtable[] table, String arg){
        Set<String> set1 = new HashSet<String>();
        Set<String> set2 = new HashSet<String>();
        for(String s : strArray1){
            set1.add(s);
        }
        for(String s : strArray2){
            set2.add(s);
        }
        // if by name, then can use getIntersection function directly
        if(arg.equals("name")){
            return getIntersection(set1, set2);
        }
        // else, need to find the file name in the NBtable (any table is ok, as it is intersection) by the bold_hash
        else {
            String[] bold_name_array = get_simple_String_Intersection(strArray1, strArray2);
            String[] names_intersection = new String[bold_name_array.length];
            int i = 0;
            for (NBtable t : table) {
                for (String s : bold_name_array) {
                    if (t.find_sha1(s)) {
                        names_intersection[i++] = t.getFile_name();
                    }
                }
            }
            return names_intersection;
        }
    }

    public static String[] get_simple_String_Intersection(String[] strArray1, String[] strArray2){
        Set<String> set1 = new HashSet<String>();
        Set<String> set2 = new HashSet<String>();
        for(String s : strArray1){
            set1.add(s);
        }
        for(String s : strArray2){
            set2.add(s);
        }
        // if by name, then can use getIntersection function directly
        return getIntersection(set1, set2);
    }

    public static String[] getIntersection(Set<String> file_names_current_dir, Set<String> file_names_commit){
        file_names_current_dir.retainAll(file_names_commit);
        String[] names_intersection = new String[file_names_current_dir.size()];
        int i = 0;
        for (String s : file_names_current_dir){
            names_intersection[i++] = s;
        }
        return names_intersection;
    }
}
