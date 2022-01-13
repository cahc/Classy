package Database;

import SwePub.Record;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JsonSwePubIdentifyPedagogy {

    private final static HashSet<Integer> didacticsLocalCodes;

    static {

 /*

2147	education	pedagogik
9450	educational work	pedagogiskt arbete
12152	educational leadership	utbildningsledarskap
4300	didactics of mathematics	matematikdidaktik
7650	didactics of biology	biologididaktik
7651	didactics of physics	fysikdidaktik
7652	didactics of chemistry	kemididaktik
7653	didactics of computer science	datavetenskapernas didaktik
7654	didactics of educational measurement	beteendevetenskapliga mätningar
7655	didactics of natural science	naturvetenskapens didaktik
8101	didactics of history	historiedidaktik
12350	history of education	historia med utbildningsvetenskaplig inriktning
21501	language teaching and learning	språkdidaktik




 */


        didacticsLocalCodes = new HashSet<>();
        didacticsLocalCodes.add(2147);
        didacticsLocalCodes.add(9450);
        didacticsLocalCodes.add(12152);
        didacticsLocalCodes.add(4300);
        didacticsLocalCodes.add(7650);
        didacticsLocalCodes.add(7651);
        didacticsLocalCodes.add(7652);
        didacticsLocalCodes.add(7653);
        didacticsLocalCodes.add(7654);
        didacticsLocalCodes.add(7655);
        didacticsLocalCodes.add(8101);
        didacticsLocalCodes.add(12350);
        didacticsLocalCodes.add(21501);


    }

    public boolean containsLocalUmUPedagogyCodes(Set<Integer> codes) {

        if(codes == null || codes.size() == 0) return false;

        for(Integer i : codes) {

            if( this.didacticsLocalCodes.contains(i) ) return true;

        }


        return false;

    }



    public static void main(String[] arg) throws IOException {

        FileHashDB fileHashDB = new FileHashDB();
        fileHashDB.setPathToFile("E:\\SWEPUB_JSON_20210805\\adHocSwePub.db");
        fileHashDB.createOrOpenDatabase();

        JsonSwePubIdentifyPedagogy helperTools = new JsonSwePubIdentifyPedagogy();

        int withSoughtTarget_local = 0;
        int withSoughtTarget_hsv = 0;

        BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( new File("E:\\SWEPUB_JSON_20210805\\ManualClassEducation2010-2020.txt")), StandardCharsets.UTF_8));

        for (Map.Entry<String, Record> entry : fileHashDB.database.entrySet()) {

            Record record = entry.getValue();

            //this should seldom (never?) be the case i guess
            if(record.getPublishedYear() == null) continue;

            //Between 2010 and 2020
            if(record.getPublishedYear().compareTo(2010) < 0 || record.getPublishedYear().compareTo(2020) > 0 ) continue;



           if(  helperTools.containsLocalUmUPedagogyCodes( record.getLocalUmUClassificationCodes() ) ) {

               writer.write("local code!" +"\t"+ record.getMasterURI() + "\t" + record.getAffiliationUris().contains("umu.se") + "\t" + record.getSecondaryUris() + "\t" + record.getUmUUri() );
               writer.newLine();
               //System.out.println(record);
               withSoughtTarget_local++;
           }

            if( record.containsLevel3Classification() ) {

                if(record.isAutoClassedBySwepub() ) continue;

                for(Integer i : record.getClassificationCodes()) {

                    Integer code = JsonSwePubParser.firstThreeDigitsOrNull(i);
                    if(code == null) continue;

                    if(code.equals(503)) {

                        writer.write("HSV" + "\t" + record.getMasterURI() + "\t" + record.getAffiliationUris().contains("umu.se") +"\t" + record.getSecondaryUris() +"\t" + record.getUmUUri());
                        writer.newLine();

                        withSoughtTarget_hsv++;
                        break;
                    }


                } //for each hsv-code

            } //if level 3


        } //for each record

        System.out.println("Number records with local codes:  " + withSoughtTarget_local );
        System.out.println("Number records with manuall hsv coces: " + withSoughtTarget_hsv);
        writer.flush();
        writer.close();

        fileHashDB.closeDatabase();

    }

}
