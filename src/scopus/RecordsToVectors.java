package scopus;



import Diva.VectorAndSim;
import Diva.VectorWithID;
import com.google.common.collect.MinMaxPriorityQueue;
import com.koloboke.collect.map.hash.HashObjIntMap;
import com.koloboke.collect.map.hash.HashObjIntMaps;
import jsat.linear.IndexValue;
import jsat.linear.SparseMatrix;
import jsat.linear.SparseVector;
import jsat.text.wordweighting.TfIdf;
import misc.LanguageTools.RemoveCopyRightFromAbstract;
import misc.Parsers.SimpleParser;
import misc.Stemmers.EnglishStemmer;
import misc.stopwordLists.EnglishStopWords60;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by crco0001 on 1/30/2018.
 */
public class RecordsToVectors {

    private final HashObjIntMap<String> termToIndiceText = HashObjIntMaps.getDefaultFactory().withNullKeyAllowed(false).withDefaultValue(-1).newMutableMap();
    private final HashObjIntMap<String> termToIndiceReferences = HashObjIntMaps.getDefaultFactory().withNullKeyAllowed(false).withDefaultValue(-1).newMutableMap();

    private int getZeroBasedIndexText(String term) {

        int indice = termToIndiceText.getInt(term);

        if(indice != -1) return indice;

        //else we havent seen this term before..

        indice = termToIndiceText.size();

        termToIndiceText.put(term,indice);

        return indice;

    }

    private int getZeroBasedIndexReferences(String term) {

        int indice = termToIndiceReferences.getInt(term);

        if(indice != -1) return indice;

        //else we havent seen this term before..

        indice = termToIndiceReferences.size();

        termToIndiceReferences.put(term,indice);

        return indice;

    }

    public List<SparseVector> getSparseVectors(List<List<String>> listsOfTokens, boolean isText) {


        List<SparseVector> sparseVectors = new ArrayList<>();

        for(List<String> tokens : listsOfTokens) {


            SparseVector sparseVector = new SparseVector(250000,30); // fix length later, init size = 25

            for(String s : tokens) {


                int termIndice = (isText) ? getZeroBasedIndexText(s) : getZeroBasedIndexReferences(s);

                sparseVector.increment(termIndice,1);

            }


            sparseVectors.add(sparseVector);
        }

        int length = (isText) ? termToIndiceText.size() : termToIndiceReferences.size();

        System.out.println("Vector size dimensions : " + length );

        for(SparseVector vec : sparseVectors) {

            vec.setLength(length);
        }


        return sparseVectors;
    }

    public void applyTFIDFonVectorList(List<SparseVector> vectors) {

        TfIdf tfIdf = new TfIdf();

        Integer[] docFreq = new Integer[ vectors.get(1).length() ];

        for (int i = 0; i < docFreq.length; i++) docFreq[i] = 0;

        for (SparseVector v : vectors) {

            Iterator<IndexValue> it = v.getNonZeroIterator();

            while (it.hasNext()) {

                int index = it.next().getIndex();

                docFreq[index]++;

            }


        }

        tfIdf.setWeight(vectors, Arrays.asList( docFreq ) );

        for(SparseVector v : vectors) tfIdf.applyTo(v);
    }

    public static MinMaxPriorityQueue<VectorAndSim> getTopK(VectorWithID v, List<VectorWithID> vectorSet, int K, double minSim) {

        MinMaxPriorityQueue<VectorAndSim> minMaxPriorityQueue = MinMaxPriorityQueue.maximumSize(K).create();

        SparseVector targetVector = v.getVec();
        int targetID = v.getId();

        for(VectorWithID otherVector : vectorSet) {


            if(targetID == otherVector.getId()) continue; //dont compare with yourself

            double sim = targetVector.dot(  otherVector.getVec() );

            if(sim < minSim) continue;

            minMaxPriorityQueue.add(  new VectorAndSim(otherVector,sim)  );


        }



        return minMaxPriorityQueue;

    }


    public static void writeToTriplets(List<MinMaxPriorityQueue<VectorAndSim>> topk, String file) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(file)));


        for (int i = 0; i < topk.size(); i++) {

            MinMaxPriorityQueue<VectorAndSim> similarVectors = topk.get(i);

            Iterator<VectorAndSim> iterator = similarVectors.iterator();

            while (iterator.hasNext()) {

                VectorAndSim similarVector = iterator.next();

                writer.write(i + " " + similarVector.getVectorID() + " " + similarVector.getSim());
                writer.newLine();

                //obs! wont make this symmetric here.


            }


        }

        writer.flush();
        writer.close();
    }


    public static void main(String[] arg) throws IOException {

        RecordsToVectors recordsToVectors = new RecordsToVectors();

        System.out.println("Reading in serialized records..");
        List<ScopusRecord> records = SimplePersistor.deserializeList("records.ser");

        EnglishStemmer stemmer = new EnglishStemmer();
        EnglishStopWords60 englishStopWords60 = new EnglishStopWords60();


        List<List<String>> textTokenLists = new ArrayList<>();
        List<List<String>> citedRefsTokenLists = new ArrayList<>();

        System.out.println("Extracting terms and cited refs (keys)");
        BufferedWriter writeEidToIndex = new BufferedWriter( new FileWriter( new File("EIDtoIndex.txt")) );
       int counter = 0;
        for(ScopusRecord record : records) {

            boolean isEnglish = "eng".equals(record.getLanguage());
            if(!isEnglish) continue;

            String text = record.getTitle();
            String summary = record.getSummaryText();
            if(summary != null) {

               summary =  RemoveCopyRightFromAbstract.cleanedAbstract(summary);
               text = text.concat(". ").concat(summary);
            }

            List<String> terms = SimpleParser.parse(text,true,englishStopWords60,stemmer);

            //System.out.println(terms);

            List<String> aKeywords = record.getAuthorKeywords();
            if(aKeywords.size() > 0) {


              ListIterator<String> iter = aKeywords.listIterator();

              while(iter.hasNext()) {

                 String akey =  iter.next();

                String fixed = stemmer.stem( akey.toLowerCase() );
                iter.set(fixed);

              }

            }
            //System.out.println(aKeywords);

            terms.addAll(aKeywords);

            textTokenLists.add(terms);

            //System.out.println("");



            List<CitedReference> citedReferenceList = record.getCitedReferences();
            List<String> citationKeys = new ArrayList<>();
            for(int i=0; i< citedReferenceList.size(); i++)  citationKeys.add( citedReferenceList.get(i).getRefID() );

            citedRefsTokenLists.add(citationKeys);


          //  System.out.println(citationKeys);
            //   System.out.println("");



           writeEidToIndex.write(counter +" " + record.getEid() );
           counter++;
           writeEidToIndex.newLine();



        }

        writeEidToIndex.flush();
        writeEidToIndex.close();

        //now we have lists with terms and citationKeys

        System.out.println("creating vectors");
        List<SparseVector> sparseTextVectors = recordsToVectors.getSparseVectors(textTokenLists,true);
        List<SparseVector> sparseCitedRefsVectors = recordsToVectors.getSparseVectors(citedRefsTokenLists,false);


        System.out.println("# text vectors: " + sparseTextVectors.size() );
        System.out.println("# reference vectors: " + sparseCitedRefsVectors.size() );

        System.out.println("Weighting TF-IDF & normalizing");

        recordsToVectors.applyTFIDFonVectorList(sparseTextVectors);
        recordsToVectors.applyTFIDFonVectorList(sparseCitedRefsVectors);

        for(SparseVector v : sparseTextVectors) v.normalize();
        for(SparseVector v : sparseCitedRefsVectors) v.normalize();


        List<VectorWithID> vectorWithIDsTEXT = new ArrayList<>();
        int id = 0;
        for(SparseVector v : sparseTextVectors) {

            vectorWithIDsTEXT.add( new VectorWithID(v,id)  );
            id++;
        }


        List<VectorWithID> vectorWithIDsREF = new ArrayList<>();
        id = 0;
        for(SparseVector v : sparseCitedRefsVectors) {

            vectorWithIDsREF.add( new VectorWithID(v,id)  );
            id++;
        }


        System.out.println("Running knng algo for text and ref..");

        long now = System.currentTimeMillis();
        List<MinMaxPriorityQueue<VectorAndSim>> knngREF =  vectorWithIDsREF.parallelStream().map( (VectorWithID vector) -> getTopK(vector,vectorWithIDsREF,20,0.01)   ).collect(Collectors.toList());
        List<MinMaxPriorityQueue<VectorAndSim>> knngTEXT =  vectorWithIDsTEXT.parallelStream().map( (VectorWithID vector) -> getTopK(vector,vectorWithIDsTEXT,20,0.01)   ).collect(Collectors.toList());


        System.out.println("TopK calculations in : " + (System.currentTimeMillis() - now)/1000.0 );

        System.out.println("Writing to file..");

        writeToTriplets(knngREF,"tripletMatrixREF.txt");
        writeToTriplets(knngTEXT,"tripletMatrixTEXT.txt");

       // System.out.println("Finding eps-nearest neighbours in parallel");

     //   now = System.currentTimeMillis();

     //   List<Set<VectorAndSim>> topEpsSets =  vectorWithIDS.parallelStream().map( (VectorWithID vector) -> getSimilarThreshold(vector,vectorWithIDS,0.05)   ).collect(Collectors.toList());





    }









}
