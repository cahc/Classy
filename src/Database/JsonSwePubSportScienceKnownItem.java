package Database;

import SwePub.Record;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonSwePubSportScienceKnownItem {


    public static void main(String[] arg) throws IOException {


        FileHashDB fileHashDB = new FileHashDB();
        fileHashDB.setPathToFile("E:\\Desktop\\JSON_SWEPUB\\SWEPUB20210421.db");
        fileHashDB.createOrOpenDatabase();

        String file;
        if(arg.length == 0) {

            file = "E:\\Desktop\\JSON_SWEPUB\\SportScienceUris_NEW.txt";

        } else {

            file = arg[0];
        }


        BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream( new File(file) ), StandardCharsets.UTF_8)) ;
        BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( new File("E:\\Desktop\\JSON_SWEPUB\\SportScienceFullRecords_NEW.txt") ), StandardCharsets.UTF_8)) ;

        BufferedWriter writer2 = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( new File("E:\\Desktop\\JSON_SWEPUB\\SportScienceFullRecordsAffilUri_NEW.txt") ), StandardCharsets.UTF_8)) ;
        BufferedWriter writer3 = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( new File("E:\\Desktop\\JSON_SWEPUB\\SportScienceFullRecordsAffilRaw_NEW.txt") ), StandardCharsets.UTF_8)) ;

        HashSet<String> uris = new HashSet<>();

        String uri;
        while ( (uri = reader.readLine()) != null ) {

            String[] splitted = uri.split("\t");

            uris.add( splitted[0].trim() );

        }

        reader.close();

        System.out.println("URIs to consider: " + uris.size());


        for (String uriKey : uris ) {


            Record record = fileHashDB.get(uriKey);

            if(record == null) {System.out.println(uriKey +" not found. This should not happen. Aborting in panic."); fileHashDB.closeDatabase(); System.exit(0); }


                writer.write(record.toString());
                writer.newLine();

                //write out affiliations, URI authorized


                List<String> affiliationUris = record.getAffiliationUris();

                    if(affiliationUris.size() == 0) {

                        writer2.write( record.getURI() +"\t" + null  );
                        writer2.newLine();
                    } else {

                        for(String s : affiliationUris) {

                            writer2.write(record.getURI() + "\t" + s);
                            writer2.newLine();
                        }


                    }

                    //write out affiliations, raw strings for contributing authors


                List<String> affiliationsRawStrings = record.getRawAffiliationsStrings();

                if(affiliationsRawStrings.size() == 0) {

                    writer3.write( record.getURI() +"\t" + null  );
                    writer3.newLine();

                } else {

                    for(String s : affiliationsRawStrings) {

                        writer3.write(record.getURI() + "\t" + s);
                        writer3.newLine();
                    }


                }



        } //next record


        writer.flush();
        writer.close();
        writer2.flush();
        writer2.close();
        writer3.flush();
        writer3.close();


        fileHashDB.closeDatabase();




    }


}
