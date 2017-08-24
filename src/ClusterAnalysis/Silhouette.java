package ClusterAnalysis;

import jsat.linear.IndexValue;
import jsat.linear.SparseMatrix;
import jsat.linear.SparseVector;
import jsat.utils.IntSet;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by Cristian on 2017-08-23.
 */
public class Silhouette {

    int nrClusters;
    int[] clusterSize;
    IntSet[] clusterToNodeSet;
    SparseMatrix similarityMatrix;
    int[] partition;
    //silhouette values for each node
    double[] s;

    public Silhouette(SparseMatrix similarityMatrix, int[] partition) {

        this.similarityMatrix = similarityMatrix;
        this.partition = partition;

        if (similarityMatrix.rows() != similarityMatrix.cols()) {
            System.out.println("not symmetric matrix");
            System.exit(0);
        }
        if (similarityMatrix.rows() != partition.length) {
            System.out.println("partition length 1= nr rows in sim matrix");
            System.exit(0);
        }

        nrClusters = Arrays.stream(partition).max().getAsInt() + 1; //zero based
        clusterToNodeSet = new IntSet[nrClusters];
        clusterSize = new int[nrClusters];

        for (int i = 0; i < clusterToNodeSet.length; i++) clusterToNodeSet[i] = new IntSet(20);

        for (int i = 0; i < partition.length; i++) {


            int clu = partition[i];

            clusterToNodeSet[clu].add(i);

        }

        for (int i = 0; i < clusterToNodeSet.length; i++) clusterSize[i] = clusterToNodeSet[i].size();
        s = new double[similarityMatrix.rows()];

    }


    //todo don't control for self loops

    private double getAverageSim(int i, int clu) {

        //a(i) average similarity between i and the nodes in i:s cluster

        double similarity = 0;

        //int cluster = partition[i];

        int cluster = clu;

        IntSet nodesInCluster = clusterToNodeSet[cluster];

        SparseVector sims = (SparseVector) similarityMatrix.getRowView(i);

        Iterator<IndexValue> iterator = sims.getNonZeroIterator();

        while (iterator.hasNext()) {

            IndexValue indexValue = iterator.next();

            if (nodesInCluster.contains(indexValue.getIndex())) similarity += indexValue.getValue();


        }

        double a = similarity / nodesInCluster.size(); // average similarity between i and nodes in cluster


        return a;

    }

    public double getAi(int i) {



        int cluster = partition[i];

        return getAverageSim(i,cluster);

    }

    public double getBi(int i) {

        //now for b..
        //scan all potential clusters, a potential cluster is a cluster that contains at least one node with sim > 0 to i

        IntSet potentialClusters = new IntSet(10);

        Iterator<IndexValue> indexValueIterator = similarityMatrix.getRowView(i).iterator();

        while (indexValueIterator.hasNext()) {

            potentialClusters.add(  partition[ indexValueIterator.next().getIndex() ]     );


        }

        double maxAvgSim = 0;


        Iterator<Integer> potentials = potentialClusters.iterator();

        while (potentials.hasNext()) {


           double sim = getAverageSim(i,potentials.next().intValue() );

           if(sim > maxAvgSim) maxAvgSim = sim;
        }



        return maxAvgSim;

    }

    public double getSilhouette(int i) {

        int clusterSize = this.clusterToNodeSet[ partition[i]  ].size();

        if(clusterSize == 1) return 0;

        double a = getAi(i);
        double b = getBi(i);


        return (a-b)/Math.max(a,b);


    }





}