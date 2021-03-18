package multilabel2;

import SwePub.HsvCodeToName;
import com.koloboke.collect.map.IntIntCursor;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;
import jsat.linear.SparseVector;

import java.io.*;
import java.util.*;

public class Index implements Serializable {

    private static final long serialVersionUID = -473737364433488888L;

    private static int counter = 0;
    private HashMap<String,Integer> index;

    private IntIntMap IDF = HashIntIntMaps.getDefaultFactory().withDefaultValue(0).newMutableMap(); //bug default value

    public Index(int n) { index = new HashMap<>(n);}
    public Index() {}


    public void addTokens( List<String> tokens ) {

        Set<String> seenBeforeForCurrentRecord = new HashSet<>(50);


        for(String t : tokens) {

            if("TS@årsrapport".equals(t)) System.out.println("TS@årsrapport!!");

            Integer indice = this.index.get(t);

            if (indice == null) {

                this.index.put(t, counter);
                this.IDF.addValue(counter,1);
                counter++;
            } else {

            if(!seenBeforeForCurrentRecord.contains(t)) this.IDF.addValue(counter,1);

            }

            seenBeforeForCurrentRecord.add(t);

        }


    }


    public void removeRareTerms(int minFreq) {


        for(IntIntCursor cur = IDF.cursor(); cur.moveNext();) {

            int index = cur.key();
            int freq = cur.value();

         //   System.out.println("index: " + index + " IDF: " + freq    );

        }




    }


    public Integer getIndice(String t) {

        return this.index.get(t);

    }

    public void save(String file) {

        try
        {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this.index);
            oos.close();
            fos.close();
            System.out.println("Serialized Index (HashMap) is saved in " + file);
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
        }


    }

    public void load(String file) {

        try
        {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            this.index = (HashMap) ois.readObject();
            ois.close();
            fis.close();
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
            return;
        }catch(ClassNotFoundException c)
        {
            System.out.println("Class not found");
            c.printStackTrace();
            return;
        }

        System.out.println("Deserialized Index (HashMap), mappings: " + this.index.size() );
        this.counter = this.index.size()-1;

    }

    public int size() {

        return this.index.size();
    }


    public String reverseLookupSlow(int indice) {

        for(Map.Entry<String,Integer> entry : this.index.entrySet()) {

            if(entry.getValue().equals(indice)) return  entry.getKey();

        }



        return null;

    }


    public SparseVector getSparseVector(List<String> stringTokens, boolean L2normalize) {

        HashSet<String> tokens = new HashSet( stringTokens );
        SparseVector sparseVector = new SparseVector(this.index.size(), tokens.size());

        for (String s : tokens) {

            Integer ind = this.getIndice(s);
            if (ind != null) sparseVector.set(ind, 1d);

        }

        if(L2normalize) {
            sparseVector.normalize();
        }
        return sparseVector;
    }

    public SparseVector getSparseVector(Set<String> stringTokens, boolean L2normalize) {


        SparseVector sparseVector = new SparseVector(this.index.size(), stringTokens.size());

        for (String s : stringTokens) {

            Integer ind = this.getIndice(s);
            if (ind != null) sparseVector.set(ind, 1d);

        }

        if(L2normalize) sparseVector.normalize();
        return sparseVector;
    }





}
