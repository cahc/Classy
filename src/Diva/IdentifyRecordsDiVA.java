package Diva;

import Database.ModsDivaFileParser;
import SwePub.HsvCodeToName;
import SwePub.Record;

import javax.xml.stream.XMLStreamException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IdentifyRecordsDiVA {


    public static void main(String[] arg) throws IOException, XMLStreamException {

        if(arg.length != 1) {System.out.println("Supply mods input file.."); System.exit(0); }

        ModsDivaFileParser modsDivaFileParser = new ModsDivaFileParser();
        List<Record> recordList = modsDivaFileParser.parse( arg[0]);
        System.out.println("size: " + recordList.size());
        BufferedWriter writer = new BufferedWriter( new FileWriter( new File("ManualResult.txt") ));
        System.out.println("Parsed: " + recordList.size());


        for(Record r : recordList) {

            //todo but what about local classification stuff?
            Set<Integer> level2 = new HashSet<>();

           for(Integer i : r.getClassificationCodes() ) {

               Integer code = HsvCodeToName.firstThreeDigitsOrNull(  i );

               if(code != null) level2.add(i);

           }

           if( level2.contains(503) ) {


               writer.write(r.getDiva2Id()+"\t" + r.getMasterURI() + "\t" + r.getLanguage() +"\t" + r.getClassificationCodes() + "\t" + r.getTitle());
               writer.newLine();
           }


        }


        writer.flush();
        writer.close();

    }
}
