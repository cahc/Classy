package Database;

import SwePub.Record;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class TempPlay {

    public static void main(String[] arg) throws IOException {

        Integer yesr = 2020;

        if(yesr.compareTo(2015) < 0 || yesr.compareTo(2020) > 0 ) System.out.println("ignore!");


        FileHashDB fileHashDB = new FileHashDB();
        fileHashDB.setPathToFile("E:\\Desktop\\JSON_SWEPUB\\SWEPUB20210408.db");
        fileHashDB.createOrOpenDatabase();

        BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( new File("E:\\Desktop\\JSON_SWEPUB\\SwePub2012-2020.txt") ), StandardCharsets.UTF_8)) ;

        for (Map.Entry<String, Record> entry : fileHashDB.database.entrySet()) {

            Record record = entry.getValue();

            Integer year = record.getPublishedYear();

            if(year == null) continue;

            if(yesr.compareTo(2012) < 0 || yesr.compareTo(2020) > 0 ) System.out.println("ignore!");


            List<String> uris = record.getAffiliationUris();

            for(String uri : uris) {


                writer.write(record.getURI() + "\t" + year + "\t" + uri +"\t" + record.getPublicationTypes() + "\t" + record.getContentTypes() );
                writer.newLine();

            }


        }

        writer.flush();
        writer.close();
        fileHashDB.closeDatabase();

    }

}
