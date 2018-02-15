package Playground;

import ClusterAnalysis.Clustering;
import ClusterAnalysis.Network;
import ClusterAnalysis.SignificantTerms.DocToCluster;
import ClusterAnalysis.SignificantTerms.TermAndCorr;
import ClusterAnalysis.VOSClusteringTechnique;
import scopus.SimplePersistor;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.*;

/**
 * Created by Cristian on 2017-04-18.
 */
public class PlayingAround {


    public static void main(String[] arg) throws IOException, XMLStreamException, ClassNotFoundException {


        Network network = Network.load("triplet.bin");

        int modularityFunction = 1;
        double resolution =  (arg.length == 2) ? Double.parseDouble(arg[0]) : 1;

        double resolution2 = ((modularityFunction == 1) ? (resolution / (2 * network.getTotalEdgeWeight() + network.getTotalEdgeWeightSelfLinks())) : resolution);
        int iter;
        int randomStarts = 10;

        double maxModularity = -1;
        Clustering clustering = null;

        for(int i=0; i< randomStarts; i++ ) {

            VOSClusteringTechnique vOSClusteringTechnique = new VOSClusteringTechnique(network, resolution2);
            iter = 0;

            do {

                vOSClusteringTechnique.runSmartLocalMovingAlgorithm(new Random());
                iter++;


            } while (50 > iter);


            double modularity = vOSClusteringTechnique.calcQualityFunction();

            if(modularity > maxModularity) {maxModularity = modularity; clustering = vOSClusteringTechnique.getClustering();  }

        }

        System.out.println("Resolution: " + resolution);
        System.out.println("Q + " + maxModularity );
        System.out.println("# clusters: " + clustering.getNClusters());


        //todo here calculate significant terms..

        //docIds goes from 0..N-1

        int[] docIds = new int[ clustering.getNNodes() ];
        for(int i=0; i<docIds.length; i++) docIds[i] = i;

        //DocToCluster docToCluster = new DocToCluster(docIds, clustering.getClusters());

        //DocToCluster docToClusterPubs = new DocToCluster(docIds, clustering.getClusters());

        DocToCluster docToCluster = new DocToCluster("C:\\Users\\crco0001\\Desktop\\PARSE_SCOPUS\\DOWNLOAD\\MAP\\generatePics\\50CKLUSTERS\\map2_1_res01.33.txt" );
        DocToCluster docToClusterPubs = new DocToCluster("C:\\Users\\crco0001\\Desktop\\PARSE_SCOPUS\\DOWNLOAD\\MAP\\generatePics\\50CKLUSTERS\\map2_1_res01.33.txt" );


        List<List<String>> texttokens = SimplePersistor.deserializeListofLists("texttokens.ser");
        List<String> pubtokens = SimplePersistor.deserializeList("pubnames.ser");


        System.out.println("N_1: " + texttokens.size());
        System.out.println("N_2: " + pubtokens.size());

        for(int i=0; i< texttokens.size(); i++) {

            docToCluster.addRecord(texttokens.get(i),i);

        }

        for(int i=0; i< pubtokens.size(); i++) {

            docToClusterPubs.addRecord(pubtokens.get(i),i);

        }


        System.out.println("Number of clusters: " + docToCluster.getCluserIDs().size());

        System.out.println(" ");
        for(Integer j : docToCluster.getCluserIDs()) {

            List<TermAndCorr> goodterms = docToCluster.rankTermsForGivenCluster(j);

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; (i < 12 && i < goodterms.size()); i++) {

                if(i==0) { stringBuilder.append(goodterms.get(i).getTerm()); } else { stringBuilder.append("; ").append(goodterms.get(i).getTerm());}

            }

            System.out.println(stringBuilder.toString() +"\t" +  j + "\t" + docToCluster.getNrDocsInCluster(j));

            System.out.println("");
        }


        System.out.println(" ");
        System.out.println(" ");
        for(Integer j : docToClusterPubs.getCluserIDs()) {

            List<TermAndCorr> goodterms = docToClusterPubs.rankTermsForGivenCluster(j);

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; (i < 5 && i < goodterms.size()); i++) {

                if(i==0) { stringBuilder.append(goodterms.get(i).getTerm()); } else { stringBuilder.append("; ").append(goodterms.get(i).getTerm());}

            }

            System.out.println(stringBuilder.toString() +"\t" +  j + "\t" + docToClusterPubs.getNrDocsInCluster(j));

            System.out.println("");
        }



    }





}
