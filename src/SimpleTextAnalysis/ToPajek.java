package SimpleTextAnalysis;

import Database.FileHashDB;
import SwePub.Record;
import com.google.common.collect.MinMaxPriorityQueue;

import com.koloboke.collect.map.hash.*;
import jsat.linear.IndexValue;
import jsat.linear.SparseVector;
import jsat.text.wordweighting.OkapiBM25;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by Cristian on 2017-04-27.
 */
public class ToPajek {

    /*
pajekFormat.net:

*Vertices 82670
1 "entity"
2 "thing"
3 "anything"
4 "something"
5 "nothing"
6 "whole"
..
..

*arcs
4244 107 5
..
..


     */


    public static String printSparseVector(SparseVector vec) {

        StringBuilder stringbuilder = new StringBuilder();

        Iterator<IndexValue> iter = vec.getNonZeroIterator();
        stringbuilder.append("[");
        while(iter.hasNext()) {

            IndexValue indexValue = iter.next();
            stringbuilder.append(indexValue.getIndex()).append(",");
            stringbuilder.append(indexValue.getValue()).append(" ");

        }
        stringbuilder.append("]");
        return stringbuilder.toString();
    }



    public static int getZeroBasedIndice(String term, HashObjIntMap<String> map) {


        int indice = map.getInt(term);

        if(indice != -1) return indice;

        //else

        indice = map.size();

        map.put(term,indice);

        return (indice);
    }

    public static void main(String[] arg) throws IOException {



        FileHashDB fileHashDB = new FileHashDB();
        fileHashDB.setPathToFile("E:\\Desktop\\JSON_SWEPUB\\SWEPUB20210411.db");
        fileHashDB.createOrOpenDatabase();

        String file = "E:\\Desktop\\JSON_SWEPUB\\SportScienceUris_NEW.txt";
        BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream( new File(file) ), StandardCharsets.UTF_8)) ;
        HashSet<String> uris = new HashSet<>();
        String uri;
        while ( (uri = reader.readLine()) != null ) {

            String[] splitted = uri.split("\t");

            uris.add( splitted[0].trim() );

        }

        reader.close();





        //mapping String term to indice (indices are 1-based).

        HashObjIntMap<String> termToIndice = HashObjIntMaps.getDefaultFactory()

                .withNullKeyAllowed(true)
                .withDefaultValue(-1)
                .<String>newMutableMap();


        System.out.println(fileHashDB.size() +" records in database");


        List<SparseVector> sparseVectorList = new ArrayList<>();
        List<Integer> docIDs = new ArrayList<>();
        List<String> URIs = new ArrayList<>();

        int id = 0;
        for (String uriKey : uris ) {

           Record record = fileHashDB.get(uriKey);

           if(record == null) { System.out.println(uriKey + " not in database. Exiting.."); fileHashDB.closeDatabase(); System.exit(0);  }

           //if(!record.isContainsEnglish()) continue;

            if(!record.isContainsSwedish()) continue;

           // List<String> terms = record.getLanguageSpecificTerms("eng");

            List<String> terms = record.getLanguageSpecificTerms("swe");

            List<String> keywords = record.getUnkontrolledKkeywords();
            String host = record.getHostName();
            terms.addAll(keywords);
            if(host != null) terms.add(host);

            SparseVector docVector = new SparseVector(100000,35); //length fix later, init size  = 25

            for(String s : terms) {

                //String term = s.substring(3,s.length()); //remove TE@

                int termIndice = getZeroBasedIndice(s,termToIndice);
                //indiceToTerm.put(termIndice,s);

                docVector.increment(termIndice,1);


            }


            sparseVectorList.add(docVector);
            docIDs.add(id);
            URIs.add( record.getURI() );
            id++;
        }


        fileHashDB.closeDatabase();

        int dimension = termToIndice.size();
        System.out.println("Vector space dimension: " + termToIndice.size());
        for(SparseVector v : sparseVectorList) {

            v.setLength(dimension);

        }


        System.out.println("# " + sparseVectorList.size() + " vectors created");


        System.out.println("Doc freq calc and BM25 weighting");

        Integer[] docFreq = new Integer[ termToIndice.size() ];

        for(int i=0; i< docFreq.length; i++) docFreq[i] = 0;

        for(SparseVector v : sparseVectorList) {

            Iterator<IndexValue> it = v.getNonZeroIterator();

            while(it.hasNext()) {


                int index = it.next().getIndex();

                docFreq[index]++;

            }


        }



        OkapiBM25 MB25 = new OkapiBM25();
        MB25.setWeight(sparseVectorList,Arrays.asList(docFreq));


        for(SparseVector v : sparseVectorList) {

            MB25.applyTo(v);
            //v.normalize();
        }



        int n = sparseVectorList.size();

        if(n != docIDs.size()) {System.out.println("ABORT!"); System.exit(0); }

        //make a "Non symetric similarity matrix" in pajek.net

        //*Vertices 82670
       // 1 "entity"
       // 2 "thing"
       // 3 "anything"
       // 4 "something"
       // 5 "nothing"
       // 6 "whole"


        BufferedWriter writer = new BufferedWriter( new FileWriter( new File("SimilaritySWESportSci.net")));

        writer.write("*Vertices " + sparseVectorList.size());
        writer.newLine();

        for(int i=1; i<= sparseVectorList.size(); i++) {

            writer.write(i + " " +"\"" + URIs.get(i-1) + "\"" );
            writer.newLine();

        }

        writer.write("*arcs");
        writer.newLine();

        for(int i=0; i< sparseVectorList.size(); i++) {

            MinMaxPriorityQueue<VectorWithSimValue> boundedPriorityQue = MinMaxPriorityQueue.maximumSize(15).create();
            SparseVector targetVector = sparseVectorList.get(i);
            //Integer target_id = docIDs.get(i);
            Integer target_id = (i+1);

                    for(int j=0; j< sparseVectorList.size(); j++) {

                        SparseVector compareVector = sparseVectorList.get(j);
                        //Integer compareId = docIDs.get(j);
                        Integer compareId = (j+1);

                       if(target_id.compareTo(compareId) == 0) continue;

                        double sim = targetVector.dot(compareVector);

                      //  if(sim < 90) continue; //to small sim value, ad-hoc!

                        boundedPriorityQue.add( new VectorWithSimValue(compareVector,sim, compareId, URIs.get(j) ) );
                    }


                    Iterator<VectorWithSimValue> it = boundedPriorityQue.iterator();

                    while (it.hasNext() ) {
                        VectorWithSimValue vectorWithSimValue = it.next();

                        writer.write(target_id + " " + vectorWithSimValue.getVectorID()  + " " + vectorWithSimValue.getSim() );
                        writer.newLine();

                    }




        }

        writer.flush();
        writer.close();


    }

}
