package scopus;



import ClusterAnalysis.ModularityOptimizer;
import ClusterAnalysis.Network;
import ClusterAnalysis.VOSClusteringTechnique;
import Diva.MatrixEntry;
import Diva.VectorAndSim;
import Diva.VectorWithID;
import com.google.common.collect.MinMaxPriorityQueue;
import com.koloboke.collect.map.hash.HashObjIntMap;
import com.koloboke.collect.map.hash.HashObjIntMaps;
import jsat.linear.IndexValue;
import jsat.linear.SparseMatrix;
import jsat.linear.SparseVector;
import jsat.text.wordweighting.TfIdf;
import jsat.utils.DoubleList;
import misc.EqualizeDistributions.ECDF;
import misc.EqualizeDistributions.QuantileFun;
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


    public static SparseMatrix getSecondOrderTopEps(SparseMatrix similarityMatrix, double threshold, boolean normalize ) {


        if(normalize) {


            for(int i=0; i<similarityMatrix.rows(); i++)  similarityMatrix.getRowView(i).normalize();


        }



        int rows = similarityMatrix.rows();

        SparseMatrix secorndOrder = new SparseMatrix(similarityMatrix.rows(), similarityMatrix.cols(),20);


        for(int i=0; i<rows; i++) {

            SparseVector i_sparseVector = ((SparseVector)similarityMatrix.getRowView(i));

                for(int j=(i+1); j<rows; j++ ) {


                    double sim = i_sparseVector.dot( (SparseVector)similarityMatrix.getRowView(j) );

                    if(sim > threshold) secorndOrder.set(i,j,sim);


                }



        }


        return secorndOrder; //upper right, symmetric


    }


    public static SparseMatrix getSecondOrderTopK(SparseMatrix similarityMatrix, int K, boolean normalize) {


        int rows = similarityMatrix.rows();

        SparseMatrix secorndOrder = new SparseMatrix(similarityMatrix.rows(), similarityMatrix.cols(), K + 10);


        if (normalize) {


            for (int i = 0; i < similarityMatrix.rows(); i++) similarityMatrix.getRowView(i).normalize();

        }


        for (int i = 0; i < rows; i++) {

            SparseVector i_sparseVector = ((SparseVector) similarityMatrix.getRowView(i));

            MinMaxPriorityQueue<MatrixEntry> minMaxPriorityQueue = MinMaxPriorityQueue.maximumSize(K).create();


            for (int j = 0; j < rows; j++) {

                if (i == j) continue;

                double sim = i_sparseVector.dot((SparseVector) similarityMatrix.getRowView(j));
                minMaxPriorityQueue.add(new MatrixEntry(i, j, sim));

            }


            Iterator<MatrixEntry> iter = minMaxPriorityQueue.iterator();

            while (iter.hasNext()) {


                MatrixEntry matrixEntry = iter.next();

                secorndOrder.set(matrixEntry.getI(), matrixEntry.getJ(), matrixEntry.getVal());

            }


        }


        return secorndOrder;
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

    public static void writeToTripletUpperRight(SparseMatrix M, String filename) throws IOException {

        int n = M.rows();

        BufferedWriter writer = new BufferedWriter( new FileWriter( new File(filename)));


        for(int i=0; i<n; i++) {

            Iterator<IndexValue> iter = M.getRowView(i).getNonZeroIterator();

            while(iter.hasNext()) {

                IndexValue indexValue = iter.next();

                if(i >= indexValue.getIndex()) continue;

                writer.write(i + "\t" + indexValue.getIndex() +"\t" + indexValue.getValue());
                writer.newLine();
            }


        }


        writer.flush();
        writer.close();
    }



    public static void main(String[] arg) throws IOException {

        RecordsToVectors recordsToVectors = new RecordsToVectors();

        System.out.println("Reading in serialized records..");
        List<ScopusRecord> records = SimplePersistor.deserializeListScopusRecords("records.ser");

        EnglishStemmer stemmer = new EnglishStemmer();
        EnglishStopWords60 englishStopWords60 = new EnglishStopWords60();


        List<List<String>> textTokenLists = new ArrayList<>();
        List<List<String>> citedRefsTokenLists = new ArrayList<>();

        System.out.println("Extracting terms and cited refs (keys)");
        BufferedWriter writeEidToIndex = new BufferedWriter( new FileWriter( new File("EIDtoIndex.txt")) );

        List<String> publicationNames = new ArrayList<>();

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


           publicationNames.add(record.getPublicationName() );
           writeEidToIndex.write(counter_zero_based +" " + record.getEid() );
           counter_zero_based++;
           writeEidToIndex.newLine();



        }

        writeEidToIndex.flush();
        writeEidToIndex.close();

        //now we have lists with terms and citationKeys


        SimplePersistor.serialiseList(textTokenLists,"texttokens.ser");
        SimplePersistor.serialiseList(publicationNames,"pubnames.ser");


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


        System.out.println("Calculating top K for text and cited references..");

        long now = System.currentTimeMillis();
        List<MinMaxPriorityQueue<VectorAndSim>> knngREF =  vectorWithIDsREF.parallelStream().map( (VectorWithID vector) -> getTopK(vector,vectorWithIDsREF,50,0.01)   ).collect(Collectors.toList());
        List<MinMaxPriorityQueue<VectorAndSim>> knngTEXT =  vectorWithIDsTEXT.parallelStream().map( (VectorWithID vector) -> getTopK(vector,vectorWithIDsTEXT,50,0.01)   ).collect(Collectors.toList());


        System.out.println("time elapsed : " + (System.currentTimeMillis() - now)/1000.0 );
        System.out.println("# in list<MinMaxPrioQ: " + knngREF.size() + " " +knngTEXT.size() );

        SparseMatrix matrixRef = knngToMatrix(knngREF,false);
        SparseMatrix matrixText = knngToMatrix(knngTEXT,false);

        System.out.println("Building ECDF and Quantile function for normalization..");

        DoubleList citedRefValues = new DoubleList(1000);
        for(int i=0; i<matrixRef.rows(); i++) {

            Iterator<IndexValue> iter = matrixRef.getRowView(i).getNonZeroIterator();

            while(iter.hasNext()) {

                citedRefValues.add(iter.next().getValue() );

            }

        }

        ECDF citedRefECDF = new ECDF(citedRefValues);


        DoubleList textValues = new DoubleList(1000);
        for(int i=0; i<matrixText.rows(); i++) {

            Iterator<IndexValue> iter = matrixText.getRowView(i).getNonZeroIterator();

            while(iter.hasNext()) {

                textValues.add(iter.next().getValue() );

            }

        }

        QuantileFun textQuantileFunction = new QuantileFun(textValues);

        System.out.println("Normalizing Cited referens matrix w.r.t. text matrix..");



        for(int i=0; i<matrixRef.rows(); i++) {

            Iterator<IndexValue> iter = matrixRef.getRowView(i).getNonZeroIterator();

            while(iter.hasNext()) {

                IndexValue indexValue = iter.next();

                double prob = citedRefECDF.getProbBinarySearch( indexValue.getValue()  );
                double newValue = textQuantileFunction.getQuantile(prob);

                indexValue.setValue(newValue);

            }

        }




        System.out.println("merge matrices, equal weight");
        matrixText.mutableAdd(1,matrixRef);

        System.out.println("Running secondOrder..");

        //not symmetric!
        SparseMatrix finalMatrix = getSecondOrderTopK(matrixText,30,true);

        //SparseMatrix secondOrder = getSecondOrderTopEps(MatrixRef,0.2,true);

        System.out.println("Second order calculations in : " + (System.currentTimeMillis() - now)/1000.0 );


        System.out.println("Make symmetric");

        finalMatrix.mutableAdd( finalMatrix.clone().transpose() );

        Network network = ModularityOptimizer.convertSparseMatrix(finalMatrix,1);

        network.save("triplet.bin");
        writeToTripletUpperRight(finalMatrix,"triplet.mat");

        writeToPajek(finalMatrix,"triplet.net",true);


        //clustering
        //int modularityFunction = 1;

        //double resolution =  1;

        //double resolution2 = ((modularityFunction == 1) ? (resolution / (2 * network.getTotalEdgeWeight() + network.getTotalEdgeWeightSelfLinks() )) : resolution);

        //VOSClusteringTechnique vOSClusteringTechnique = new VOSClusteringTechnique(network, resolution2);
        //double modularity = vOSClusteringTechnique.calcQualityFunction();
        //System.out.println("Q: " + modularity +" clusters: " + vOSClusteringTechnique.getClustering().getNClusters());


        //vOSClusteringTechnique.runSmartLocalMovingAlgorithm( new Random() );
       //modularity = vOSClusteringTechnique.calcQualityFunction();
       // System.out.println("Q: " + modularity +" clusters: " + vOSClusteringTechnique.getClustering().getNClusters());

        //vOSClusteringTechnique.runSmartLocalMovingAlgorithm( new Random() );
       // modularity = vOSClusteringTechnique.calcQualityFunction();
       // System.out.println("Q: " + modularity  +" clusters: " + vOSClusteringTechnique.getClustering().getNClusters() );



        //writeToTripletUpperRight(finalMatrix,"triplet.mat");

        //writeToPajek(finalMatrix,"secondOrderV2.net",false);

       // writeToPajek(MatrixRef,"combined.net",false);


        //writeToPajek(M1,"refs2.net", false);
        //writeToPajek(M2, "text2.net",false);

        //writeToTripletsZeroBased(knngREF,"tripletMatrixREF.txt");

        //writeToTripletsZeroBased(knngTEXT,"tripletMatrixTEXT.txt");




       // System.out.println("Finding eps-nearest neighbours in parallel");

     //   now = System.currentTimeMillis();

     //   List<Set<VectorAndSim>> topEpsSets =  vectorWithIDS.parallelStream().map( (VectorWithID vector) -> getSimilarThreshold(vector,vectorWithIDS,0.05)   ).collect(Collectors.toList());





    }









}
