package Database;

import de.bwaldvogel.liblinear.FeatureNode;

import java.io.*;
import java.util.List;
import java.util.Set;

public class SimpleSerializer {

    public static void serialiseList(List list, String filename) throws IOException {

        ObjectOutputStream oos = null;
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(filename, false);
            oos = new ObjectOutputStream(fout);
            oos.writeObject(list);
            oos.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (oos != null) {
                oos.close();
            }
        }


    }


    public static List< List<FeatureNode>> deserializeFeatureNodeList(String filename) throws IOException {

        ObjectInputStream objectinputstream = null;
        List<List<FeatureNode>> readFeatureNodeLists = null;

        try {
            FileInputStream streamIn = new FileInputStream(filename);
            objectinputstream = new ObjectInputStream(streamIn);
            readFeatureNodeLists = (List<List<FeatureNode>>) objectinputstream.readObject();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (objectinputstream != null) {
                objectinputstream.close();
            }
        }


        return readFeatureNodeLists;

    }

    public static List<Set<Integer>> deserializeLabelList(String filename) throws IOException {

        ObjectInputStream objectinputstream = null;
        List<Set<Integer>> readLabelList = null;

        try {
            FileInputStream streamIn = new FileInputStream(filename);
            objectinputstream = new ObjectInputStream(streamIn);
            readLabelList = (List<Set<Integer>>) objectinputstream.readObject();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (objectinputstream != null) {
                objectinputstream.close();
            }
        }


        return readLabelList;

    }






}
