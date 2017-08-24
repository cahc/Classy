package ClusterAnalysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by crco0001 on 8/22/2017.
 */
public class SeekResolutionStability {


    public static List<Double> getSequenceOfResParameters(double min, double max,double incr) {

        List<Double> paras = new ArrayList<>();

        double current = min;

        do {

            paras.add(current);

            current += incr;


        } while (current <= max);


        return paras;

    }


    public static long getN(Clustering clustering) {

        int[] nodesPerCluster = clustering.getNNodesPerCluster();

        long N = 0;

        for(int i=0; i<nodesPerCluster.length; i++) N += Math.pow(nodesPerCluster[i],2);

        return N;
    }

    public static void main(String[] arg) throws IOException {

        int modularityFunction = 2;

        Network network = ModularityOptimizer.readInputFile(arg[0], 1);


        System.out.format("Number of nodes: %d%n", network.getNNodes());
        System.out.format("Number of edges: %d%n", network.getNEdges());

        List<Double> testResulutionParas = SeekResolutionStability.getSequenceOfResParameters(0.0,0.0001,0.00000001);

        List<Integer> nrClusters = new ArrayList<>();
        List<Double> objectiveValue = new ArrayList<>();


        for(int k=0; k<testResulutionParas.size(); k++) {

            double resolution =  testResulutionParas.get(k);

            double resolution2 = ((modularityFunction == 1) ? (resolution / (2 * network.getTotalEdgeWeight() + network.totalEdgeWeightSelfLinks)) : resolution);


            long beginTime = System.currentTimeMillis();
            Clustering clustering = null;
            double maxModularity = Double.NEGATIVE_INFINITY;
            Random random = new Random(0);
            int nRandomStarts = 5;
            int nIterations = 10;

            int j = 0;
            boolean update;
            boolean printOutput = false;
            double modularity;
            VOSClusteringTechnique vOSClusteringTechnique;

            for (int i = 0; i < nRandomStarts; i++) {


                if (printOutput && (nRandomStarts > 1)) System.out.format("Random start: %d%n", i + 1);

                vOSClusteringTechnique = new VOSClusteringTechnique(network, resolution2);

                j = 0;
                update = true;

                do {
                    if (printOutput && (nIterations > 1)) System.out.format("Iteration: %d%n", j + 1);

                    //if (algorithm == 1)
                    //    update = vOSClusteringTechnique.runLouvainAlgorithm(random);
                    // else if (algorithm == 2)
                         update = vOSClusteringTechnique.runLouvainAlgorithmWithMultilevelRefinement(random);
                    // else if (algorithm == 3)


                   // vOSClusteringTechnique.runSmartLocalMovingAlgorithm(random);

                    j++;

                    modularity = vOSClusteringTechnique.calcQualityFunction();

                    if (printOutput && (nIterations > 1))
                        System.out.format("Modularity: %.4f%n", modularity);
                }
                while ((j < nIterations) && update);

                if (modularity > maxModularity) {
                    clustering = vOSClusteringTechnique.getClustering();
                    maxModularity = modularity;
                }

                if (printOutput && (nRandomStarts > 1)) {
                    if (nIterations == 1)
                        System.out.format("Modularity: %.4f%n", modularity);
                    System.out.println();
                }

            } // for each iteration


            long endTime = System.currentTimeMillis();

        /*
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


        */


            System.out.println("# clusters:" +"\t" +clustering.getNClusters() + "\t" + "Nsq:" + "\t" + getN(clustering) + "\t" +clustering.getNClusters() + "\t" + "modularity" + maxModularity + "\t" + "resolution" + "\t" +  testResulutionParas.get(k));


        } //for each resolution parameter

        //ModularityOptimizer.writeOutputFile("partition2.txt", clustering);




    }


}
