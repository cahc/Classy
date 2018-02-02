package scopus;

import java.io.*;
import java.util.List;

/**
 * Created by crco0001 on 1/26/2018.
 */
public class SimplePersistor {



    public static void serialiseList(List list, String filename) throws IOException {

        ObjectOutputStream oos = null;
        FileOutputStream fout = null;
        try{
            fout = new FileOutputStream(filename, false);
            oos = new ObjectOutputStream(fout);
            oos.writeObject(list);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if(oos != null){
                oos.close();
            }
        }


    }


    public static List<ScopusRecord> deserializeList(String filename) throws IOException {

        ObjectInputStream objectinputstream = null;
        List<ScopusRecord> readRecords = null;

        try {
            FileInputStream streamIn = new FileInputStream(filename);
            objectinputstream = new ObjectInputStream(streamIn);
           readRecords = (List<ScopusRecord>) objectinputstream.readObject();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(objectinputstream != null){
                objectinputstream .close();
            }
        }



        return readRecords;

    }





}
