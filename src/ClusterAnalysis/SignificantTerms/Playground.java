package ClusterAnalysis.SignificantTerms;

import Database.ModsDivaFileParser;
import SwePub.Record;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by crco0001 on 8/28/2017.
 */
public class Playground {


    public static void main(String[] arg) throws IOException, XMLStreamException {


        if (arg.length != 2) {
            System.out.println("mods.xml vosMapp.txt");
            System.exit(0);
        }


        ModsDivaFileParser modsDivaFileParser = new ModsDivaFileParser();

        System.out.println("Parsing XML");
        List<Record> recordList = modsDivaFileParser.parse(arg[0]);


        ArrayList<Record> reducedRecorList = new ArrayList<>();

        for (Record r : recordList) {

            if (r.isFullEnglishText()) reducedRecorList.add(r);

        }

        recordList = null;


        DocToCluster docToCluster = new DocToCluster(arg[1]);

        int counter = 0;
        for (Record rec : reducedRecorList) {

            docToCluster.addRecord(rec, "eng", counter);

            counter++;
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

    }

}



