package SimpleTextAnalysis;

import Database.FileHashDB;
import SwePub.Record;
import java.io.IOException;
import java.util.*;

/**
 * Created by Cristian on 2017-04-21.
 */
public class ExtractSignificantTermsFromVOSClustering {


    public static void main(String[] args) throws IOException {


        //DocToCluster docToCluster = new DocToCluster("E:\\Desktop\\JSON_SWEPUB\\SimilarityEngSportSci18clusters.map.txt");
        //String language = "eng";

        DocToCluster docToCluster = new DocToCluster("E:\\Desktop\\JSON_SWEPUB\\SimilaritySWESportSci10clusters.map.txt");
        String language = "swe";

        FileHashDB fileHashDB = new FileHashDB();
        fileHashDB.setPathToFile("E:\\Desktop\\JSON_SWEPUB\\SWEPUB20210411.db");
        fileHashDB.createOrOpenDatabase();

        System.out.println(fileHashDB.size() + " records in database");

        //lookup cluster Lable to get all docsIds
        HashMap<Integer,Set<Integer>> clusterLableToDocs = new HashMap<>();

        //lookup a term to get all docIds containing this term
        HashMap<String,Set<Integer>> termToDocs = new HashMap<>();



        for (Map.Entry<String, Integer> id : docToCluster.getEntrySetDocToClusterLable()) {

            String docLabel = id.getKey();
            //Integer lable = id.getValue();

            Record record = fileHashDB.database.get(docLabel);
            docToCluster.addRecord(record,language);

            //System.out.println(mapId + " " + record.getTitle() + " lable: " + lable);


        }


        System.out.println("Total terms: " + docToCluster.getNrTerms() + " total docs: " + docToCluster.getNrDocs() + " total clusters: " + docToCluster.getCluserIDs().size() );


        for(Integer lable : docToCluster.getCluserIDs()) {


          //  List<TermAndCorr> hello = docToCluster.rankTermsForGivenCluster(lable);

           List<TermAndCorr> hello = docToCluster.rankTermsForGovenClusterTFS(lable,0.5); //LOWER a MORE SPECIFICITY

            System.out.println("Best terms for cluster: " + lable + " cluster size: " + docToCluster.getNrDocsInCluster(lable));


            for (int i = 0; i <= 20; i++) {
                System.out.println(hello.get(i));
            }


            System.out.println();
        }





        fileHashDB.closeDatabase();


    }

}


