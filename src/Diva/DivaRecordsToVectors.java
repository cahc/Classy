package Diva;

import ClusterAnalysis.*;
import Database.ModsDivaFileParser;
import SwePub.Record;
import com.google.common.collect.MinMaxPriorityQueue;
import com.koloboke.collect.map.hash.HashObjIntMap;
import com.koloboke.collect.map.hash.HashObjIntMaps;
import jsat.linear.IndexValue;
import jsat.linear.SparseMatrix;
import jsat.linear.SparseVector;
import jsat.text.wordweighting.OkapiBM25;
import jsat.text.wordweighting.TfIdf;

import javax.xml.stream.XMLStreamException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
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

    public void applyTFIDFonVectorList(List<SparseVector> vectors) {

        TfIdf tfIdf = new TfIdf();

        Integer[] docFreq = new Integer[this.termToIndice.size()];

        for (int i = 0; i < docFreq.length; i++) docFreq[i] = 0;

        for (SparseVector v : vectors) {

            Iterator<IndexValue> it = v.getNonZeroIterator();

            while (it.hasNext()) {


                int index = it.next().getIndex();

                docFreq[index]++;

            }


        }

        tfIdf.setWeight(vectors,Arrays.asList( docFreq ) );

        for(SparseVector v : vectors) tfIdf.applyTo(v);
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

    public static SparseMatrix getTopKSecondOrder(SparseMatrix similarityMatrixOriginal, int K, double minSim) {

        SparseMatrix similarityMatrix = similarityMatrixOriginal.clone();

        for(int i=0; i<similarityMatrix.rows(); i++) similarityMatrix.getRowView(i).normalize();

        for(int i=0; i< similarityMatrix.rows(); i++) similarityMatrix.set(i,i,1.0D);

        SparseMatrix secondOrderMatrix = new SparseMatrix(similarityMatrix.rows()+10,similarityMatrix.cols()+10);

        for(int i=0; i<similarityMatrix.rows(); i++) {

            MinMaxPriorityQueue<VectorAndSim> minMaxPriorityQueue = MinMaxPriorityQueue.maximumSize(K).create();

            SparseVector aVector = (SparseVector)similarityMatrix.getRowView(i);
            for(int j=0; j<similarityMatrix.rows(); j++) {

                if(j==i) continue;

                SparseVector otherVector = (SparseVector)similarityMatrix.getRowView(j);

                double sim = aVector.dot(otherVector);
                if(sim < minSim) continue;

                minMaxPriorityQueue.add(  new VectorAndSim(new VectorWithID(otherVector,j),sim)  );


            }


            for(VectorAndSim vectorAndSim : minMaxPriorityQueue) {


                secondOrderMatrix.set(i,vectorAndSim.getVectorID(),vectorAndSim.getSim());


            }



        }



        //reset

        return secondOrderMatrix;
    }


    public static Set<VectorAndSim> getSimilarThreshold(VectorWithID v, List<VectorWithID> vectorSet, double threshold) {

        HashSet<VectorAndSim> setOfSimilarVectors = new HashSet<>();

        SparseVector targetVector = v.getVec();
        int targetID = v.getId();

        for(VectorWithID otherVector : vectorSet) {


            if(targetID == otherVector.getId()) continue; //dont compare with yourself

            double sim = targetVector.dot(  otherVector.getVec() );

            if(sim < threshold) continue;

            setOfSimilarVectors.add(  new VectorAndSim(otherVector,sim)  );


        }


        return setOfSimilarVectors;
    }

    public static SparseMatrix knngToSymetricSimilarityMatrix(List<MinMaxPriorityQueue<VectorAndSim>> topKsets) {


        SparseMatrix sparseSimilarityMatrix = new SparseMatrix(topKsets.size(),topKsets.size(),15);

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


        return sparseSimilarityMatrix;

    }

    public static SparseMatrix knngToSymetricSimilarityMatrixType2(List<MinMaxPriorityQueue<VectorAndSim>> topKsets) {

        SparseMatrix sparseSimilarityMatrix = new SparseMatrix(topKsets.size(),topKsets.size(),15);

        for(int i=0; i< topKsets.size(); i++) {

            MinMaxPriorityQueue<VectorAndSim> similarVectors = topKsets.get(i);

            Iterator<VectorAndSim> iterator = similarVectors.iterator();

            while(iterator.hasNext()) {

                VectorAndSim vec = iterator.next();


                sparseSimilarityMatrix.set(i, vec.getVectorID(),vec.getSim() );

                //make symetric

                //sparseSimilarityMatrix.set(vec.getVectorID(), i , vec.getSim() );

            }


        }

        System.out.println("Make symmetric X + X^t /2");
        //make symmetric
        SparseMatrix trasposeText = sparseSimilarityMatrix.clone();
        trasposeText.mutableTranspose();
        sparseSimilarityMatrix.mutableAdd(1,trasposeText);
        //sparseSimilarityMatrix.mutableMultiply(0.5);


        return  sparseSimilarityMatrix;
    }

    public static SparseMatrix knngToSymetricSimilarityMatrixType3(List<MinMaxPriorityQueue<VectorAndSim>> topKsets) {


        SparseMatrix sparseSimilarityMatrix = new SparseMatrix(topKsets.size(),topKsets.size(),15);

        for(int i=0; i< topKsets.size(); i++) {

            MinMaxPriorityQueue<VectorAndSim> similarVectors = topKsets.get(i);

            Iterator<VectorAndSim> iterator = similarVectors.iterator();

            while(iterator.hasNext()) {

                VectorAndSim vec = iterator.next();


                sparseSimilarityMatrix.set(i, vec.getVectorID(),vec.getSim() );


            }


        }


        //multiplyTranspose(Matrix B, Matrix C)
        //Alters the matrix C to be equal to C = C+A*BT

        SparseMatrix A = new SparseMatrix(sparseSimilarityMatrix.rows(),sparseSimilarityMatrix.cols(),20);

        SparseMatrix B = new SparseMatrix(sparseSimilarityMatrix.rows(),sparseSimilarityMatrix.cols(),20);

        SparseMatrix transposed = sparseSimilarityMatrix.clone();
        transposed.mutableTranspose();

        sparseSimilarityMatrix.multiplyTranspose(sparseSimilarityMatrix,A);
        transposed.multiplyTranspose(transposed,B);



        return (SparseMatrix)A.add(B);
    }

    public static SparseMatrix epsToSymetricSimilarityMatrix(List<Set<VectorAndSim>> epsSet) {

        SparseMatrix sparseSimilarityMatrix = new SparseMatrix(epsSet.size(),epsSet.size(),15);

        for(int i=0; i<epsSet.size(); i++) {

            Set<VectorAndSim> set = epsSet.get(i);


                for(VectorAndSim vectorAndSim : set) {

                    sparseSimilarityMatrix.set(i, vectorAndSim.getVectorID(), vectorAndSim.getSim() );
                    sparseSimilarityMatrix.set(vectorAndSim.getVectorID(), i, vectorAndSim.getSim() );

                }


        }

        return sparseSimilarityMatrix;

    }


    public static void writeToPajek(SparseMatrix matrix, String fileName) throws IOException {


        if(matrix.rows() != matrix.cols()) throw new IOException("not a sumetric matrix");


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

                if(indexValue.getIndex() <= r) continue;

                writer.write((r+1) +" " + (indexValue.getIndex()+1) + " " + indexValue.getValue() );
                writer.newLine();
            }



        }




        writer.flush();
        writer.close();



    }

    public static void writeToNetwork(SparseMatrix matrix, String fileName) throws IOException {

        /*

            The input file is a simple tab-delimited text file listing all pairs of nodes in a network that are connected by an edge.
            An example of an input file for Zachary's karate club network is available here.
            Notice that the numbering of nodes starts at 0. For each pair of nodes, the node with the lower index is listed first, followed by the node with the higher index.
            The lines in an input file are sorted based on the node indices (first based on the indices in the first column, then based on the indices in the second column).
            In the case of a weighted network, edge weights are provided in a third column.


            0	1
            0	2
            0	3
            1	2
            1	3
            1	7


         */

        if(matrix.rows() != matrix.cols()) throw new IOException("not a symmetric matrix");

        BufferedWriter writer = new BufferedWriter( new FileWriter( new File(fileName)));

        for(int r=0; r<matrix.rows(); r++) {

            Iterator<IndexValue> it = matrix.getRowView(r).getNonZeroIterator();
            while (it.hasNext()) {

                IndexValue indexValue = it.next();
                if(indexValue.getIndex() <= r ) continue;

                writer.write( r +"\t" + (indexValue.getIndex()) + "\t" + indexValue.getValue() );
                writer.newLine();
            }



        }


        writer.flush();
        writer.close();


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
        //divaRecordsToVectors.applyOkapiBM25onVectorList(sparseVectors);

        divaRecordsToVectors.applyTFIDFonVectorList(sparseVectors);
        for(SparseVector v : sparseVectors) v.normalize();


        List<VectorWithID> vectorWithIDS = new ArrayList<>();
        int id = 0;
        for(SparseVector v : sparseVectors) {

            vectorWithIDS.add( new VectorWithID(v,id)  );
            id++;
        }



        System.out.println("Finding k-nearest neighbours in parallel");

        long now = System.currentTimeMillis();


         List<MinMaxPriorityQueue<VectorAndSim>> topKsets =  vectorWithIDS.parallelStream().map( (VectorWithID vector) -> getTopK(vector,vectorWithIDS,10,0.05)   ).collect(Collectors.toList());


        System.out.println("TopK calculations in : " + (System.currentTimeMillis() - now)/1000.0 );


        System.out.println("Finding eps-nearest neighbours in parallel");

        now = System.currentTimeMillis();

        List<Set<VectorAndSim>> topEpsSets =  vectorWithIDS.parallelStream().map( (VectorWithID vector) -> getSimilarThreshold(vector,vectorWithIDS,0.05)   ).collect(Collectors.toList());

        System.out.println("TopEps calculations in : " + (System.currentTimeMillis() - now)/1000.0 );


        // check some results

        /*
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

        */


        System.out.println("Constructing similarity matrix");


       //SparseMatrix sparseSimilarityMatrix = knngToSymetricSimilarityMatrix(topKsets);
       // SparseMatrix sparseSimilarityMatrix = knngToSymetricSimilarityMatrixType2(topKsets);
      //  SparseMatrix sparseSimilarityMatrix = knngToSymetricSimilarityMatrixType3(topKsets);


        SparseMatrix sparseSimilarityMatrix  = epsToSymetricSimilarityMatrix(topEpsSets);

        SparseMatrix sparseSimilarityMatrix2 = knngToSymetricSimilarityMatrixType2(topKsets);

        System.out.println("eps:" +" " + sparseSimilarityMatrix.rows() + " " + sparseSimilarityMatrix.cols() + " " + sparseSimilarityMatrix.isSparce() + " " + sparseSimilarityMatrix.nnz());

        System.out.println("Top10:" +" " + sparseSimilarityMatrix2.rows() + " " + sparseSimilarityMatrix2.cols() + " " + sparseSimilarityMatrix2.isSparce() + " " + sparseSimilarityMatrix2.nnz());


        System.out.println("Second order..");


       SparseMatrix secondOrderSim = getTopKSecondOrder(sparseSimilarityMatrix,15,0.05);

       writeToPajek(secondOrderSim,"pajekSecondOrder.txt");
        writeToNetwork(sparseSimilarityMatrix2,"networkSecondOrder.txt");


        writeToPajek(sparseSimilarityMatrix2,"pajek.net");

        writeToNetwork(sparseSimilarityMatrix2,"network.txt");


        BufferedWriter writer = new BufferedWriter( new FileWriter( new File("bibInfo.txt") ));

        int counter = 0;
        for(Record r : reducedRecorList ) {

            writer.write(counter +"\t" +r.getURI() +"\t"+ r.getTitle() + "\t" +r.getSummary() +"\t" +r.getHostName() );
            writer.newLine();
            counter++;
        }

        writer.flush();
        writer.close();


        //CLUSTERING

        int modularityFunction = 1;
        double resolution = 0.1;
        double resolution2;

        Network network = ModularityOptimizer.convertSparseMatrix(sparseSimilarityMatrix2,modularityFunction);

        resolution2 = ((modularityFunction == 1) ? (resolution / (2 * network.getTotalEdgeWeight() + network.getTotalEdgeWeightSelfLinks())) : resolution);


        long beginTime = System.currentTimeMillis();
        Clustering clustering = null;
        double maxModularity = Double.NEGATIVE_INFINITY;
        Random random = new Random( 0 );
        int nRandomStarts = 5;
        int i,j;
        boolean printOutput = true;
        int nIterations = 10;
        int algorithm = 3;
        boolean update;
        double modularity;
        long endTime;
        VOSClusteringTechnique VOSClusteringTechnique;

        for (i = 0; i < nRandomStarts; i++)
        {
            if (printOutput && (nRandomStarts > 1))
                System.out.format("Random start: %d%n", i + 1);

            VOSClusteringTechnique = new VOSClusteringTechnique(network, resolution2);

            j = 0;
            update = true;
            do
            {
                if (printOutput && (nIterations > 1))
                    System.out.format("Iteration: %d%n", j + 1);

                if (algorithm == 1)
                    update = VOSClusteringTechnique.runLouvainAlgorithm(random);
                else if (algorithm == 2)
                    update = VOSClusteringTechnique.runLouvainAlgorithmWithMultilevelRefinement(random);
                else if (algorithm == 3)
                    VOSClusteringTechnique.runSmartLocalMovingAlgorithm(random);
                j++;

                modularity = VOSClusteringTechnique.calcQualityFunction();

                if (printOutput && (nIterations > 1))
                    System.out.format("Modularity: %.4f%n", modularity);
            }
            while ((j < nIterations) && update);

            if (modularity > maxModularity)
            {
                clustering = VOSClusteringTechnique.getClustering();
                maxModularity = modularity;
            }

            if (printOutput && (nRandomStarts > 1))
            {
                if (nIterations == 1)
                    System.out.format("Modularity: %.4f%n", modularity);
                System.out.println();
            }
        }
        endTime = System.currentTimeMillis();

        if (printOutput)
        {
            if (nRandomStarts == 1)
            {
                if (nIterations > 1)
                    System.out.println();
                System.out.format("Modularity: %.4f%n", maxModularity);
            }
            else
                System.out.format("Maximum modularity in %d random starts: %.4f%n", nRandomStarts, maxModularity);
            System.out.format("Number of communities: %d%n", clustering.getNClusters());
            System.out.format("Elapsed time: %d seconds%n", Math.round((endTime - beginTime) / 1000.0));
            System.out.println();
            System.out.println("Writing output file...");
            System.out.println();
        }

        ModularityOptimizer.writeOutputFile("partition.part2", clustering);



        System.out.println("calculating silhouettes");
        Silhouette silhouette = new Silhouette(sparseSimilarityMatrix2, clustering.cluster);

        List<Integer> indices = new ArrayList<>();

        for(int k=0; k<sparseSimilarityMatrix2.rows(); k++) {

           indices.add(k);


        }

        List<Double> sils = indices.parallelStream().map( silhouette::getSilhouette ).collect( Collectors.toList() );


        double numerator = 0;

        for(int x=0; x<sils.size(); x++) numerator+=sils.get(x);


        System.out.println("Sil 5370 (should be good): " + silhouette.getSilhouette(5370));
        System.out.println("Sil 5265 (should be worse); " + silhouette.getSilhouette(5265));
        System.out.println("Sil 1; " + silhouette.getSilhouette(1));


        System.out.println("Average sil: " +  numerator/sils.size());



        System.out.println("Network: " + network.getNNodes() + " " +network.getTotalEdgeWeight() );

        System.out.format("Number of nodes: %d%n", network.getNNodes());
        System.out.format("Number of edges: %d%n", network.getNEdges());



       // System.out.println(sparseSimilarityMatrix.getRowView(0));

        //List<Double> hej = sparseVectors.parallelStream().map(  (SparseVector v) -> DivaRecordsToVectors.apa(v,sparseVectors)    ).collect(Collectors.toList() );


    }

}
