package structuralAnalysis;


import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by neilwalkinshaw on 24/10/2017.
 */
public class ClassDiagram {

    Set<String> inheritanceRelations;
    Set<String> associationRelations;

    /**
     * Given a package name and a directory returns all classes within that directory
     * @param directory
     * @param pkgname
     * @return Classes within Directory with package name
     */
    public static List<Class<?>> processDirectory(File directory, String pkgname) {

        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        String prefix = pkgname+".";
        if(pkgname.equals(""))
            prefix = "";

        // Get the list of the files contained in the package
        String[] files = directory.list();
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i];
            String className = null;

            // we are only interested in .class files
            if (fileName.endsWith(".class")) {
                // removes the .class extension
                className = prefix+fileName.substring(0, fileName.length() - 6);
            }


            if (className != null) {
                classes.add(loadClass(className));
            }

            //If the file is a directory recursively class this method.
            File subdir = new File(directory, fileName);
            if (subdir.isDirectory()) {

                classes.addAll(processDirectory(subdir, prefix + fileName));
            }
        }
        return classes;
    }

    private static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException("Unexpected ClassNotFoundException loading class '" + className + "'");
        }
    }



    public ClassDiagram(String root){
        inheritanceRelations = new HashSet<String>();
        associationRelations = new HashSet<String>();
        File dir = new File(root);
        List<Class<?>> classes = processDirectory(dir,"");

        for(Class cls : classes){
            inheritanceRelations.add("\""+cls.getName()+"\"" + "->" + "\""+cls.getSuperclass().getName()+"\"");
            for(Field f : cls.getDeclaredFields()){
                associationRelations.add("\""+cls.getName() +"\""+ "->" +"\""+ f.getType().getName()+"\"");
            }
        }
        visualise();
    }

    public void visualise(){
        StringBuffer dotGraph = new StringBuffer();
        dotGraph.append("digraph{\n");
        for(String inh : inheritanceRelations){
            dotGraph.append(inh+" [arrowhead=onormal];\n");
        }
        for(String inh : associationRelations){
            dotGraph.append(inh+" [arrowhead=diamond];\n");
        }
        dotGraph.append("}");
        System.out.println(dotGraph.toString());
    }

}
