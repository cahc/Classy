package Database;

import SwePub.Record;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;

public class JsonSwePubSportScienceKnownItem {


    public static void main(String[] arg) throws IOException {


        FileHashDB fileHashDB = new FileHashDB();
        fileHashDB.setPathToFile("E:\\Desktop\\JSON_SWEPUB\\SWEPUB20210408.db");
        fileHashDB.createOrOpenDatabase();

        String file;
        if(arg.length == 0) {

            file = "E:\\Desktop\\JSON_SWEPUB\\SportScienceUris.txt";

        } else {

            file = arg[0];
        }


        BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream( new File(file) ), StandardCharsets.UTF_8)) ;
        BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( new File("E:\\Desktop\\JSON_SWEPUB\\SportScienceFullRecords.txt") ), StandardCharsets.UTF_8)) ;

        HashSet<String> uris = new HashSet<>();

        String uri;
        while ( (uri = reader.readLine()) != null ) {

            String[] splitted = uri.split("\t");

            uris.add( splitted[0].trim() );

        }

        reader.close();

        System.out.println("URIs to consider: " + uris.size());


        for (Map.Entry<Integer, Record> entry : fileHashDB.database.entrySet()) {

            Record record = entry.getValue();

            if( uris.contains( record.getURI()) ) {

                writer.write(record.toString());
                writer.newLine();
            }

        }


        writer.flush();
        writer.close();
        fileHashDB.closeDatabase();




    }


}
