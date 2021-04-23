package Database;

import SwePub.Record;
import SwePub.TextAndLang;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import misc.LanguageTools.HelperFunctions;
import misc.LanguageTools.HtmlUnescaper;
import misc.Parsers.SimpleParser;
import misc.Stemmers.EnglishStemmer;
import misc.Stemmers.SwedishStemmer;
import misc.stopwordLists.EnglishStopWords60;
import misc.stopwordLists.SwedishStopWords60;

import static Database.JsonAuthorizedUriAffiliations.*;


public class JsonSwePubParser {

    private String pathToJson;

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

    private static void recursiveGetAffils(final JsonNode node, Set<String> affils)  {


        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = node.fields();

        while (fieldsIterator.hasNext()) {
            Map.Entry<String, JsonNode> field = fieldsIterator.next();

            final String key = field.getKey();
            //System.out.println("Key: " + key);
            final JsonNode value = field.getValue();

            if(value.isContainerNode()) {


                Iterator<JsonNode> iter = value.elements();

                while(iter.hasNext()) {

                    recursiveGetAffils(iter.next(),affils); //recursion

                }


            } else {


                if("name".equals(key)) {



                    // System.out.println("key/Value: " + key + " " + value + " " + value.getNodeType());
                    affils.add(value.asText());
                }

            }
        }


    }

    public JsonSwePubParser(String jsonfile) { this.pathToJson = jsonfile;};

    public void parse(FileHashDB db) throws IOException, InterruptedException {
        FileOutputStream f = new FileOutputStream("ErrorLog.txt");
        System.setErr(new PrintStream(f));


        int docs = 0;

        SwedishStopWords60 swedishStopWords60 = new SwedishStopWords60();
        EnglishStopWords60 englishStopWords60 = new EnglishStopWords60();

        SwedishStemmer swedishStemmer = new SwedishStemmer();
        EnglishStemmer englishStemmer = new EnglishStemmer();


        //BufferedReader reader = new BufferedReader(  new InputStreamReader( new FileInputStream("/Users/cristian/Desktop/JSON_SWEPUB/swepub-deduplicated-2021-02-14.jsonl"), StandardCharsets.UTF_8));
        BufferedReader reader = new BufferedReader(  new InputStreamReader( new FileInputStream(this.pathToJson), StandardCharsets.UTF_8));

        ObjectMapper mapper = new ObjectMapper();


        String jline;
        int parsed = 0;
        int autoclassed = 0;
        int noUka = 0;
        int unsupportedLang = 0;
        int hasLevel2 = 0;
        int hasLevel3 = 0;
        int noTextType = 0;


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
            if(!instance.get("@type").asText().equals("Text") ) { noTextType++; continue; }



            String ID = master.get("@id").asText();

            String language = instance.get("language").get(0).get("code").asText();

            //ignore non swedisg or english
            if( !( "swe".equals(language)  || "eng".equals(language) || "und".equals(language)) )  {unsupportedLang++; continue; }


            Record record = new Record();
            record.setURI(ID);

            //Innehållsmärkning: https://id.kb.se/term/swepub/svep/ref.
            //Outputtyp: https://id.kb.se/term/swepub/publication/journal-article.

            for(JsonNode node : instance.get("genreForm")) {

                String id = node.get("@id").textValue();

                if(id.startsWith("https://id.kb.se/term/swepub/svep/")) { record.addContentType( id ); } else if (!id.startsWith("https://id.kb.se/term/swepub/swedishlist")) { record.addPublicationType( id ); }

            }


            /*

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


         */

            JsonSwePubSubjects swePubSubjects = new JsonSwePubSubjects();
            JsonNode subject = instance.get("subject");

            if(subject != null) {
                for (JsonNode node : subject) {


                    JsonNode code = node.get("code");

                    JsonNode inScheme = node.get("inScheme");


                    if (code != null && inScheme != null) {

                        String whichScheme = inScheme.get("code").asText();

                        if (!"uka.se".equals(whichScheme)) continue;


                        Integer classificationCode = code.asInt();

                        //temp fix for erroneous data in SwePub
                        if (classificationCode.equals(20309)) classificationCode = 20399;

                        //210 and 21001 Nanoteknik is the same..
                        if (classificationCode.equals(210)) classificationCode = 21001;


                        if (classificationCode > 999) {

                            //five digits

                            swePubSubjects.addUkaLevel3(classificationCode);
                            Integer level2 = firstThreeDigitsOrNull(classificationCode);
                            swePubSubjects.addUkaLevel2(level2);


                        } else if (classificationCode > 99) {

                            //three digits

                            swePubSubjects.addUkaLevel2(classificationCode);


                        }


                        //check if it is autoclassed
                        JsonNode isAutoclassed = node.get("hasNote");

                        if(isAutoclassed != null) {

                            String info = isAutoclassed.get(0).get("label").asText();

                            if("Autoclassified by Swepub".equals(info)) {


                                record.setAutoClassedBySwepub(true);

                            }

                        }



                    } else {

                        //uncontrolled, there might be language info here

                        String keywords = node.get("prefLabel").asText(); // can be a single keyword or a ; seperated list

                        String[] keywordsArray = keywords.split(";");

                        for (String s : keywordsArray) {

                            swePubSubjects.addKeywords(s.toLowerCase().trim());

                        }


                    }


                } //for each subject node


            }




            //now check if it is useful
            if( !(swePubSubjects.hasLevel3Codes() || swePubSubjects.hasLevel2Codes() ) ) noUka++;
            if(record.isAutoClassedBySwepub()) autoclassed++;

            if(swePubSubjects.hasLevel2Codes()) hasLevel2++;
            if(swePubSubjects.hasLevel3Codes()) hasLevel3++;


            ////////////////////////////////////////////////////////
            ////////////////////////////////////////////////////////
            //if here we start to extract other stuff for that goes in to the training record


            ArrayList l = new ArrayList();
            l.add(language);
            record.setLanguage( l );
            docs++;

            //HSV and uncontrolled keywords
            for(Integer i : swePubSubjects.getUkaLevel2()) record.addClassificationCodes(i);
            for(Integer i : swePubSubjects.getUkaLevel3()) record.addClassificationCodes(i);
            for(String s : swePubSubjects.getKeywords()) record.addUnkontrolledKkeywords(prefixFromKeyWords.concat(s.trim()));


            //get title and maybe alternative title

            JsonNode title  = instance.get("hasTitle");

            for(JsonNode n : title) {

                //"@type":"VariantTitle"
                //can be errornous and only containing (empty) subtitle

               JsonNode m = n.get("mainTitle");

               if(m == null) continue;

               String main = m.asText();

                JsonNode subtitle = n.get("subtitle");
                if (subtitle != null) {

                    main = main + " " + subtitle.asText();
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
            //SERIE, not implemented in the original parsing of XML swepub
            //

            //JsonNode hasSeries = master.get("hasSeries");

            //if(hasSeries != null) {

                //  TODO
           // }



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

                    String extractedISBN = HelperFunctions.extractAndHyphenateISBN(isbn,true, ID);
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


                               String extractedISBN = HelperFunctions.extractAndHyphenateISBN( n.get("value").asText()  ,true,ID );
                               if(extractedISBN != null) record.addISBN( extractedISBN );



                           }



                        } //for each node in identifyed by


                    }


                }


            }

            //
            //Affiliations TODO
            //
            //dosent make much sense in a scopus type of setup


            JsonNode contrib = instance.get("contribution");

            HashSet<String> a = new HashSet<>();

            for(JsonNode n : contrib) {

                JsonNode aff = n.get("hasAffiliation");

                //System.out.println(aff);
                if (aff == null) continue;

                //aff is an array
                //System.out.println(  aff.size() );

                for (JsonNode nn : aff) {

                    recursiveGetAffils(nn,a);

                }

            }


            record.addAffiliations( new ArrayList<>(a) );



            //////////////////////////////////////
            //now get authorized affiliation uris for authors
            //////////////////////////////////////

            List<JsonNode> affilCollector = new ArrayList();
            for (JsonNode contributionObject : contrib) {

                JsonNode agent = contributionObject.get("agent");
                //process only persons and their affiliations
                if (!"Person".equals(agent.get("@type").textValue())) continue;
                //todo process agent object w r t name and local id and OIRCID

                //no affiliation information at all
                if( !contributionObject.has("hasAffiliation") ) continue; //System.out.println("No affiliation information for a person: " + id ); //TODO missing KI here!

                //now get affiliations controlled or "external" to swepub contributors
                //array of objects, Note that hasAffiliations can be an object recursively!
                collectAllHasAffiliationNodes(contributionObject,affilCollector);


            } //for each contributor


            Set<String> authorizedAffilUris = processAllHasAffiliationNodes(affilCollector);
            Set<String> urisToNames = new HashSet<>();

            for(String uri : authorizedAffilUris) {

                String mappedName = mapAffiliationUriToName(uri);

                if(mappedName != null) urisToNames.add(mappedName);
            }

            for(String uri : authorizedAffilUris) record.addAffiliationUris(uri);
            for(String name : urisToNames) record.addAffiliationUrisToName(name);



            //
            //Get raw affiliation strings for each author contributor
            //

            List<String> rawAffiliations = getRawAffiliationsPerAuthor(contrib);

            //    for(String s : rawAffiliations) {
         //       System.out.println(s);
         //   }

            record.addRawAffiliationsStrings(rawAffiliations);


            //now do some language detection! only records that in the XML is tagged with swedish and english is considered
            //this is done as a record can have a, say, a swedish title but a engish abstract

            record.setFinalLanguages();

            //update classification availability information

            record.setClassificationCodesAvaliability();

            //Extract terms from host & Affiliation

            record.makeFeaturesFromAffiliation();
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


            record.setMapDBKey(docs);
            db.put( record.getURI(), record);

           //System.out.println(record.toString());


            parsed++;



        } //for earch json line



        System.out.println("Parsed/saved: "  + parsed + " autoclassed: " + autoclassed + " no level2/3 uka: " + noUka + " unsupported lang: " + unsupportedLang  + " no text type: " + noTextType + " Total in Json: " + (parsed+unsupportedLang+noTextType ) );

        System.out.println("Has level 3 of parsed/saved : " + hasLevel3 + " " + "percent: " + (double)hasLevel3/(parsed)  );
        System.out.println("Has level 2 of parsed/saved: " + hasLevel2 + " " + "percent: " + (double)hasLevel2/(parsed)  );



        reader.close();


    }



    public static void main(String[] arg) throws IOException, InterruptedException {

        System.out.println("Parsing Json and saving to MapBD");

        long start = System.currentTimeMillis();

        FileHashDB fileHashDB = new FileHashDB();
        fileHashDB.setPathToFile("E:\\Desktop\\JSON_SWEPUB\\SWEPUB20210421.db");
        fileHashDB.create();

        JsonSwePubParser jsonSwePubParser = new JsonSwePubParser("E:\\Desktop\\JSON_SWEPUB\\swepub-deduplicated-2021-04-21.jsonl");
        jsonSwePubParser.parse(fileHashDB);

        System.out.println("Records parsed and saved: " + fileHashDB.size() );

        long stop = System.currentTimeMillis();

        System.out.println("Parsed and saved to db in " + (stop - start) / 1000.0 + "seconds");
        fileHashDB.closeDatabase();



    }




}
