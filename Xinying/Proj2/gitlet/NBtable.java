package gitlet;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

// a specialized data structure for storing filename and its hashcode generated by SHA1
public class NBtable implements Serializable {

    private String FullName;
    private String SHA1Value;
    private static final long serialVersionUID = -2700177984038074721L;

    public NBtable(String Fullname, String SHA1value){
        FullName = Fullname;
        SHA1Value = SHA1value;
    }

    public Boolean findSHA(String SHA1){
        if(SHA1Value.equals(SHA1)){ return true;
        }else{ return false; }
    }

    public Boolean findName(String filename){
        if(FullName.equals(filename)){ return true;
        }else{ return false; }
    }

    public static Boolean FileNameinNBArray(String filename, NBtable[] NBArray){  // find blob commit
        for(NBtable NB : NBArray){ if(NB.findName(filename)){ return true; } }
        return false;
    }

    public static Boolean SHAinNBArray(String Sha1, NBtable[] NBArray){  // find blob commit
        for(NBtable NB : NBArray){ if(NB.findSHA(Sha1)){ return true; } }
        return false;
    }

    public static String FindFileNameinNBArray(String Sha1, NBtable[] NBArray){  // find blob commit
        for(NBtable NB : NBArray){
            if(NB.findSHA(Sha1)){
                return NB.getFullName();
            }
        }
        System.out.println("Wrong SHA1!");
        return NBArray[0].getFullName();
    }

    public static String FindSHAinNBArray(String filename, NBtable[] NBArray){  // find blob commit
        for(NBtable NB : NBArray){
            if(NB.findName(filename)){
                return NB.getSHA1Value();
            }
        }
        System.out.println("Wrong finename!");
        return NBArray[0].getSHA1Value();
    }

    //Flag should be 'FullName' or 'SHA1Value'
    public static String[] NBtoString(NBtable[] NBtables,String flag){
        String[] strings = new String[NBtables.length];
        for(int i=0;i<NBtables.length;i++){
            switch(flag){
                case "FullName":
                    strings[i] = NBtables[i].getFullName();
                    break;
                case "SHA1Value":
                    strings[i] = NBtables[i].getSHA1Value();
                    break;
                default:
                    System.out.println("Flag should be 'FullName' or 'SHA1Value'");
            }
        }
        return strings;
    }

    // 补集 complement
    // in nb1 && not in nb2
    public static String[] complement(NBtable[] nb1, NBtable[] nb2,String flag){ return StringtoSet(NBtoString(nb1,flag),NBtoString(nb2,flag),"complement"); }
    public static String[] complement(String[] str1, String[] str2){ return StringtoSet(str1,str2,"complement"); }

    public static String[] union(String[] str1, String[] str2){
        return StringtoSet(str1,str2,"union");
    }

    public static  String[] intersection(NBtable[] nb1, NBtable[] nb2,String flag){ return StringtoSet(NBtoString(nb1,flag),NBtoString(nb2,flag),"intersection"); }
    public static  String[] intersection(String[] str1, String[] str2){ return StringtoSet(str1,str2,"intersection"); }

    public static String[] StringtoSet(String[] str1, String[] str2, String flag){
        Set<String> set1 = new HashSet<String>();
        Set<String> set2 = new HashSet<String>();
        for (String num : str1) {
            set1.add(num);
        }
        for (String num : str2) {
            set2.add(num);
        }
        switch (flag){
            case "complement":  // in set1 && not in set2
                set1.removeAll(set2);
                break;
            case "union":
                set1.addAll(set2);
                break;
            case "intersection":
                set1.retainAll(set2);
        }
        return SetToString(set1);
    }

    public static String[] SetToString(Set<String> set1){
        String[] s = new String[set1.size()];
        int index = 0;
        for (String num : set1) {
            s[index++] = num;
        }
        return s;
    }


    public String getFullName() { return FullName; }
    public String getSHA1Value() { return SHA1Value; }

    public void setSHA1Value(String newSHA) { this.SHA1Value = newSHA; }
    public void setFullName(String fullName) { this.FullName = fullName; }
}