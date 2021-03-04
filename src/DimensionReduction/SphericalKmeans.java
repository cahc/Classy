package DimensionReduction;

import Database.FileHashDB;
import Database.IndexAndGlobalTermWeights;
import Database.MyOwnException;
import SwePub.Record;
import TrainAndPredict.HelperFunctions;
import TrainAndPredict.VecHsvPair;
import com.google.common.collect.BiMap;
import jsat.classifiers.CategoricalData;
import jsat.classifiers.ClassificationDataSet;
import jsat.linear.DenseVector;
import jsat.linear.IndexValue;
import jsat.linear.SparseVector;
import jsat.linear.Vec;
import jsat.utils.IntList;
import meka.core.A;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class SphericalKmeans {


    private final int k;
    private final boolean preProcess;
    private final boolean startFromExisting;
    private final int maxIter;

    //find closest centroid, the partition at the end of iterations
    int[] closestCentroid;
    int[] previousCentroid;

    private List<DenseVector> compositeVectors = null;
    private List<Vec> vecList = null;

    public static double cosineSimForNonNormalizedVectors(Vec a, Vec b) {

        double norm_a = a.pNorm(2.0D);
        double norm_b = b.pNorm(2.0D);

        return (a.dot(b) / (norm_a * norm_b));


    }

    public static double cosineSimPreComputedNorms(Vec a, double norm_a, Vec b, double norm_b) {

        //double norm_a = a.pNorm(2.0D);
        //double norm_b = b.pNorm(2.0D);

        return (a.dot(b) / (norm_a * norm_b));


    }


    public static void saveSparseToCluto(List<Vec> vectors) throws IOException {


        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("matrix.clu")));


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


    public static List<DenseVector> preProcess(List<Vec> vecList, double simThreshold, int K) {

        //leader - follower, should be better than random initialization


        ////////////indices in random order//////////////
        int[] randomIndices = new int[vecList.size()];
        for (int i = 0; i < randomIndices.length; i++) {

            randomIndices[i] = i;
        }

        Random rand = new Random();

        for (int i = 0; i < randomIndices.length; i++) {
            int randomIndexToSwap = rand.nextInt(randomIndices.length);
            int temp = randomIndices[randomIndexToSwap];
            randomIndices[randomIndexToSwap] = randomIndices[i];
            randomIndices[i] = temp;
        }


        //////////////////////////////////////////////


        //list of object indices in given cluster
        List<IntList> ci = new ArrayList<>();

        for (int j = 0; j < vecList.size(); j++) {

            int i = randomIndices[j]; //objects in random order


            if (j == 0) {

                ci.add(new IntList());
                ci.get(0).add(i);

                continue;
            }


            boolean addedToExisting = false;
            Vec v = vecList.get(i);

            for (IntList clusters : ci) {


                Integer leaderIndice = clusters.get(0); //0 always the "leader"

                Vec v2 = vecList.get(leaderIndice);

                if (v.dot(v2) >= simThreshold) {

                    //add v to v2:s cluster
                    //this is very greedy, randomize order after each iteration of the outer loop
                    //todo maybe consider adding to the cluster with maximum sim
                    clusters.add(i);
                    addedToExisting = true;

                    break;
                }

            }


            if (!addedToExisting) {

                //i becomes a leader in a new cluster

                IntList newCluster = new IntList();
                newCluster.add(i);
                ci.add(newCluster);
            }


            if (j % 500 == 0) {

                System.out.println("Proccessed: " + j + " and # groups formed so far: " + ci.size());

            }


            //shuffle list so not dependent on order in inner loop. Avoiding small clusters in the end
            //todo shuffle at every n iterations instead
            Collections.shuffle(ci);


        }

        //sort from largest to smallest
        Collections.sort(ci, new IntListLengthComparator());


        System.out.println("Largest: " + ci.get(0).size());
        System.out.println("Smallest: " + ci.get(ci.size() - 1).size());


        //now merge clusters so that we can return k centroids to use as initialization of, e.g., a k-means clustering!


        ////////////////////////////////////////////////////////////
        //first set up init centroids based on the K largest clusters
        /////////////////////////////////////////////////////////////

        int dim = vecList.get(0).length();


        List<DenseVector> centroids_k = new ArrayList<>();

        for (int i = 0; i < K; i++) {

            centroids_k.add(new DenseVector(dim));

        }


        int totalSize1 = 0;

        for (int i = 0; i < K; i++) {


            IntList c = ci.get(i);

            totalSize1 = totalSize1 + c.size();


            PrimitiveIterator.OfInt iter = c.streamInts().iterator();

            while (iter.hasNext()) {

                int indice = iter.next();

                centroids_k.get(i).mutableAdd(vecList.get(indice));


            }
        }


        //for the rest of the clusters in ci, add to the closed centroid

        List<SparseVector> toBeMergedCentroids = new ArrayList<>();
        for (int i = K; i < ci.size(); i++) {

            toBeMergedCentroids.add(new SparseVector(dim));

        }


        int j = 0;
        for (int i = K; i < ci.size(); i++) {


            IntList c = ci.get(i);

            PrimitiveIterator.OfInt iter = c.streamInts().iterator();


            while (iter.hasNext()) {

                int indice = iter.next();

                toBeMergedCentroids.get(j).mutableAdd(vecList.get(indice));


            }


            j++;

        }


        System.out.println("Merging centroids so we return k=" + K);


        double[] precomputedNormsForVectorsToBeAdded = new double[toBeMergedCentroids.size()];
        for (int i = 0; i < toBeMergedCentroids.size(); i++)
            precomputedNormsForVectorsToBeAdded[i] = toBeMergedCentroids.get(i).pNorm(2.0D);


        //NOTE THESE MUST BE UPPDATED WHEN WE ADD MERGE A VECTOR!
        double[] precomputedNormsForCentroidVectors = new double[K];
        for (int i = 0; i < K; i++) precomputedNormsForCentroidVectors[i] = centroids_k.get(i).pNorm(2.0D);


        for (int z = 0; z < toBeMergedCentroids.size(); z++) {


            SparseVector sparseVector = toBeMergedCentroids.get(z);

            int bestIndice = -1;
            double bestSim = 0;

            for (int k = 0; k < K; k++) {


                //this is slow as we are normalizing every time..

                //todo we could cache the norms here to speed stuff up
                double sim = cosineSimPreComputedNorms(centroids_k.get(k), precomputedNormsForCentroidVectors[k], sparseVector, precomputedNormsForVectorsToBeAdded[z]);


                if (sim > bestSim) {

                    bestSim = sim;
                    bestIndice = k;


                }


            }

            //merge z with the best closest centroid


            if (bestIndice != -1) {

                centroids_k.get(bestIndice).mutableAdd(sparseVector);

                //and update norm

                precomputedNormsForCentroidVectors[bestIndice] = centroids_k.get(bestIndice).pNorm(2.0D);


            } else {

                System.out.println("Warning! a to be merged vector had zero similarity with every sum/centroid vector");
                //else ignore
            }


        }


        //now we are done, normalize and return centroid and initial quality


        double Q1 = 0;
        for (int i = 0; i < K; i++) {


            Q1 = Q1 + centroids_k.get(i).pNorm(2.0D);

            centroids_k.get(i).normalize();

        }


        System.out.println("Q=" + Q1 + " average similarity: " + (Q1 / vecList.size()));


        return centroids_k;


    }

    static class IntListLengthComparator implements Comparator<IntList> {
        @Override
        public int compare(IntList o1, IntList o2) {

            if (o1.size() > o2.size()) return -1;
            if (o1.size() < o2.size()) return 1;

            return 0;

        }
    }


    public int[] getPartition() {

        return this.closestCentroid;

    }

    public List<DenseVector> getCentroids() {

        return this.compositeVectors;
    }


    public SphericalKmeans(List<Vec> vecList, String existingPartition, int k, int maxIter, boolean l2normalize) throws IOException {

        this.startFromExisting = true;

        this.k = k;
        this.preProcess = false;
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

        BufferedReader reader = new BufferedReader( new FileReader( existingPartition));

        compositeVectors = new ArrayList<>();

        for(int i=0; i<k; i++) {

            compositeVectors.add( new DenseVector(dim) );

        }


        int vecIndice = 0;
        String line;
        while ((line = reader.readLine()) != null) {


            Vec v = vecList.get(vecIndice);
            int clusterIndice = Integer.valueOf(line.trim());

            compositeVectors.get(clusterIndice).mutableAdd( v );


            vecIndice++;


        }


        reader.close();

        //normalize seed

        for(int i=0; i<k; i++) {

            compositeVectors.get(i).normalize();

        }



    }

    public SphericalKmeans(List<Vec> vecList, int k, boolean preProcess, int maxIter, boolean l2normalize) {

        this.startFromExisting =false;

        this.k = k;
        this.preProcess = preProcess;
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


        if (this.preProcess) {

            this.compositeVectors = preProcess(this.vecList, 0.1, this.k); //L2-normalized


        } else if(!this.startFromExisting) {


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


        //composite vectors initialized (centroids).

        int[] array = new int[this.vecList.size()];
        for (int i = 0; i < this.vecList.size(); i++) array[i] = i;
        int dim = this.vecList.get(0).length(); //indexAndGlobalTermWeights.getNrTerms();
        int N = this.vecList.size();


        System.out.println("Now running k-means for " + this.maxIter + " iterations");
        System.out.println("N=" + N + " d=" + dim + " k=" + this.k);


        ////parallell iterations

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
            System.out.println("# centroids: " + compositeVectors.size());
            for (int i = 0; i < closestCentroid.length; i++) {

                compositeVectors.get(closestCentroid[i]).mutableAdd(vecList.get(i));

            }


            System.out.println("Normalizing centroids");
            //also calculate the quality function
            double Q = 0;
            for (int i = 0; i < k; i++) {

                Q = Q + compositeVectors.get(i).pNorm(2.0D);

                //todo use the norm here to normalize to avoid double norm norm
                compositeVectors.get(i).normalize();

            }


            System.out.println("Q: " + Q + " " + (Q / closestCentroid.length));

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
        List<Vec> vecList = classificationDataSet.getDataVectors();


        SphericalKmeans sphericalKmeans = new SphericalKmeans(vecList, 500, true, 10, false);

        //SphericalKmeans sphericalKmeans = new SphericalKmeans(vecList,"partition.txt",300,5,false);


        sphericalKmeans.fit();

        int[] partition = sphericalKmeans.getPartition();
        List<DenseVector> centroids = sphericalKmeans.getCentroids();

        //test cosine similarity between two centroids

        System.out.println("Self-similarity: " + centroids.get(0).dot( centroids.get(0) )); //should be 1.0
        System.out.println("Similarity between centrod 1 and centroid 2: " + centroids.get(0).dot(centroids.get(1))); //should be low
        System.out.println("Similarity between centrod 1 and centroid 10: " + centroids.get(0).dot(centroids.get(9))); //should be low
        System.out.println("Similarity between centrod 5 and centroid 100: " + centroids.get(4).dot(centroids.get(99))); //should be low


        BufferedWriter writer2 = new BufferedWriter( new OutputStreamWriter( new FileOutputStream("partition_500_run2.txt")) );

        for(int i=0; i<partition.length; i++) {

            writer2.write( String.valueOf(partition[i]) );
            writer2.newLine();

        }

        writer2.flush();
        writer2.close();


    }


}
