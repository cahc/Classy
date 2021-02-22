package Database;

import SwePub.Record;
import SwePub.TextAndLang;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import misc.LanguageTools.HelperFunctions;
import misc.LanguageTools.HtmlUnescaper;
import misc.Parsers.SimpleParser;
import misc.Stemmers.EnglishStemmer;
import misc.Stemmers.SwedishStemmer;
import misc.stopwordLists.EnglishStopWords60;
import misc.stopwordLists.SwedishStopWords60;


public class JsonSwePubParser {

    public static final String prefixFromKeyWords = "K@";
    public static final String prefixFromAffiliation = "A@";
    public static final String prefixFromHost = "H@";
    public static final String prefixFromTIABSwe = "TS@";
    public static final String prefixFromTIABEng = "TE@";


    public static Integer firstThreeDigitsOrNull(Integer x) {

        while (x > 999) {
            // while "more than 3 digits", "throw out last digit"
            x = x / 10;
        }


        if(x.compareTo(99) > 0) return x;
        return null; // null if input was less than three digits to begin with
    }




    public static void main(String[] arg) throws IOException {


        SwedishStopWords60 swedishStopWords60 = new SwedishStopWords60();
        EnglishStopWords60 englishStopWords60 = new EnglishStopWords60();

        SwedishStemmer swedishStemmer = new SwedishStemmer();
        EnglishStemmer englishStemmer = new EnglishStemmer();


        BufferedReader reader = new BufferedReader(  new InputStreamReader( new FileInputStream("/Users/cristian/Desktop/JSON_SWEPUB/swepub-deduplicated-2021-02-14.jsonl"), StandardCharsets.UTF_8));
        ObjectMapper mapper = new ObjectMapper();


        String jline;
        int parsed = 0;
        int autoclassed = 0;
        int noSubject = 0;
        int noUka = 0;
        int unsupportedLang = 0;
        int hasLevel2 = 0;
        int hasLevel3 = 0;


        while( (jline = reader.readLine()) != null ) {

            JsonNode root = null;


            /////////////////////////////////


            try {
                root = mapper.readTree(jline);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            if(root == null) continue;

            JsonNode master = root.get("master"); //the master record in case of duplicates. Secondary records in publications[] but hey are of no interest here. No particular order is implied in publications[].


            JsonNode instance = master.get("instanceOf");

            //ignore non text types
            if(!instance.get("@type").asText().equals("Text") ) { continue; }



            String ID = master.get("@id").asText();

            String language = instance.get("language").get(0).get("code").asText();

            //ignore non swedisg or english
            if( !( "swe".equals(language)  || "eng".equals(language) || "und".equals(language)) )  {unsupportedLang++; continue; }



            //now check if there are HSV/SCB classifications codes for the record.
            //make sure it is original classifications and not swepub autoclass.

            JsonNode subject = instance.get("subject");
            if( subject.size() == 0 ) {noSubject++; continue; }

            JsonNode isAutoclassed = subject.findValue("hasNote");

            if(isAutoclassed != null) {


                String info = isAutoclassed.get(0).get("label").asText();


                if("Autoclassified by Swepub".equals(info)) {

                    //System.out.println(ID + " is autoclassed. Ignoring");
                    autoclassed++;
                    continue;

                }



            }


            //now we have non autoclassed records to deal with

            JsonSwePubSubjects swePubSubjects = new JsonSwePubSubjects();

            for(JsonNode node : subject) {


                JsonNode code = node.get("code");

                JsonNode inScheme = node.get("inScheme");


                if(code != null && inScheme != null) {


                    String whichScheme = inScheme.get("code").asText();

                    if(!"uka.se".equals(whichScheme)) continue;



                    Integer classificationCode = code.asInt();

                    //temp fix for erroneous data in SwePub
                    if(classificationCode.equals(20309)) classificationCode = 20399;

                    //210 and 21001 Nanoteknik is the same..
                    if(classificationCode.equals(210)) classificationCode = 21001;


                    if(classificationCode > 999) {

                        //five digits

                        swePubSubjects.addUkaLevel3(classificationCode);
                        Integer level2 = firstThreeDigitsOrNull(classificationCode);
                        swePubSubjects.addUkaLevel2(level2);


                    } else if (classificationCode > 99) {

                        //three digits

                        swePubSubjects.addUkaLevel2(classificationCode);


                    }




                } else {

                    //uncontrolled, there might be language info here

                    String keywords = node.get("prefLabel").asText(); // can be a single keyword or a ; seperated list

                    String[] keywordsArray = keywords.split(";");

                    for(String s : keywordsArray) {

                        swePubSubjects.addKeywords( s.toLowerCase().trim() );

                    }


                }




            } //for each subject node


            //now check if it is useful


            if( !(swePubSubjects.hasLevel3Codes() || swePubSubjects.hasLevel2Codes() ) ) {noUka++; continue; }

            if(swePubSubjects.hasLevel2Codes()) hasLevel2++;
            if(swePubSubjects.hasLevel3Codes()) hasLevel3++;


            ////////////////////////////////////////////////////////
            ////////////////////////////////////////////////////////
            //if here we start to extract other stuff for that goes in to the training record



            Record record = new Record();


            //HSV and uncontrolled keywords
            for(Integer i : swePubSubjects.getUkaLevel2()) record.addClassificationCodes(i);
            for(Integer i : swePubSubjects.getUkaLevel3()) record.addClassificationCodes(i);
            for(String s : swePubSubjects.getKeywords()) record.addUnkontrolledKkeywords(prefixFromKeyWords.concat(s.trim()));


            //get title and maybe alternative title

            JsonNode title  = instance.get("hasTitle");

            for(JsonNode n : title) {

                String main = n.get("mainTitle").asText();

                JsonNode subtitle = n.get("subtitle");
                if (subtitle != null) {

                    main = main + " " + subtitle;
                }


                record.setTitle(HtmlUnescaper.unescapeHTML1(main, 0)); // fix for (incorrectly) escaped å ä ö

            }


            //get summary and mayby alternative title

            JsonNode summary = instance.get("summary");

            if(summary != null) {


                for(JsonNode n : summary) {

                    String AbstractString = n.get("label").asText();

                    record.setSummary(HtmlUnescaper.unescapeHTML1(AbstractString, 0)); //fix for (incorrectly) escaped å ä ö

                }



            }


            //
            //SERIE
            //


            JsonNode hasSeries = master.get("hasSeries");

            if(hasSeries != null) {

                //  TODO
            }



            //
            //Utgivare & år
            //


            JsonNode publisher = master.get("publication");

            if(publisher != null) {

                JsonNode pp = publisher.get(0).get("agent");

                if(pp != null) {

                    String utgivare = pp.get("label").asText();

                    record.setPublisher(utgivare);

                }

                JsonNode year = publisher.get(0).get("date");

                if(year != null) {


                    record.setPublishedYear( year.asInt(-1) );

                }


            }



            //ISBN

            for(JsonNode node : master.get("identifiedBy")) {


                if( "ISBN".equals( node.get("@type").asText() ) )  {


                    String isbn = node.get("value").asText();

                    String extractedISBN = HelperFunctions.extractAndHyphenateISBN(isbn,true);
                    if(extractedISBN != null) record.addISBN( extractedISBN );

                }

            }


            //ISSN and journal/work

            JsonNode partOF = master.get("partOf");


            if(partOF != null ) {

                for (JsonNode node : partOF) {

                    //{"@type":"Work","hasTitle":[{"@type":"Title","volumeNumber":"forthcoming","mainTitle":"The Design Journal"}],"identifiedBy":[{"@type":"ISSN","value":"1460-6925"},{"@type":"ISSN","value":"1756-3062"}]}
                    JsonNode temp = node.get("hasTitle");

                    if(temp != null) record.setHostName( temp.get(0).get("mainTitle").asText() );

                    if(node.get("identifiedBy") != null ) {


                        for(JsonNode n : node.get("identifiedBy")) {


                            if("ISSN".equals( n.get("@type").asText() ))  {


                                record.addIssn(HelperFunctions.extractISSN(  n.get("value").asText()   ));


                            }

                           if("ISBN".equals( n.get("@type").asText() ) )  {


                               String extractedISBN = HelperFunctions.extractAndHyphenateISBN( n.get("value").asText()  ,true);
                               if(extractedISBN != null) record.addISBN( extractedISBN );



                           }



                        } //for each node in identifyed by


                    }


                }


            }




            //now do some language detection! only records that in the XML is tagged with swedish and english is considered
            //this is done as a record can have a, say, a swedish title but a engish abstract

            record.setFinalLanguages();

            //update classification availability information

            record.setClassificationCodesAvaliability();

            //Extract terms from host & Affiliation

            //record.makeFeaturesFromAffiliation();
            record.makeFeaturesFromHost();


            //Extract terms from supported language titles and abstracts

            boolean swe_title = false;
            boolean swe_abstract = false;
            boolean eng_title = false;
            boolean eng_abstract = false;

            if(record.containsSupportedLanguageText()) {

                //PROSES TITLE(S)

                List<TextAndLang> titles = record.getTitle();

                for(TextAndLang tl : titles) {

                    if(tl.getLang().equals("swe")) {

                        swe_title = true;
                        String temp = tl.getText();
                        List<String> list = SimpleParser.parse( temp, true, swedishStopWords60,swedishStemmer );

                        for(String t : list) record.addTermsFromTitleAbstract( prefixFromTIABSwe.concat(t) );



                    }

                    if(tl.getLang().equals("eng")) {

                        eng_title = true;
                        String temp = tl.getText();
                        List<String> list = SimpleParser.parse( temp, true, englishStopWords60, englishStemmer );


                        for(String t : list) record.addTermsFromTitleAbstract( prefixFromTIABEng.concat(t)  );

                    }


                } //loop over titles ends


                //PROCESS ABSTRACTS(S)

                List<TextAndLang> summaries = record.getSummary();

                for(TextAndLang tl : summaries) {

                    if(tl.getLang().equals("swe")) {
                        swe_abstract = true;
                        String temp = tl.getText();
                        List<String> list = SimpleParser.parse( temp, true, swedishStopWords60,swedishStemmer );

                        for(String t : list) { record.addTermsFromTitleAbstract(prefixFromTIABSwe.concat(t)); }


                    }


                    if(tl.getLang().equals("eng")) {
                        eng_abstract = true;
                        String temp = tl.getText();
                        List<String> list = SimpleParser.parse( temp, true, englishStopWords60, englishStemmer );


                        for(String t : list) { record.addTermsFromTitleAbstract(prefixFromTIABEng.concat(t) ); }



                    }

                } //loop over abstracts ends



            } //contain supported language if-construction ends otherwise we don't bother to parse ti/ab


            record.setFullEnglishText( eng_abstract & eng_title  );
            record.setFullSwedishText( swe_abstract & swe_title   );
            record.setContainsEnglish( eng_abstract || eng_title );
            record.setContainsSwedish( swe_abstract || swe_title );



















            //
            //
            //


            //
            //Affiliations TODO
            //
            //dosent make much sense in a scopus type of setup

            /*
            JsonNode affiliations = instance.get("contribution");

            List<String> affiliationStrings = new ArrayList<>();


            //take the name if there is no identifiedBY
            //if there are identifiedBy
            //only
            //@type = Local

            for(JsonNode contributer : affiliations) {


                JsonNode affils = contributer.get("hasAffiliation");

               if(affils != null) {

                   for(JsonNode nn : affils) {


                       JsonNode idby = nn.get("identifiedBy");

                       if(idby == null) {

                         JsonNode af = nn.get("name");
                         if(af != null) affiliationStrings.add( af.asText() );

                       } else {


                          if("Local".equals( idby.get(0).get("@type") )) affiliationStrings.add( nn.get("name").asText()  );

                       }






                   }


               }

            }

            System.out.println(ID + " " + affiliationStrings);


             */



            parsed++;



        } //for earch json line



        System.out.println("Parsed: "  + parsed + " ignoring autoclassed: " + autoclassed + " no subject: " + noSubject + " no level2/3 uka: " + noUka + " unsupported lang: " + unsupportedLang  + " Total: " + (parsed+autoclassed+noSubject+noUka+unsupportedLang ) );

        System.out.println("Has level 3 : " + hasLevel3 + " " + "percent: " + (double)hasLevel3/(parsed+autoclassed+noSubject+noUka+unsupportedLang)  );
        System.out.println("Has level 2 : " + hasLevel2 + " " + "percent: " + (double)hasLevel2/(parsed+autoclassed+noSubject+noUka+unsupportedLang)  );




    }






}
