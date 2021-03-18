package multilabel2;

import SwePub.Record;
import com.koloboke.collect.map.IntIntCursor;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.ObjIntCursor;
import com.koloboke.collect.map.hash.HashIntIntMaps;
import com.koloboke.collect.map.hash.HashObjIntMap;
import com.koloboke.collect.map.hash.HashObjIntMaps;
import jsat.linear.SparseVector;
import org.h2.mvstore.DataUtils;

import java.io.*;
import java.util.*;

public class Index implements Serializable {

    private static final long serialVersionUID = -473737364433488888L;

    private static int counter = 0;
    private HashObjIntMap<String> index = HashObjIntMaps.getDefaultFactory().withDefaultValue(-1).newMutableMap(10000);
    private IntIntMap IDF = HashIntIntMaps.getDefaultFactory().withDefaultValue(0).newMutableMap(10000);

    public Index() {}


    public boolean addRecord(Record record, int level, String language) {


        if(level == 5 && language.equals("eng")) {

            if( !(record.containsLevel5Classification() && record.isFullEnglishText() ) ) return false;


            List<String> terms = record.getLanguageSpecificTerms("eng");
            terms.addAll( record.getTermsFromAffiliation() );
            terms.addAll( record.getTermsFromHost()  );
            terms.addAll( record.getUnkontrolledKkeywords() );
            String ISBN = record.getISBN();
            if(ISBN != null) terms.add( ISBN  );
            terms.addAll( record.getIssn());
            addTokens(terms);

            return true;



        } else if (level == 3 && language.equals("eng")) {

            if( !(record.containsLevel3Classification() && record.isFullEnglishText() ) ) return false;

            List<String> terms = record.getLanguageSpecificTerms("eng");
            terms.addAll( record.getTermsFromAffiliation() );
            terms.addAll( record.getTermsFromHost()  );
            terms.addAll( record.getUnkontrolledKkeywords() );
            String ISBN = record.getISBN();
            if(ISBN != null) terms.add( ISBN  );
            terms.addAll( record.getIssn());
            addTokens(terms);

            return true;


        } else if (level == 5 && language.equals("swe")) {

            if( !(record.containsLevel5Classification() && record.isFullSwedishText() ) ) return false;

            List<String> terms = record.getLanguageSpecificTerms("swe");
            terms.addAll( record.getTermsFromAffiliation() );
            terms.addAll( record.getTermsFromHost()  );
            terms.addAll( record.getUnkontrolledKkeywords() );
            String ISBN = record.getISBN();
            if(ISBN != null) terms.add( ISBN  );
            terms.addAll( record.getIssn());
            addTokens(terms);

            return true;


        } else if(level == 3 && language.equals("swe")) {

            if( !(record.containsLevel3Classification() && record.isFullSwedishText() ) ) return false;


            List<String> terms = record.getLanguageSpecificTerms("swe");
            terms.addAll( record.getTermsFromAffiliation() );
            terms.addAll( record.getTermsFromHost()  );
            terms.addAll( record.getUnkontrolledKkeywords() );
            String ISBN = record.getISBN();
            if(ISBN != null) terms.add( ISBN  );
            terms.addAll( record.getIssn());
            addTokens(terms);

            return true;

        }


        return false;

    }

    public void addTokens( List<String> tokens ) {

        Set<String> seenBeforeForCurrentRecord = new HashSet<>(50);


        for(String t : tokens) {

            int indice = this.index.getInt(t);

            if (indice == -1) {

                this.index.put(t, counter);
                this.IDF.addValue(counter,1);
                counter++;
            } else {

            if(!seenBeforeForCurrentRecord.contains(t)) this.IDF.addValue(indice,1);

            }

            seenBeforeForCurrentRecord.add(t);

        }


    }


    public int getIDF(int index) {


        return IDF.get(index);

    }


    public void removeRareTerms(int minFreq) {


        System.out.println("Dimensionality before reduction of rare terms: " + this.index.size());



        for(ObjIntCursor<String> cur = index.cursor(); cur.moveNext(); ) {


            int indice = cur.value();

            int IDF = this.IDF.get(indice);

            if(IDF < minFreq) cur.remove();

        }


        System.out.println("Dimensionality after reduction of rare terms: " + this.index.size());


        HashObjIntMap<String> index2 = HashObjIntMaps.getDefaultFactory().withDefaultValue(-1).newMutableMap(10000);


        int dim = 0;

        for(ObjIntCursor<String> cur = index.cursor(); cur.moveNext(); ) {

            String term = cur.key();
            index2.put(term,dim);
            dim++;

        }



        //remapped from 0.. to maxDim
        this.index = index2;


        System.out.println("new index size: " + this.index.size());


    }


    public int getIndice(String t) {

        return this.index.getInt(t);

    }

    public void save(String file) {

        try
        {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);



            HashMap<String,Integer> serializeMap = new HashMap<>(this.index.size());

            for(ObjIntCursor<String> cur = index.cursor(); cur.moveNext(); ) {


                serializeMap.put(cur.key(),cur.value());

            }




            oos.writeObject(serializeMap);
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

            HashMap<String,Integer> serializeMap = (HashMap<String,Integer>)ois.readObject();

            this.index.clear();

            for(Map.Entry<String,Integer> entry : serializeMap.entrySet()) {


                this.index.put(entry.getKey(), entry.getValue().intValue());

            }



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



        for(ObjIntCursor<String> cur = index.cursor(); cur.moveNext(); ) {


            if(cur.value() == indice) return  cur.key();
        }



        return null;

    }


    public SparseVector getSparseVector(List<String> stringTokens, boolean L2normalize) {

        HashSet<String> tokens = new HashSet( stringTokens );
        SparseVector sparseVector = new SparseVector(this.index.size(), tokens.size());

        for (String s : tokens) {

            int ind = this.getIndice(s);
            if (ind != -1) sparseVector.set(ind, 1d);

        }

        if(L2normalize) {
            sparseVector.normalize();
        }
        return sparseVector;
    }

    public SparseVector getSparseVector(Set<String> stringTokens, boolean L2normalize) {


        SparseVector sparseVector = new SparseVector(this.index.size(), stringTokens.size());

        for (String s : stringTokens) {

            int ind = this.getIndice(s);
            if (ind != -1) sparseVector.set(ind, 1d);

        }

        if(L2normalize) sparseVector.normalize();
        return sparseVector;
    }





}
