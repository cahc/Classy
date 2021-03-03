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

public class ClusteringBased {



    public static double cosineSimForNonNormalizedVectors(Vec a , Vec b) {

        double norm_a = a.pNorm(2.0D);
        double norm_b = b.pNorm(2.0D);

        return (a.dot(b) / (norm_a*norm_b));


    }

    public static double cosineSimPreComputedNorms(Vec a , double norm_a, Vec b, double norm_b) {

        //double norm_a = a.pNorm(2.0D);
        //double norm_b = b.pNorm(2.0D);

        return (a.dot(b) / (norm_a*norm_b));


    }


    public static void saveSparseToCluto( List<Vec> vectors ) throws IOException {


        BufferedWriter writer = new BufferedWriter(  new OutputStreamWriter( new FileOutputStream("matrix.clu")) );


        int n = vectors.size();
        int m = vectors.get(0).length();

        int nnz = 0;
        for(Vec v : vectors) {

            nnz = nnz + (((SparseVector)v).nnz());
        }


        writer.write(n + " " + m + " " +nnz);
        writer.newLine();



        for(Vec v : vectors) {

            SparseVector v2 = (SparseVector)v;

            Iterator<IndexValue> iter = v2.getNonZeroIterator();

            boolean first = true;
            while(iter.hasNext()) {

                IndexValue indexValue = iter.next();
                int dim = (indexValue.getIndex() + 1);
                double value = indexValue.getValue();

                if(first) {


                    writer.write(dim + " " + value);
                    first =false;
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
        for(int i=0; i<randomIndices.length; i++) {

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

        for(int j=0; j<vecList.size(); j++) {

            int i = randomIndices[j]; //objects in random order


            if(j==0) {

                ci.add(new IntList() );
                ci.get(0).add(i);

                continue;
            }


            boolean addedToExisting = false;
            Vec v = vecList.get(i);

            for(IntList clusters : ci) {


                    Integer leaderIndice = clusters.get(0); //0 always the "leader"

                    Vec v2 = vecList.get(leaderIndice);

                    if(v.dot(v2) >= simThreshold) {

                        //add v to v2:s cluster
                        //this is very greedy, randomize order after each iteration of the outer loop
                        //todo maybe consider adding to the cluster with maximum sim
                        clusters.add(i);
                        addedToExisting = true;

                        break;
                    }

            }



            if(!addedToExisting) {

                //i becomes a leader in a new cluster

                IntList newCluster = new IntList();
                newCluster.add(i);
                ci.add(newCluster);
            }



            if (j % 500 == 0){

                System.out.println("Proccessed: " + j + " and # groups formed so far: "  + ci.size() );

            }



            //shuffle list so not dependent on order in inner loop. Avoiding small clusters in the end
            //todo shuffle at every n iterations instead
            Collections.shuffle(ci);


        }

        //sort from largest to smallest
        Collections.sort(ci, new IntListLengthComparator());







        System.out.println("Largest: " + ci.get(0).size());
        System.out.println("Smallest: " + ci.get( ci.size()-1).size());


        //now merge clusters so that we can return k centroids to use as initialization of, e.g., a k-means clustering!


        ////////////////////////////////////////////////////////////
        //first set up init centroids based on the K largest clusters
        /////////////////////////////////////////////////////////////

        int dim = vecList.get(0).length();


        List<DenseVector> centroids_k = new ArrayList<>();

        for(int i=0; i<K; i++) {

            centroids_k.add( new DenseVector( dim ) );

        }


        int totalSize1 = 0;

        for(int i=0; i<K; i++) {


            IntList c =ci.get(i);

            totalSize1 = totalSize1 + c.size();


            PrimitiveIterator.OfInt iter = c.streamInts().iterator();

            while(iter.hasNext()) {

                int indice = iter.next();

                centroids_k.get(i).mutableAdd(  vecList.get(indice)   );


            }
        }





        //for the rest of the clusters in ci, add to the closed centroid

          List<SparseVector> toBeMergedCentroids = new ArrayList<>();
            for(int i=K; i<ci.size(); i++) {

            toBeMergedCentroids.add( new SparseVector( dim ) );

            }



         int j= 0;
         for(int i=K; i<ci.size(); i++) {



            IntList c = ci.get(i);

            PrimitiveIterator.OfInt iter = c.streamInts().iterator();


            while(iter.hasNext()) {

                int indice = iter.next();

                toBeMergedCentroids.get(j).mutableAdd(  vecList.get(indice)   );


            }


            j++;

        }




        System.out.println("Merging centroids so we return k=" +K);



        double[] precomputedNormsForVectorsToBeAdded = new double[  toBeMergedCentroids.size()  ];
        for(int i=0; i<toBeMergedCentroids.size(); i++)  precomputedNormsForVectorsToBeAdded[i] = toBeMergedCentroids.get(i).pNorm(2.0D);


        //NOTE THESE MUST BE UPPDATED WHEN WE ADD MERGE A VECTOR!
        double[] precomputedNormsForCentroidVectors = new double[K];
        for(int i=0; i<K; i++) precomputedNormsForCentroidVectors[i] = centroids_k.get(i).pNorm(2.0D);



        for(int z=0; z<toBeMergedCentroids.size(); z++) {


            SparseVector sparseVector = toBeMergedCentroids.get(z);

            int bestIndice = -1;
            double bestSim = 0;

                for(int k=0; k<K; k++ ) {


                    //this is slow as we are normalizing every time..

                    //todo we could cache the norms here to speed stuff up
                    double sim =  cosineSimPreComputedNorms( centroids_k.get(k), precomputedNormsForCentroidVectors[k],  sparseVector, precomputedNormsForVectorsToBeAdded[z]   );


                    if(sim > bestSim) {

                        bestSim = sim;
                        bestIndice = k;


                    }


                }

            //merge z with the best closest centroid


            if(bestIndice != -1) {

                centroids_k.get(bestIndice).mutableAdd( sparseVector );

                //and update norm

                precomputedNormsForCentroidVectors[bestIndice] = centroids_k.get(bestIndice).pNorm(2.0D);



            } else {

                System.out.println("Warning! a to be merged vector had zero similarity with every sum/centroid vector");
                //else ignore
            }


        }



        //now we are done, normalize and return centroid and initial quality




        double Q1 = 0;
        for(int i=0; i<K; i++) {


            Q1 = Q1 + centroids_k.get(i).pNorm(2.0D);

            centroids_k.get(i).normalize();

        }


        System.out.println("Q=" + Q1 + " average similarity: " + (Q1/vecList.size()));



        return centroids_k;


    }

    static class IntListLengthComparator implements Comparator<IntList> {
        @Override
        public int compare(IntList o1, IntList o2) {

            if( o1.size() > o2.size() ) return -1;
            if( o1.size() < o2.size() ) return 1;

            return 0;

        }
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


        System.out.println("dataset size " + classificationDataSet.size());


        //pre-processing step

        boolean preprocessingStep = true;
        boolean externalPartition = false;

        int k = 300;
        int dim = classificationDataSet.getDataVectors().get(0).length(); //indexAndGlobalTermWeights.getNrTerms();
        int N = classificationDataSet.getDataVectors().size();
        List<DenseVector> centroids = null;
        List<Vec> vecList = classificationDataSet.getDataVectors();

        int maxIterations = 15;
        int iter = 0;

        //saveSparseToCluto(vecList);
        //System.exit(0);
        //for(Vec v : vecList) v.normalize();




        if(preprocessingStep && externalPartition) {

            System.out.println("Using external partition as seed");

            BufferedReader reader = new BufferedReader( new FileReader( "matrix.clu.clustering.300"));

            centroids = new ArrayList<>();

            for(int i=0; i<k; i++) {

                centroids.add( new DenseVector(dim) );

            }


            int vecIndice = 0;
            String line;
            while ((line = reader.readLine()) != null) {


                Vec v = vecList.get(vecIndice);
                int clusterIndice = Integer.valueOf(line.trim());

                centroids.get(clusterIndice).mutableAdd( v );


                vecIndice++;


            }


            reader.close();

            //normalize seed

            for(int i=0; i<k; i++) {

                centroids.get(i).normalize();

            }


        }








        if(preprocessingStep && !externalPartition) {
            System.out.println("Running leader follower for quick init");

            centroids = preProcess(vecList, 0.1, k);



        } else if(!preprocessingStep && !externalPartition) {


            centroids = new ArrayList<>();

            for(int i=0; i<k; i++) {

                centroids.add( new DenseVector(dim) );

            }

            //initialize centroids randomly
            for(int i=0; i<k; i++) {
                int randomNum = ThreadLocalRandom.current().nextInt(0, vecList.size());
                centroids.get(i).mutableAdd(  vecList.get(randomNum)  );
            }


        }





        //find closest centroid
        int[] closestCentroid = new int[vecList.size()];

        int[] previousCentroid = new int[vecList.size()];

        for(int i=0; i<previousCentroid.length; i++) previousCentroid[i] = -1; //dummy init




        int[] array= new int[vecList.size()];
        for(int i=0; i<vecList.size(); i++) array[i] = i;



        System.out.println("Now running k-means for " + maxIterations + " iterations");
        System.out.println("N=" + N + " d=" + dim +" k=" +k);

        while(iter < maxIterations) {

            IntStream intArrStream = Arrays.stream(array).parallel();

            System.out.println("Finding closest centroid");
            int finalIter = iter;
            List<DenseVector> finalCentroids = centroids;
            intArrStream.forEach(i->
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
                        if(bestCentrodSoFar != -1) { closestCentroid[i] = bestCentrodSoFar; } else { closestCentroid[i] = ThreadLocalRandom.current().nextInt(0, k);     }


                    }




            );


            /*

            for (int i = 0; i < vecList.size(); i++) {


                double minDot = 0.0;

                int bestCentrodSoFar = ThreadLocalRandom.current().nextInt(0, k);   //random in case of no similarity to any centroid (in the first assignment step)

                for (int j = 0; j < k; j++) {

                    double sim = vecList.get(i).dot(centroids.get(j));

                    if (sim > minDot) {

                        minDot = sim;
                        bestCentrodSoFar = j;

                    }

                } //inner loop ends

                closestCentroid[i] = bestCentrodSoFar;

            } //next vector


*/
            System.out.println("Iter : " + iter);


            //compute number of changes

            int changes =0;

            for(int i=0; i< closestCentroid.length; i++) {

                if(closestCentroid[i] != previousCentroid[i]) changes++;

            }

            System.out.println("objects moved: " + changes);

            for(int i=0; i< closestCentroid.length; i++) {

                previousCentroid[i] = closestCentroid[i];

            }



            System.out.println("updating centroids");
            //new empty
            centroids.clear();
            for(int i=0; i<k; i++) {
                centroids.add( new DenseVector(dim) );
            }
            System.out.println("# centroids: " + centroids.size());
            for (int i = 0; i < closestCentroid.length; i++) {

                centroids.get(closestCentroid[i]).mutableAdd(vecList.get(i));

            }


            System.out.println("Normalizing centroids");
            //also calculate the quality function
            double Q =0;
            for (int i = 0; i < k; i++) {

                Q = Q + centroids.get(i).pNorm(2.0D);

                //todo use the norm here to normalize to avoid double norm norm
                centroids.get(i).normalize();

            }


            /*calculate mean similarity to closest centroid, this is already done by Q, mean sim is Q/N

            System.out.println("Calculating quality function");

            double sim = 0;

            for (int i = 0; i < closestCentroid.length; i++) {

                sim = sim + centroids.get(closestCentroid[i]).dot(vecList.get(i));

            }

            */

            /*calculate mean similarity to random centroid


            double sim2 = 0;

            for (int i = 0; i < closestCentroid.length; i++) {

                int rand = ThreadLocalRandom.current().nextInt(0, k);

                sim2 = sim2 + centroids.get(closestCentroid[rand]).dot(vecList.get(i));

            }

            System.out.println("mean sim to random: " + sim2 / closestCentroid.length);
        */

            System.out.println("Q: " + Q + " " + (Q/closestCentroid.length) );

            System.out.println("##");

            //iterate

            iter++;
        }

        BufferedWriter writer2 = new BufferedWriter( new OutputStreamWriter( new FileOutputStream("apa.txt")) );

        for(int i=0; i<closestCentroid.length; i++) {

            writer2.write( String.valueOf(closestCentroid[i]) );
            writer2.newLine();

        }

        writer2.flush();
        writer2.close();

    }



}
