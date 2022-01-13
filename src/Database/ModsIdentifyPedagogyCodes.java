package Database;

import SwePub.HsvCodeToName;
import SwePub.Record;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModsIdentifyPedagogyCodes {


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

    public static boolean containsLocalUmUPedagogyCodes(Set<Integer> codes) {

        if(codes == null || codes.size() == 0) return false;

        for(Integer i : codes) {

            if( didacticsLocalCodes.contains(i) ) return true;

        }


        return false;

    }

    public static void main(String[] arg) throws IOException, XMLStreamException {


        ModsDivaFileParser modsDivaFileParser = new ModsDivaFileParser();
        List<Record> recordList = modsDivaFileParser.parse( "E:\\2022\\SWEPUB_JSON_20210805_UTBVET\\TAKETWO\\diva_2010-2020.xml" );
        System.out.println("size: " + recordList.size());

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter( new FileOutputStream( new File("E:\\2022\\SWEPUB_JSON_20210805_UTBVET\\TAKETWO\\testParse.txt") ), StandardCharsets.UTF_8));

        int count = 0;
        int count2 = 0;
        Integer ukaPedagogyCode = 503;
        for(Record r : recordList) {


         boolean ukaCodesFound = false;
         Set<Integer> uka = r.getClassificationCodes();
         for(Integer i : uka) {
             Integer level2 = HsvCodeToName.firstThreeDigitsOrNull(i);

             if(ukaPedagogyCode.equals(level2)) {

                 ukaCodesFound = true;
                 break;
             }
         }

         if(ukaCodesFound) count2++;

            boolean localCodesFound = containsLocalUmUPedagogyCodes( r.getLocalUmUClassificationCodes() );
            if(!localCodesFound) continue;

            writer.write(r.getDiva2Id() + "\t" + r.toString());
            writer.newLine();
            count++;
        }

        System.out.println("with local codes: " + count + " with uka: " + count2);
        writer.flush();
        writer.close();



    }

}
