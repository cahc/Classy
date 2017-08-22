package Diva;

import Database.ModsDivaFileParser;
import SwePub.Record;
import com.google.common.collect.MinMaxPriorityQueue;
import com.koloboke.collect.map.hash.HashObjIntMap;
import com.koloboke.collect.map.hash.HashObjIntMaps;
import jsat.linear.IndexValue;
import jsat.linear.SparseMatrix;
import jsat.linear.SparseVector;
import jsat.text.wordweighting.OkapiBM25;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by crco0001 on 8/16/2017.
 */
public class DivaRecordsToVectors {


    private static int getRandomNumberInRange(int min, int max) {

        Random r = new Random();
        return r.ints(min, (max )).findFirst().getAsInt(); // max not inclusive

    }



    private final HashObjIntMap<String> termToIndice = HashObjIntMaps.getDefaultFactory().withNullKeyAllowed(false).withDefaultValue(-1).newMutableMap();

    public int getZeroBasedIndex(String term) {

        int indice = termToIndice.getInt(term);

        if(indice != -1) return indice;

        //else we havent seen this term before..

        indice = termToIndice.size();

        termToIndice.put(term,indice);

        return indice;

    }

    public void applyOkapiBM25onVectorList(List<SparseVector> vectors) {

        Integer[] docFreq = new Integer[this.termToIndice.size()];

        for (int i = 0; i < docFreq.length; i++) docFreq[i] = 0;

        for (SparseVector v : vectors) {

            Iterator<IndexValue> it = v.getNonZeroIterator();

            while (it.hasNext()) {


                int index = it.next().getIndex();

                docFreq[index]++;

            }


        }


        OkapiBM25 okapiBM25 = new OkapiBM25();
        okapiBM25.setWeight(vectors, Arrays.asList( docFreq ));

        for(SparseVector v : vectors) okapiBM25.applyTo(v);

    }


    public List<SparseVector> getSparseVectors(List<Record> recordList) {

        ArrayList<SparseVector> sparseVectors = new ArrayList<>();


        for(Record r : recordList) {


            List<String> terms = r.getLanguageSpecificTerms("eng");
            terms.addAll( r.getUnkontrolledKkeywords() );
            terms.addAll( r.getTermsFromHost() );


            SparseVector sparseVector = new SparseVector(100000,25); // fix length later, init size = 25

                    for(String s : terms) {


                        int termIndice = getZeroBasedIndex(s);

                        sparseVector.increment(termIndice,1);

                    }


                    sparseVectors.add(sparseVector);
        }

        int length = termToIndice.size();
        System.out.println("Vector size dimensions : " + length );

        for(SparseVector vec : sparseVectors) {

            vec.setLength(length);
        }


        return sparseVectors;
    }


    public static MinMaxPriorityQueue<VectorAndSim> getTopK(VectorWithID v, List<VectorWithID> vectorSet, int K) {

        MinMaxPriorityQueue<VectorAndSim> minMaxPriorityQueue = MinMaxPriorityQueue.maximumSize(K).create();

        SparseVector targetVector = v.getVec();
        int targetID = v.getId();

        for(VectorWithID otherVector : vectorSet) {


            if(targetID == otherVector.getId()) continue; //dont compare with yourself

            double sim = targetVector.dot(  otherVector.getVec() );

            minMaxPriorityQueue.add(  new VectorAndSim(otherVector,sim)  );


        }



        return minMaxPriorityQueue;

    }


    public static void main(String[] arg) throws IOException, XMLStreamException {


      //  HashObjIntMap<String> wordCounter = HashObjIntMaps.getDefaultFactory().newMutableMap();


        ModsDivaFileParser modsDivaFileParser = new ModsDivaFileParser();

        System.out.println("Parsing XML");
        List<Record> recordList = modsDivaFileParser.parse(arg[0]);


        ArrayList<Record> reducedRecorList = new ArrayList<>();

        for(Record r : recordList) {

            if(r.isFullEnglishText()) reducedRecorList.add(r);

        }

        recordList = null;


        DivaRecordsToVectors divaRecordsToVectors = new DivaRecordsToVectors();

        System.out.println("creating vectors");
        List<SparseVector> sparseVectors = divaRecordsToVectors.getSparseVectors(reducedRecorList);

        System.out.println("Weighting vectors");
        divaRecordsToVectors.applyOkapiBM25onVectorList(sparseVectors);

        List<VectorWithID> vectorWithIDS = new ArrayList<>();
        int id = 0;
        for(SparseVector v : sparseVectors) {

            vectorWithIDS.add( new VectorWithID(v,id)  );
            id++;
        }




        System.out.println("Finding k-nearest neighbours in parallel");

        long now = System.currentTimeMillis();


         List<MinMaxPriorityQueue> topKsets =  vectorWithIDS.parallelStream().map( (VectorWithID vector) -> getTopK(vector,vectorWithIDS,10)   ).collect(Collectors.toList());





        System.out.println("TopK calculations in : " + (System.currentTimeMillis() - now)/1000.0 );


        // check some results

        int[] randomInts = new int[10];
        for(int i=0; i<10; i++) randomInts[i] = getRandomNumberInRange(0,topKsets.size());

       for(int i=0; i<10; i++) {

           MinMaxPriorityQueue<VectorAndSim> tragetAndNiegbourhood = topKsets.get( randomInts[i] );

           Iterator<VectorAndSim> iterator = tragetAndNiegbourhood.iterator();

           System.out.println("Target: " + reducedRecorList.get( randomInts[i] ).getTitle());

           while (iterator.hasNext()) {


               VectorAndSim match = iterator.next();

               System.out.println("Match: " + " " + reducedRecorList.get(match.getVectorID()).getTitle() + " " + match.getSim());

           }


           System.out.println();
       }


        System.out.println("Constructing similarity matrix");

        SparseMatrix sparseSimilarityMatrix = new SparseMatrix(topKsets.size(),topKsets.size(),10);

        for(int i=0; i< topKsets.size(); i++) {

            MinMaxPriorityQueue<VectorAndSim> similarVectors = topKsets.get(i);

            Iterator<VectorAndSim> iterator = similarVectors.iterator();

            while(iterator.hasNext()) {

                VectorAndSim vec = iterator.next();

                sparseSimilarityMatrix.set(i, vec.getVectorID(),vec.getSim() );

                //make symetric

                sparseSimilarityMatrix.set(vec.getVectorID(), i , vec.getSim() );


            }


        }


        System.out.println(sparseSimilarityMatrix.rows() + " " + sparseSimilarityMatrix.cols() + " " + sparseSimilarityMatrix.isSparce() + " " + sparseSimilarityMatrix.nnz());


        System.out.println(sparseSimilarityMatrix.getRowView(0));

        //List<Double> hej = sparseVectors.parallelStream().map(  (SparseVector v) -> DivaRecordsToVectors.apa(v,sparseVectors)    ).collect(Collectors.toList() );


    }

}
