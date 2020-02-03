package Playground;

import scopus.AffiliationLevel1;
import scopus.ScopusRecord;
import scopus.SimplePersistor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by crco0001 on 2/16/2018.
 */
public class PlayingAround2 {


    public static void main(String[] arg) throws IOException {


        Integer year = 2015;

        System.out.println(year.compareTo(2016));

        System.exit(0);

        System.out.println("Reading in serialized records..");
        List<ScopusRecord> records = SimplePersistor.deserializeListScopusRecords("records.ser");

        BufferedWriter writer = new BufferedWriter( new FileWriter( new File("affilDistribution.txt")));

        for(ScopusRecord record : records) {

            boolean isEnglish = "eng".equals(record.getLanguage());
            if(!isEnglish) continue;


            Iterator<AffiliationLevel1> level1Affils = record.getAffiliationLevel1List().listIterator();

            while(level1Affils.hasNext()) {

                AffiliationLevel1 affil = level1Affils.next();

               writer.write(record.getEid() +"\t" + affil.getAfid() +"\t" + affil.getAffiliation() );
               writer.newLine();


            }


        }

        writer.flush();
        writer.close();

    }
}
