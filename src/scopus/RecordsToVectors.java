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

    public static void writeToTripletsZeroBased(List<MinMaxPriorityQueue<VectorAndSim>> topk, String file) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(file)));


        for (int i = 0; i < topk.size(); i++) {

            MinMaxPriorityQueue<VectorAndSim> similarVectors = topk.get(i);

            Iterator<VectorAndSim> iterator = similarVectors.iterator();

            while (iterator.hasNext()) {

                VectorAndSim similarVector = iterator.next();

                writer.write( i + "\t" + similarVector.getVectorID() + "\t" + similarVector.getSim());
                writer.newLine();

                //obs! wont make this symmetric here.


            }


        }

        writer.flush();
        writer.close();
    }


    public static SparseMatrix knngToMatrix(List<MinMaxPriorityQueue<VectorAndSim>> topKsets, boolean makeSymetric) {


        SparseMatrix sparseSimilarityMatrix = new SparseMatrix(topKsets.size(),topKsets.size(),20);

        for(int i=0; i< topKsets.size(); i++) {

            MinMaxPriorityQueue<VectorAndSim> similarVectors = topKsets.get(i);

            Iterator<VectorAndSim> iterator = similarVectors.iterator();

            while(iterator.hasNext()) {

                VectorAndSim vec = iterator.next();


                sparseSimilarityMatrix.set(i, vec.getVectorID(),vec.getSim() );

                //make symetric

               if(makeSymetric)  sparseSimilarityMatrix.set(vec.getVectorID(), i , vec.getSim() );


            }


        }


        return sparseSimilarityMatrix;

    }

    public static void writeToPajek(SparseMatrix matrix, String fileName, boolean upperRightOnly) throws IOException {


        if(matrix.rows() != matrix.cols()) throw new IOException("not a square matrix");


        BufferedWriter writer = new BufferedWriter( new FileWriter( new File(fileName)));

        writer.write("*Vertices " + matrix.rows() );
        writer.newLine();

        for(int i=1; i<= matrix.rows(); i++) {

            writer.write(i + " " +"\"" + (i-1) + "\"" );
            writer.newLine();

        }

        writer.write("*arcs");
        writer.newLine();


        for(int r=0; r<matrix.rows(); r++) {

            Iterator<IndexValue> it = matrix.getRowView(r).getNonZeroIterator();
            while (it.hasNext()) {

                IndexValue indexValue = it.next();

                if(upperRightOnly && indexValue.getIndex() <= r) continue;

                writer.write((r+1) +" " + (indexValue.getIndex()+1) + " " + indexValue.getValue() );
                writer.newLine();
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
       int counter_zero_based = 0;
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



           writeEidToIndex.write(counter_zero_based +" " + record.getEid() );
           counter_zero_based++;
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
        int id_zero_based = 0;
        for(SparseVector v : sparseTextVectors) {

            vectorWithIDsTEXT.add( new VectorWithID(v,id_zero_based)  );
            id_zero_based++;
        }


        List<VectorWithID> vectorWithIDsREF = new ArrayList<>();
        id_zero_based = 0;
        for(SparseVector v : sparseCitedRefsVectors) {

            vectorWithIDsREF.add( new VectorWithID(v,id_zero_based)  );
            id_zero_based++;
        }


        System.out.println("Running knng algo for text and ref..");

        long now = System.currentTimeMillis();
        List<MinMaxPriorityQueue<VectorAndSim>> knngREF =  vectorWithIDsREF.parallelStream().map( (VectorWithID vector) -> getTopK(vector,vectorWithIDsREF,10,0.01)   ).collect(Collectors.toList());
        List<MinMaxPriorityQueue<VectorAndSim>> knngTEXT =  vectorWithIDsTEXT.parallelStream().map( (VectorWithID vector) -> getTopK(vector,vectorWithIDsTEXT,10,0.01)   ).collect(Collectors.toList());


        System.out.println("TopK calculations in : " + (System.currentTimeMillis() - now)/1000.0 );
        System.out.println("# in list<MinMaxPrioQ: " + knngREF.size() + " " +knngTEXT.size() );

        System.out.println("Writing to file..");

        SparseMatrix MatrixRef = knngToMatrix(knngREF,false);
        SparseMatrix MatrixText = knngToMatrix(knngTEXT,false);

        double weigh = 0.5;
        MatrixRef.mutableMultiply(1-weigh);
        MatrixRef.mutableAdd(weigh,MatrixText);

        SparseMatrix transposed = new SparseMatrix(MatrixRef.rows(),MatrixText.cols(),15);
        MatrixRef.transpose(transposed);

        MatrixRef.mutableAdd(transposed);


        writeToPajek(MatrixRef,"combined.net",false);


        //writeToPajek(M1,"refs2.net", false);
        //writeToPajek(M2, "text2.net",false);

        //writeToTripletsZeroBased(knngREF,"tripletMatrixREF.txt");
        //writeToTripletsZeroBased(knngTEXT,"tripletMatrixTEXT.txt");




       // System.out.println("Finding eps-nearest neighbours in parallel");

     //   now = System.currentTimeMillis();

     //   List<Set<VectorAndSim>> topEpsSets =  vectorWithIDS.parallelStream().map( (VectorWithID vector) -> getSimilarThreshold(vector,vectorWithIDS,0.05)   ).collect(Collectors.toList());





    }









}
