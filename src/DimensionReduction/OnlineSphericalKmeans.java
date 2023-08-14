package DimensionReduction;

import TrainAndPredict.HelperFunctions;
import jsat.classifiers.ClassificationDataSet;
import jsat.linear.DenseVector;
import jsat.linear.SparseVector;
import jsat.linear.Vec;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class OnlineSphericalKmeans {


    private int k;
    private int dim;
    private int batches;
    private List<SparseVector> vecList;
    private int[] partition;
    private List<SphericalCentroid> sphericalCentroidList;

    public static double learningRate(int t,int N, int M) {

        //Exponential decreasing learning rate schedule
        //#n_0 initial (say 1.0), n_f final (say 0.01)
        //# 0 <= t <= N*M  (M number of batch iterations)

        double n_0 = 0.025;
        double n_f = 0.01;


        double n_t =  n_0 *  Math.pow(n_f/n_0,  ((double)t)/(N*M) );

        return(n_t);



    }

    public static double learningRate(int t,int N, int M, double start, double stop) {

        //Exponential decreasing learning rate schedule
        //#n_0 initial (say 1.0), n_f final (say 0.01)
        //# 0 <= t <= N*M  (M number of batch iterations)

        double n_0 = start;
        double n_f = stop;


        double n_t =  n_0 *  Math.pow(n_f/n_0,  ((double)t)/(N*M) );

        return(n_t);



    }


    public int[] getPartition() {

        return this.partition;
    }

    public OnlineSphericalKmeans(List<SparseVector> vecList, int k, int batches) {


        //todo warin if not spare vec
        this.vecList = vecList;
        this.k = k;
        this.dim = vecList.get(0).length();
        this.batches = batches;
        this.partition = new int[vecList.size()];

        int dim = vecList.get(0).length();

        for(int i=0; i<k; i++) partition[i] = -1;


        this.sphericalCentroidList = new ArrayList<>();
        for(int i=0; i<k; i++) this.sphericalCentroidList.add( new SphericalCentroid(dim) );

        //random initialize centroids
        for (int i = 0; i < k; i++) {
            int randomNum = ThreadLocalRandom.current().nextInt(0, vecList.size());
            this.sphericalCentroidList.get(i).add(vecList.get(randomNum), 1.0D); //L2-normalized if the input vectors are L2-normalized (as they should!)
        }


    }


    public OnlineSphericalKmeans(List<SparseVector> vecList, int[] partition, int k, int batches) {


        this.vecList = vecList;
        this.k = k;
        this.dim = vecList.get(0).length();
        this.batches = batches;
        this.partition = partition;
        int dim = vecList.get(0).length();
        this.sphericalCentroidList = new ArrayList<>();


        //initialize centroid based on existing partition
        //todo l1 regularization
        List<DenseVector> temp = new ArrayList<>();
        for(int i=0; i<k; i++) temp.add( new DenseVector(dim));
        for(int i=0; i<partition.length; i++ ) {

            temp.get( partition[i] ).mutableAdd( vecList.get(i) );

        }

        for(DenseVector denseVector : temp) denseVector.normalize();

        for(int i=0; i<k; i++) {

            this.sphericalCentroidList.add( new SphericalCentroid(  temp.get(i) ));
        }


    }


    public void fit() {

        double lr = 0.01; //flat learning rate

        //exponential decreasing learning rate
        int N = this.vecList.size();
        int M = this.batches;
        int t=0;
        //double lr = -1;
        int randomAssign = 0;

        for(int m=0; m<this.batches; m++) {

        System.out.println("Online spherical k-means, batch: " + (m+1));

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


            int clusterSize[] = new int[this.k]; //for each new batch?

            //one batch
            for(int l=0; l<this.vecList.size(); l++) {


                int i=l;

                SparseVector v = this.vecList.get(i);

                double minDot = 0;

                for(int j=0; j<k; j++) {


                    double sim = sphericalCentroidList.get(j).similarity(v);

                    if(sim > minDot) {

                        minDot = sim;
                        this.partition[i] = j;

                    }


                } //for each centroid


                //double lr = 0.01; // / (m+1) ; // flat learning rate per batch iterattion, lenear descreasing

                //lr = learningRate(t,N,M);

                if(this.partition[i] == -1) {

                    //assign randomly

                    clusterSize[randomAssign]++;

                    // double lr = 1.0 / Math.sqrt( clusterSize[partition[i]]++ );
                    //lr = learningRate(t,N,M);


                    int randomNum = ThreadLocalRandom.current().nextInt(0, k );
                    this.partition[i] = randomNum;
                    this.sphericalCentroidList.get(randomNum).add( v ,lr); //or very low learning rate here as it is randomly assign?
                    t++;
                    randomAssign++;



                } else {


                    clusterSize[partition[i]]++;

                    //double lr = 1.0 / Math.sqrt( clusterSize[partition[i]]++ );

                    sphericalCentroidList.get(partition[i]).add( v, lr);
                    t++;
                }



            } //for each object



            System.out.println("Batch completed");
            System.out.println("Random assignments: " + randomAssign);


            //quality function
            DenseVector[] denseVectors = new DenseVector[k];
            for(int z=0; z<denseVectors.length; z++) denseVectors[z] = new DenseVector(dim);
            for(int z=0; z<vecList.size(); z++) {

                // int i = randomIndices[z]; unnessesary

                SparseVector v = vecList.get(z);

                denseVectors[partition[z]].mutableAdd( v );

            }

            double Q = 0;
            for (int i = 0; i < k; i++) {

                Q = Q + denseVectors[i].pNorm(2.0D);

            }

            System.out.println("Q: " + Q + " avg sim: " + (Q / vecList.size()) + " 1-cos(x,cent): " + (vecList.size() - Q) );
            System.out.println("last learning rate: " + lr);

        } // for each batch




    }

    public static void main(String[] arg) throws IOException {

        ClassificationDataSet classificationDataSet = HelperFunctions.readJSATdata("myDataSet.jsat");
        List<SparseVector> vecList = (List<SparseVector>)(Object)classificationDataSet.getDataVectors();

        //use online k-means with  flat  learning  rate to get a rough approximation of the centroids
        //OnlineSphericalKmeans onlineSphericalKmeans = new OnlineSphericalKmeans(vecList,100,2);
        //onlineSphericalKmeans.fit();

        //run batch k-means
        BatchSphericalKmeans batchSphericalKmeans = new BatchSphericalKmeans(vecList, 100, 10,false);
        batchSphericalKmeans.fit();


    }


}
