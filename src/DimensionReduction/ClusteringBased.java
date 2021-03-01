package DimensionReduction;

import Database.FileHashDB;
import Database.IndexAndGlobalTermWeights;
import Database.MyOwnException;
import SwePub.Record;
import TrainAndPredict.VecHsvPair;
import com.google.common.collect.BiMap;
import jsat.classifiers.CategoricalData;
import jsat.classifiers.ClassificationDataSet;
import jsat.linear.DenseVector;
import jsat.linear.SparseVector;
import jsat.linear.Vec;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class ClusteringBased {




    public static void main(String[] arg) throws MyOwnException, IOException {



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

        for (Map.Entry<Integer, Record> entry : records.database.entrySet()) {

            Record r = entry.getValue();


            if(r.isFullEnglishText() && r.containsLevel5Classification()) {

                VecHsvPair vec = indexAndGlobalTermWeights.trainingDocToVecHsvPair(r);

                if(vec == null) continue;

                SparseVector v = vec.getVector();
                v.normalize();
                classificationDataSet.addDataPoint(v, categoryCodeMapper.get(vec.getHsvCode()));

            }


        }

        System.out.println("dataset size " + classificationDataSet.size());

        System.out.println("Running kmeans" );

          // int[] hej = elkanKMeans.cluster(classificationDataSet,50,null);

         //   System.out.println(hej.length);



        int k = 200;
        int dim = indexAndGlobalTermWeights.getNrTerms();

        List<DenseVector> centroids = new ArrayList<>();

        for(int i=0; i<k; i++) {

            centroids.add( new DenseVector(dim) );

        }

        List<Vec> vecList = classificationDataSet.getDataVectors();
        for(Vec v : vecList) v.normalize();

        //
        //Build initial centroids





        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive

        //initialize centroids randomly
        for(int i=0; i<k; i++) {
            int randomNum = ThreadLocalRandom.current().nextInt(0, vecList.size());
            centroids.get(i).mutableAdd(  vecList.get(randomNum)  );
        }

        //find closest centroid
        int[] closestCentroid = new int[vecList.size()];

        int[] previousCentroid = new int[vecList.size()];

        for(int i=0; i<previousCentroid.length; i++) previousCentroid[i] =i;



        //iterate from here
        int maxIterations = 30;
        int iter = 0;


        int[] array= new int[vecList.size()];
        for(int i=0; i<vecList.size(); i++) array[i] = i;




        while(iter < maxIterations) {

            IntStream intArrStream = Arrays.stream(array).parallel();

            System.out.println("Finding closest centroid");
            int finalIter = iter;
            intArrStream.forEach(i->
                    {

                        double minDot = 0.0;

                        //random in case of no similarity to any centroid (in the first assignment step)
                        int bestCentrodSoFar;
                        if(finalIter ==0) { bestCentrodSoFar = ThreadLocalRandom.current().nextInt(0, k); } else { bestCentrodSoFar = closestCentroid[i]; }

                        for (int j = 0; j < k; j++) {

                            double sim = vecList.get(i).dot(centroids.get(j));

                            if (sim > minDot) {

                                minDot = sim;
                                bestCentrodSoFar = j;

                            }

                        } //inner loop ends

                        closestCentroid[i] = bestCentrodSoFar;

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

        BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream("apa.txt")) );

        for(int i=0; i<closestCentroid.length; i++) {

            writer.write( String.valueOf(closestCentroid[i]) );
            writer.newLine();

        }

        writer.flush();
        writer.close();

    }



}
