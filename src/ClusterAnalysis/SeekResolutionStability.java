package ClusterAnalysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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



    public static void main(String[] arg) throws Exception {

        int modularityFunction = 1;

        Network network = ModularityOptimizer.readInputFile(arg[0], 1);


        System.out.format("Number of nodes: %d%n", network.getNNodes());
        System.out.format("Number of edges: %d%n", network.getNEdges());


        List<Double> sequenceOfResParameters = SeekResolutionStability.getSequenceOfResParameters(0.2,3,0.0025 );

        List<int[]> partitions = new ArrayList<>();

        List<Integer> nrClusters = new ArrayList<>();

        int counter = 0;

        for(int k=0; k<sequenceOfResParameters.size(); k++) {

            double resolution =  sequenceOfResParameters.get(k);

            double resolution2 = ((modularityFunction == 1) ? (resolution / (2 * network.getTotalEdgeWeight() + network.totalEdgeWeightSelfLinks)) : resolution);



            Clustering clustering = null;
            double maxModularity = Double.NEGATIVE_INFINITY;
            Random random = new Random(0);
            int nRandomStarts = 5;
            int nIterations = 30;

            int j = 0;
            boolean update;
            boolean printOutput = false;
            double modularity;
            VOSClusteringTechnique vOSClusteringTechnique;

            List<Integer> clusterSizes = new ArrayList<>();

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
                     //    update = vOSClusteringTechnique.runLouvainAlgorithmWithMultilevelRefinement(random);
                    // else if (algorithm == 3)


                    vOSClusteringTechnique.runSmartLocalMovingAlgorithm(random);

                    j++;

                    modularity = vOSClusteringTechnique.calcQualityFunction();

                    if (printOutput && (nIterations > 1))
                        System.out.format("Modularity: %.4f%n", modularity);
                } while ((j < nIterations) && update);






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



           partitions.add(  clustering.getClusters() );



            nrClusters.add( clustering.getNClusters()  );
            //System.out.println("# clusters:" +"\t" +  clustering.getNClusters()  + "\t" + "modularity" + maxModularity + "\t" + "resolution:" + "\t" +  sequenceOfResParameters.get(k));


            System.out.println("Tested: " + resolution);

        } //for each resolution parameter

        //ModularityOptimizer.writeOutputFile("partition2.txt", clustering);


        System.out.println(partitions.size()+ " partitions created");

        System.out.println("Calculating robustness of partition at resolution value eps");

        int delta = 5;

        BufferedWriter writer = new BufferedWriter( new FileWriter( new File("StabilityCheck.txt")));
        writer.write("RESOLUTION\tAVG_IV\tCLUSTERS");
        writer.newLine();

        for(int i=0; i<partitions.size()-delta; i++) {

            List<Double> ivs = new ArrayList<>();
            for (int j = 1; j <= delta; j++) {

                ivs.add(VariationOfInformation.getVI(partitions.get(i), partitions.get(i + j)));
            }


            writer.write( sequenceOfResParameters.get(i) + "\t" + ivs.stream().mapToDouble(Double::doubleValue).average().getAsDouble() +"\t" +  nrClusters.get(i)  );
            writer.newLine();
        }


        writer.flush();
        writer.close();

    } //main ends


}
