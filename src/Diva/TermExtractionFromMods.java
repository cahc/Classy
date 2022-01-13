package Diva;

import Database.FileHashDB;
import Database.ModsDivaFileParser;
import SwePub.Record;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.List;
import java.util.Map;


/**
 * Created by crco0001 on 8/10/2018.
 */
public class TermExtractionFromMods {


    public static void main(String[] arg) throws IOException, XMLStreamException {


        if(arg.length != 1) {System.out.println("supply mods file.."); System.exit(0); }

        FileHashDB fileHashDB = new FileHashDB();
        fileHashDB.create();

        ModsDivaFileParser modsDivaFileParser = new ModsDivaFileParser();
        List<Record> recordList = modsDivaFileParser.parse(arg[0]);

        for(Record r : recordList) fileHashDB.put(r.getMasterURI(),r);

        System.out.println("Records parsed and saved: " + fileHashDB.size() );


        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(new File("RecordOutput.txt")), "UTF-8") );
        System.out.println("Starting to read and write..");

        long start = System.currentTimeMillis();
        int docs = 0;

        //OBS; NOT IN CREATION ORDER..
        for (Map.Entry<String, Record> entry : fileHashDB.database.entrySet()) {

            docs++;
            writer.write( entry.getValue().toString() ); writer.newLine();  //regex fox new lines and other shit

        }




        writer.flush();
        writer.close();


        long stop = System.currentTimeMillis();
        System.out.println("Read " + docs + "in: " + (stop - start) / 1000.0 + "seconds");
        fileHashDB.closeDatabase();



    }


}
