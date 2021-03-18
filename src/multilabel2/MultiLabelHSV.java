package multilabel2;

import Database.FileHashDB;
import Database.JsonSwePubParser;
import SwePub.Record;
import jsat.linear.SparseVector;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MultiLabelHSV {

    public static void main(String[] arg) throws IOException, InterruptedException {

        /////////////////////////////////////////////////////
                    //PARSE RECORDS AND SAVE TO DB//
        /////////////////////////////////////////////////////
   /*
        long start = System.currentTimeMillis();
        FileHashDB fileHashDB = new FileHashDB();
        fileHashDB.setPathToFile("E:\\swepub_json_20210214\\SwePubJson.db");
        fileHashDB.create();
        JsonSwePubParser jsonSwePubParser = new JsonSwePubParser("E:\\swepub_json_20210214\\swepub-deduplicated-2021-02-21.jsonl");
        jsonSwePubParser.parse(fileHashDB);
        System.out.println("Records parsed and saved: " + fileHashDB.size() );
        long stop = System.currentTimeMillis();
        System.out.println("Parsed and saved to db in " + (stop - start) / 1000.0 + "seconds");
        fileHashDB.closeDatabase();
'*/


        /////////////////////////////////////////////////////////////
        ////CREATE INDEX DEPENTENT ON CLASSIFYER LEVEL AND LANGUAGE//
        ////////////////////////////////////////////////////////////

        //level 3 and english
        Index index = new Index(10000);

        FileHashDB fileHashDB2 = new FileHashDB();
        fileHashDB2.setPathToFile("E:\\swepub_json_20210214\\SwePubJson.db");
        fileHashDB2.createOrOpenDatabase();

        int total = 0;
        for (Map.Entry<Integer, Record> entry : fileHashDB2.database.entrySet()) {

            Record record = entry.getValue();
            if( !(record.containsLevel5Classification() && record.isFullSwedishText() ) ) continue;

            List<String> terms = record.getLanguageSpecificTerms("swe");
            terms.addAll( record.getTermsFromAffiliation() );
            terms.addAll( record.getTermsFromHost()  );
            terms.addAll( record.getUnkontrolledKkeywords() );
            String ISBN = record.getISBN();
            if(ISBN != null) terms.add( ISBN  );
            terms.addAll( record.getIssn());
            index.addTokens(terms);
            total++;

        }


        System.out.println("total records used: " + total + " index size: " + index.size());

        index.removeRareTerms(3);

        System.out.println("0 " + index.reverseLookupSlow(0));
        System.out.println("1146997 " + index.reverseLookupSlow(1146997));
        System.out.println("614367 " + index.reverseLookupSlow(614367));
        System.out.println("671103 " + index.reverseLookupSlow(671103));
        fileHashDB2.closeDatabase();











    }



}
