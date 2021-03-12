package DimensionReduction;

import TrainAndPredict.HelperFunctions;
import jsat.classifiers.ClassificationDataSet;
import jsat.linear.DenseVector;
import jsat.linear.IndexValue;
import jsat.linear.SparseVector;
import jsat.linear.Vec;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class BatchSphericalKmeans {


    private final int k;
    private final boolean startFromExisting;
    private final int maxIter;

    //find closest centroid, the partition at the end of iterations
    int[] closestCentroid;
    int[] previousCentroid;

    private List<DenseVector> compositeVectors = null;
    private List<SparseVector> vecList = null;


    public static List<SparseVector> readSparseFromCluto(String fileName) throws IOException {

        //note cluto files use 1-based indices
        List<SparseVector> vectorList = new ArrayList<>();

        BufferedReader reader = new BufferedReader(  new FileReader(fileName)  );

        String firstline = reader.readLine();

        String[] meta = firstline.trim().split("\\s+");

        if(meta.length != 3) {System.out.println("wrong header"); System.exit(0); }


        int n = Integer.valueOf( meta[0] );
        int m = Integer.valueOf( meta[1] );
        int nnz = Integer.valueOf( meta[2 ]);

        String line;
        while( (line = reader.readLine()) != null ) {

            line = line.trim();


            String[] incidesValues = line.split("\\s+");

            SparseVector sparseVector = new SparseVector(m,20);

            for(int i=0; i<incidesValues.length; i += 2) {

               // System.out.println(incidesValues[i] + " --> " + incidesValues[i+1]);

                sparseVector.set( Integer.valueOf( incidesValues[i] )-1, Double.valueOf( incidesValues[i+1] ) );


            }

            vectorList.add(sparseVector);

        }


        reader.close();
        return vectorList;

    }

    public static void saveSparseToCluto(List<SparseVector> vectors, String filename) throws IOException {


        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename)));


        int n = vectors.size();
        int m = vectors.get(0).length();

        int nnz = 0;
        for (Vec v : vectors) {

            nnz = nnz + (((SparseVector) v).nnz());
        }


        writer.write(n + " " + m + " " + nnz);
        writer.newLine();


        for (Vec v : vectors) {

            SparseVector v2 = (SparseVector) v;

            Iterator<IndexValue> iter = v2.getNonZeroIterator();

            boolean first = true;
            while (iter.hasNext()) {

                IndexValue indexValue = iter.next();
                int dim = (indexValue.getIndex() + 1);
                double value = indexValue.getValue();

                if (first) {


                    writer.write(dim + " " + value);
                    first = false;
                } else {

                    writer.write(" " + dim + " " + value);
                }


            }

            writer.newLine();

        }


        writer.flush();
        writer.close();

    }


    public int[] getPartition() {

        return this.closestCentroid;

    }

    public List<DenseVector> getCentroids() {

        return this.compositeVectors;
    }


    public BatchSphericalKmeans(List<SparseVector> vecList, int[] partition, int k, int maxIter, boolean l2normalize) throws IOException {

        this.startFromExisting = true;

        if(vecList.size() != partition.length) {System.out.println("Wrong initial partition length"); System.exit(0); }

        this.k = k;
        this.maxIter = maxIter;
        this.closestCentroid = new int[vecList.size()];
        this.previousCentroid = new int[vecList.size()];
        this.vecList = vecList;

        for (int i = 0; i < previousCentroid.length; i++) previousCentroid[i] = -1; //dummy init


        if (l2normalize) {


            for (int i = 0; i < vecList.size(); i++) vecList.get(i).normalize();

        }



        int dim = this.vecList.get(0).length();


        System.out.println("Using external partition as seed");


        compositeVectors = new ArrayList<>();

        for(int i=0; i<k; i++) {

            compositeVectors.add( new DenseVector(dim) );

        }



        for (int i = 0; i<partition.length; i++ ) {


            Vec v = vecList.get(i);


            int clusterIndice = partition[i];

            compositeVectors.get(clusterIndice).mutableAdd( v );


        }


        //normalize seed

        for(int i=0; i<k; i++) {

            compositeVectors.get(i).normalize();

        }



    }

    public BatchSphericalKmeans(List<SparseVector> vecList, int k, int maxIter, boolean l2normalize) {

        this.startFromExisting =false;

        this.k = k;
        this.maxIter = maxIter;
        this.closestCentroid = new int[vecList.size()];
        this.previousCentroid = new int[vecList.size()];
        this.vecList = vecList;
        for (int i = 0; i < previousCentroid.length; i++) previousCentroid[i] = -1; //dummy init


        if (l2normalize) {


            for (int i = 0; i < vecList.size(); i++) vecList.get(i).normalize();

        }

    }


    public void fit() {



        //random initialization
        if(!this.startFromExisting) {


            int dim = vecList.get(0).length();


            this.compositeVectors = new ArrayList<>();

            for (int i = 0; i < this.k; i++) {

                this.compositeVectors.add(new DenseVector(dim));

            }


            for (int i = 0; i < k; i++) {
                int randomNum = ThreadLocalRandom.current().nextInt(0, this.vecList.size());
                this.compositeVectors.get(i).mutableAdd(this.vecList.get(randomNum)); //L2-normalized if the input vectors are L2-normalized (as they should!)
            }


        } else {

            //composite vectors already initialized

        }



        int[] array = new int[this.vecList.size()];
        for (int i = 0; i < this.vecList.size(); i++) array[i] = i;
        int dim = this.vecList.get(0).length(); //indexAndGlobalTermWeights.getNrTerms();
        int N = this.vecList.size();


        System.out.println("Now running batch k-means for " + this.maxIter + " iterations");
        System.out.println("N=" + N + " d=" + dim + " k=" + this.k);


        ////parallel iterations

        int iter = 1;

        while (iter <= this.maxIter) {

            IntStream intArrStream = Arrays.stream(array).parallel();

            System.out.println("Finding closest centroid");
            List<DenseVector> finalCentroids = compositeVectors;

            intArrStream.forEach(i ->
                    {

                        double minDot = 0.0;

                        int bestCentrodSoFar = -1;

                        for (int j = 0; j < k; j++) {

                            double sim = vecList.get(i).dot(finalCentroids.get(j));

                            if (sim > minDot) {

                                minDot = sim;
                                bestCentrodSoFar = j;

                            }

                        } //inner loop ends


                        //assign object to a random cluster if it has zero sim to any centroid
                        if (bestCentrodSoFar != -1) {
                            closestCentroid[i] = bestCentrodSoFar;
                        } else {
                            closestCentroid[i] = ThreadLocalRandom.current().nextInt(0, k);
                        }


                    }


            );


            System.out.println("Iter : " + iter);


            //compute number of changes

            int changes = 0;

            for (int i = 0; i < closestCentroid.length; i++) {

                if (closestCentroid[i] != previousCentroid[i]) changes++;

            }

            System.out.println("objects moved: " + changes);

            for (int i = 0; i < closestCentroid.length; i++) {

                previousCentroid[i] = closestCentroid[i];

            }


            System.out.println("updating centroids");
            //new empty
            compositeVectors.clear();
            for (int i = 0; i < k; i++) {
                compositeVectors.add(new DenseVector(dim));
            }

            int[] clusterSize = new int[k];

            System.out.println("# centroids: " + compositeVectors.size());
            for (int i = 0; i < closestCentroid.length; i++) {

                compositeVectors.get(closestCentroid[i]).mutableAdd(vecList.get(i));

                clusterSize[ closestCentroid[i]   ]++;
            }


            System.out.println("Normalizing centroids");
            //also calculate the quality function
            double Q = 0;
            for (int i = 0; i < k; i++) {

                double norm = compositeVectors.get(i).pNorm(2.0D);

                Q = Q + norm;
                compositeVectors.get(i).mutableDivide(Math.max(norm, 1.0E-10D));

            }


            System.out.println("Q: " + Q + " avg sim: " + (Q / closestCentroid.length) + " 1-cos(x,cent): " + (vecList.size() - Q) );
            System.out.println("cluster size: " + Arrays.toString(clusterSize));
            //todo sparsifying






            System.out.println("##");

            iter++;
        }


        // closestCentroid is the partition

        // compositeVectors is now the centroids


    }


    public static void main(String[] arg) throws IOException {


/*

        String lang = "eng";
        int level = 5;


        IndexAndGlobalTermWeights indexAndGlobalTermWeights = new IndexAndGlobalTermWeights(lang, level);
        indexAndGlobalTermWeights.readFromMapDB("E:\\swepub_json_20210214\\");

        //EntropyTermWeights entropyTermWeights = new EntropyTermWeights(lang, level );
        //entropyTermWeights.readFromMapDB("E:\\swepub_json_20210214\\");



        FileHashDB records = new FileHashDB();
        records.setPathToFile("E:\\swepub_json_20210214\\Records.db");
        records.createOrOpenDatabase();
        System.out.println("data set: " + records.size());


        BiMap<Integer, Integer> categoryCodeMapper = IndexAndGlobalTermWeights.level5ToCategoryCodes;
        CategoricalData categoryInformation = IndexAndGlobalTermWeights.level5CategoryInformation;
        ClassificationDataSet classificationDataSet = new ClassificationDataSet(indexAndGlobalTermWeights.getNrTerms(), new CategoricalData[]{}, categoryInformation);

        BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(" textRep.jsat")) );

        System.out.println("creating normalized vectors");

        for (Map.Entry<Integer, Record> entry : records.database.entrySet()) {

            Record r = entry.getValue();


            if(r.isFullEnglishText() && r.containsLevel5Classification()) {

                VecHsvPair vec = indexAndGlobalTermWeights.trainingDocToVecHsvPair(r);

                if(vec == null) continue;

                writer.write(r.toString());
                writer.newLine();

                SparseVector v = vec.getVector();
                v.normalize();
                classificationDataSet.addDataPoint(v, categoryCodeMapper.get(vec.getHsvCode()));

            }


        }

        writer.flush();
        writer.close();

        System.out.println("Writing to file..");
        HelperFunctions.writeJSATdata(classificationDataSet,"myDataSet.jsat");

        records.closeDatabase();
        System.exit(0);




        */


        ClassificationDataSet classificationDataSet = HelperFunctions.readJSATdata("myDataSet.jsat");
        List<SparseVector> sparseVectors = (List<SparseVector>)(Object)classificationDataSet.getDataVectors();



        BatchSphericalKmeans sphericalKmeans = new BatchSphericalKmeans(sparseVectors, 100, 3, false);
        sphericalKmeans.fit();


        //test cosine similarity between two centroids

        /*
        System.out.println("Self-similarity: " + centroids.get(0).dot( centroids.get(0) )); //should be 1.0
        System.out.println("Similarity between centrod 1 and centroid 2: " + centroids.get(0).dot(centroids.get(1))); //should be low
        System.out.println("Similarity between centrod 1 and centroid 10: " + centroids.get(0).dot(centroids.get(9))); //should be low
        System.out.println("Similarity between centrod 5 and centroid 100: " + centroids.get(4).dot(centroids.get(99))); //should be low


        BufferedWriter writer2 = new BufferedWriter( new OutputStreamWriter( new FileOutputStream("partition_1000_run1.txt")) );

        for(int i=0; i<partition.length; i++) {

            writer2.write( String.valueOf(partition[i]) );
            writer2.newLine();

        }

        writer2.flush();
        writer2.close();



        int[] partition = sphericalKmeans.getPartition();
        List<DenseVector> centroids =  sphericalKmeans.getCentroids();

        //1-cosine vale

        double val = 0;
        for(int i=0; i<vecList.size(); i++) {

            double dist = 1.0D - vecList.get(i).dot( centroids.get( partition[i]  ) );

            val = val + dist;

        }


        System.out.println(" sum 1-cos(x, centroid): " + val);


        BufferedWriter writer = new BufferedWriter( new FileWriter( new File("partition_test1.txt")));
        for(int i=0; i<partition.length; i++) {

            writer.write( partition[i] );
            writer.newLine();


        }

        writer.flush();
        writer.close();



        */



    }


}
