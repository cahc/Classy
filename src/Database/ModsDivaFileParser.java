package Database;

/**
 * Created by Cristian on 2017-06-16.
 */

import SwePub.Record;
import SwePub.TextAndLang;
import misc.LanguageTools.HelperFunctions;
import misc.LanguageTools.HtmlUnescaper;
import misc.Parsers.SimpleParser;
import misc.Stemmers.EnglishStemmer;
import misc.Stemmers.SwedishStemmer;
import misc.stopwordLists.EnglishStopWords60;
import misc.stopwordLists.SwedishStopWords60;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Cristian on 2016-12-08.
 */
public class ModsDivaFileParser {

    public static final String prefixFromAffiliation = "A@"; // record.makeFeaturesFromAffiliation();

    public static final String prefixFromKeyWords = "K@";
    public static final String prefixFromHost = "H@";  // record.makeFeaturesFromHost();
    public static final String prefixFromTIABSwe = "TS@";
    public static final String prefixFromTIABEng = "TE@";

    public static Pattern wordPattern = Pattern.compile("[^\\(\\)\\)\\]\\[ \\-,\\:\\.]{2,50}");

    public static int depth;

    public static ArrayList<String> getRecordContentSourceAndDiva2ID(XMLEventReader r, Record record) throws XMLStreamException {

        ArrayList<String> organisationIdentifyer = new ArrayList<>(1);
        //recordContentSource is Mandatory

        while (true) {

            XMLEvent event = r.nextEvent();

            boolean isStart = event.isStartElement();
            if (isStart) depth++;

            boolean isEnd = event.isEndElement();
            if (isEnd) depth--;


            if (isStart && event.asStartElement().getName().getLocalPart().equals("recordContentSource")) {


                organisationIdentifyer.add(r.getElementText());
                //now at END ELEMENT
                depth--;
            }

            if (isStart && event.asStartElement().getName().getLocalPart().equals("recordIdentifier")) {

                String diva2Id = r.getElementText().toLowerCase();

                //now at END ELEMENT
                record.setDiva2Id(diva2Id);
                depth--;

            }




            if (isEnd && event.asEndElement().getName().getLocalPart().equals("recordInfo")) break;
        }


        return organisationIdentifyer;

    }

    public static void setYearAndPublisher(XMLEventReader r, Record record) throws XMLStreamException {

        //DateIssued is Mandatory if the publication is formally published

        Integer year = -99; //missing publication year


        while (true) {

            XMLEvent event = r.nextEvent();

            boolean isStart = event.isStartElement();
            if (isStart) depth++;

            boolean isEnd = event.isEndElement();
            if (isEnd) depth--;


            if (isStart && event.asStartElement().getName().getLocalPart().equals("dateIssued")) {

                try {
                    year = Integer.valueOf(r.getElementText().substring(0, 4)); //ISO 8691 4 Digits

                } catch (NumberFormatException e) {
                    //   System.err.println("Erroneous year information");
                    year = -1;

                } catch (StringIndexOutOfBoundsException e) {

                    //  System.err.println("Erroneous year information");
                    year = -1;

                } finally {

                    //now at END ELEMENT
                    depth--;
                }

            }


            if (isStart && event.asStartElement().getName().getLocalPart().equals("publisher")) {


                record.setPublisher(r.getElementText());
                depth--;

            }


            if (isEnd && event.asEndElement().getName().getLocalPart().equals("originInfo")) break;
        }


        record.setPublishedYear(year);

    }


    public static void setRelatedItemInfo(XMLEventReader r, Record record) throws XMLStreamException {


        while (true) {

            XMLEvent event = r.nextEvent();

            boolean isStart = event.isStartElement();
            if (isStart) depth++;

            boolean isEnd = event.isEndElement();
            if (isEnd) depth--;


            if (isStart && event.asStartElement().getName().getLocalPart().equals("title")) {


                record.setHostName(r.getElementText());
                depth--;
                continue;
            }


            if (isStart && event.asStartElement().getName().getLocalPart().equals("identifier")) {

                Attribute attribute = event.asStartElement().getAttributeByName(new QName("type"));

                if (attribute != null) {

                    if (attribute.getValue().equals("issn")  || attribute.getValue().equals("eissn")   ) {

                        String issnData = r.getElementText();
                        depth--;

                        record.addIssn(HelperFunctions.extractISSN(issnData));
                        continue;


                    }

                    if (attribute.getValue().equals("isbn")) {

                        String isbnData = r.getElementText();
                        depth--;

                        String extractedISBN = HelperFunctions.extractAndHyphenateISBN(isbnData, true, record);
                        if (extractedISBN != null) record.addISBN(extractedISBN);
                        continue;
                    }


                }

            }


            if (isEnd && event.asEndElement().getName().getLocalPart().equals("relatedItem")) break;

        }

    }

    public static String getURI(XMLEventReader r) throws XMLStreamException {


        String identifier = r.getElementText();
        //now current element is a END_ELEMENT
        depth--;
        return identifier;
    }

    public static String getAbstract(XMLEventReader r) throws XMLStreamException {


        String abstr = r.getElementText();
        //now current element is a END_ELEMENT
        depth--;

        return abstr;

    }

    public static String getDiva2(XMLEventReader r) throws XMLStreamException {

        String diva2Id = "-";

        while(true) {

            XMLEvent event = r.nextEvent();

            boolean isStart = event.isStartElement();
            if (isStart) depth++;

            boolean isEnd = event.isEndElement();
            if (isEnd) depth--;

            if (isStart && event.asStartElement().getName().getLocalPart().equals("recordIdentifier")) {
                //TODO can there be multiple topics within a <subject></subject> ?
                diva2Id = r.getElementText().toLowerCase();
                //now at END ELEMENT
                depth--;
                return diva2Id;
            }


            if (isEnd && event.asEndElement().getName().getLocalPart().equals("recordInfo")) {

                break;
            }


        }

        return diva2Id;


        }


    public static String getKeyword(XMLEventReader r) throws XMLStreamException {

        String keyword = "-99";
        while (true) {


            XMLEvent event = r.nextEvent();

            boolean isStart = event.isStartElement();
            if (isStart) depth++;

            boolean isEnd = event.isEndElement();
            if (isEnd) depth--;


            if (isStart && event.asStartElement().getName().getLocalPart().equals("topic")) {
                //TODO can there be multiple topics within a <subject></subject> ?
                keyword = r.getElementText().toLowerCase();
                //now at END ELEMENT
                depth--;
                return keyword;
            }


            if (isEnd && event.asEndElement().getName().getLocalPart().equals("subject")) {

                break;
            }


        }

        return keyword;

    }

    public static ArrayList<String> getAffiliations(XMLEventReader r, boolean isPersonal) throws XMLStreamException {

        ArrayList<String> affils = new ArrayList<>();
        while (true) {


            XMLEvent event = r.nextEvent();

            boolean isStart = event.isStartElement();
            if (isStart) depth++;

            boolean isEnd = event.isEndElement();
            if (isEnd) depth--;


            if (isStart && event.asStartElement().getName().getLocalPart().equals("affiliation")) {

                Attribute attribute = event.asStartElement().getAttributeByName(new QName("authority"));

                if (attribute == null) {

                    affils.add(r.getElementText().toLowerCase());
                    //now at END ELEMENT
                    depth--;
                } else {

                    if (!"kb.se".equals(attribute.getValue())) {
                        affils.add(r.getElementText().toLowerCase());
                        depth--;
                    }

                }

            }


            if (!isPersonal && isStart && event.asStartElement().getName().getLocalPart().equals("namePart")) {

                affils.add(r.getElementText().toLowerCase());
                //now at END ELEMENT
                depth--;

            }


            if (isEnd && event.asEndElement().getName().getLocalPart().equals("name")) break;


        }

        return affils; // could be of length zero
    }

    public static String getTitle(XMLEventReader r) throws XMLStreamException {

        //TODO alternative titles, titles repeated in different languages

        StringBuilder title = new StringBuilder(20);

        while (true) {

            XMLEvent event = r.nextEvent();

            boolean isStart = event.isStartElement();
            if (isStart) depth++;

            boolean isEnd = event.isEndElement();
            if (isEnd) depth--;


            if (isStart && event.asStartElement().getName().getLocalPart().equals("title")) {

                title.append(r.getElementText());
                //now at END ELEMENT
                depth--;

            }

            if (isStart && event.asStartElement().getName().getLocalPart().equals("subTitle")) {

                title.append(" ").append(r.getElementText());
                //now at END ELEMENT
                depth--;

            }


            if (isEnd && event.asEndElement().getName().getLocalPart().equals("titleInfo")) break;
        }


        return title.toString();

    }

    public static ArrayList<String> getLanguage(XMLEventReader r) throws XMLStreamException {

        ArrayList<String> languages = new ArrayList<>(1);
        while (true) {


            XMLEvent event = r.nextEvent();

            boolean isStart = event.isStartElement();
            if (isStart) depth++;

            boolean isEnd = event.isEndElement();
            if (isEnd) depth--;


            if (isStart && event.asStartElement().getName().getLocalPart().equals("languageTerm")) {

                languages.add(r.getElementText());
                //Now at END ELEMENT
                depth--;
            }


            if (isEnd && event.asEndElement().getName().getLocalPart().equals("language")) break;


        }

        return languages;
    }

    public static List<Record> parse(Object xmlFile) throws IOException, XMLStreamException {
        Reader reader = null;

        //FileOutputStream f = new FileOutputStream("ErrorLog.txt");
        //System.setErr(new PrintStream(f));


        SwedishStopWords60 swedishStopWords60 = new SwedishStopWords60();
        EnglishStopWords60 englishStopWords60 = new EnglishStopWords60();

        SwedishStemmer swedishStemmer = new SwedishStemmer();
        EnglishStemmer englishStemmer = new EnglishStemmer();

        if(xmlFile instanceof String) {

            reader = new BufferedReader( new InputStreamReader(new FileInputStream((String)xmlFile),"UTF-8") );
        }

        if(xmlFile instanceof BufferedReader) {

            reader = (BufferedReader)xmlFile;
        }


        XMLInputFactory xmlif = XMLInputFactory.newFactory();
        XMLEventReader xmler = xmlif.createXMLEventReader(reader);


        List<Record> recordsList = new ArrayList<>();

        int docs = 0;

        newRecord:
        while (xmler.hasNext()) {
            XMLEvent xmle = xmler.nextEvent();


            if (xmle.getEventType() == XMLEvent.START_ELEMENT && xmle.asStartElement().getName().getLocalPart().equals("mods")) {
                depth = 0;
                docs++;
                Record record = new Record();

                //create a new empty Record object

                while (true) { //loop until end of record
                    xmle = xmler.nextEvent();

                    boolean isStart = xmle.isStartElement();
                    if (isStart) {
                        depth++;
                    }

                    boolean isEnd = xmle.isEndElement();
                    if (isEnd) {
                        depth--;
                    }

                    //local repository source: recordInfo
                    if (isStart && xmle.asStartElement().getName().getLocalPart().equals("recordInfo")) { // recordInfo is a container



                        ArrayList<String> supplier = getRecordContentSourceAndDiva2ID(xmler, record);

                        record.setSupplier(supplier);
                        //System.out.println("Depth: " + depth + " " + supplier);
                        continue;

                    }
                    //publication year and publisher: originInfo
                    if (isStart && xmle.asStartElement().getName().getLocalPart().equals("originInfo")) { //origin info is a container

                        setYearAndPublisher(xmler, record);
                        continue;

                    }
                    //URI, ISSN, ISBN: identifier
                    if (isStart && xmle.asStartElement().getName().getLocalPart().equals("identifier")) {

                        Attribute type = xmle.asStartElement().getAttributeByName(new QName("type"));
                        //TODO can be repeated many times
                        if ("uri".equals(type.getValue())) {


                            String identifier = getURI(xmler);
                            record.setURI(identifier);
                            //TODO remove this debugging
                            //System.out.println("now processing: " + record.getURI());

                            //System.out.println("depth: " + depth + " " + identifier );
                            continue;
                        }


                        if ("issn".equals(type.getValue())) {

                            String issnData = xmler.getElementText();
                            depth--;

                            record.addIssn(HelperFunctions.extractISSN(issnData));
                            continue;
                        }

                        if ("isbn".equals(type.getValue())) {
                            String isbnData = xmler.getElementText();
                            depth--;

                            String extractedISBN = HelperFunctions.extractAndHyphenateISBN(isbnData, true, record);
                            if (extractedISBN != null) record.addISBN(extractedISBN);

                            continue;
                        }


                    }
                    //TITLE: titleInfo, level 1

                    if (isStart && xmle.asStartElement().getName().getLocalPart().equals("titleInfo") && depth == 1) { //depth is two (2) in swepub parser!!

                        Attribute type = xmle.asStartElement().getAttributeByName(new QName("type"));

                        //if(type != null) continue; // assume type is null or "alternative"

                        String title = getTitle(xmler);


                        record.setTitle(HtmlUnescaper.unescapeHTML1(title, 0)); // fix for (incorrectly) escaped å ä ö
                        //System.out.println("depth: " + depth + " " + title );
                        continue;
                    }
                    //LANGUAGE(S): language
                    if (isStart && xmle.asStartElement().getName().getLocalPart().equals("language")) {

                        ArrayList<String> languages = getLanguage(xmler);

                        //System.out.println("depth: " + depth + " " + languages );
                        record.setLanguage(languages);
                        continue;


                    }
                    //HSV + KEYWORDS: subject
                    if (isStart && xmle.asStartElement().getName().getLocalPart().equals("subject")) {


                        Attribute attribute = xmle.asStartElement().getAttributeByName(new QName("authority"));

                        if ((attribute != null) && ("uka.se".equals(attribute.getValue()) || "hsv".equals(attribute.getValue()))) {
                            QName HREF = new QName("http://www.w3.org/1999/xlink", "href");
                            attribute = xmle.asStartElement().getAttributeByName(HREF);

                            String classsificationCode = attribute.getValue();
                            //temp fix for erroneous data in SwePub TODO remove this later
                            if (classsificationCode.equals("20309")) classsificationCode = "20399";


                            //210 and 21001 Nanoteknik is the same.. remapp so we can use level 3 nano tech in level 5 classifyers

                            if(classsificationCode.equals("210")) classsificationCode = "21001";



                            record.addClassificationCodes(Integer.valueOf(classsificationCode));
                            //System.out.println( attribute.getValue() );
                            continue;
                        } else {


                            String keyword = getKeyword(xmler);

                            //split on ; (should not be the case but errors in registration exists)
                            if (keyword.contains(";")) {

                                String[] splitted = keyword.split(";");

                                for (String s : splitted)
                                    record.addUnkontrolledKkeywords(prefixFromKeyWords.concat(s.trim()));


                            } else {

                                record.addUnkontrolledKkeywords(prefixFromKeyWords.concat(keyword));
                            }

                            continue;

                        }


                    }
                    //ABSTRACT: abstract
                    if (isStart && xmle.asStartElement().getName().getLocalPart().equals("abstract")) {
                        //TODO handle multiple languages

                        String summary = getAbstract(xmler);

                        record.setSummary(HtmlUnescaper.unescapeHTML1(summary, 0)); //fix for (incorrectly) escaped å ä ö
                        continue;
                    }

                    //AFFILIATIONS BOTH PERSONAL AND CORPORATE (IGNORING KB.SE AUTHORITY): name
                    if (isStart && xmle.asStartElement().getName().getLocalPart().equals("name")) {

                        boolean isPersonal = false;
                        Attribute attribute = xmle.asStartElement().getAttributeByName(new QName("type"));
                        //ignore conferences
                        if(attribute != null && attribute.getValue().equals("conference")) continue;

                        if (attribute != null && attribute.getValue().equals("personal")) isPersonal = true;
                        ArrayList<String> affils = getAffiliations(xmler, isPersonal);
                        record.addAffiliations(affils);
                        continue;

                    }

                    //SVEP type: genere
                    if (isStart && xmle.asStartElement().getName().getLocalPart().equals("genre")) {


                        Attribute attribute1 = xmle.asStartElement().getAttributeByName(new QName("type"));
                        Attribute attribute2 = xmle.asStartElement().getAttributeByName(new QName("authority"));

                        if (attribute1 != null && attribute2 != null) {

                            String type1 = attribute1.getValue();
                            String type2 = attribute2.getValue();
                            if (type1.equals("publicationType") && type2.equals("svep")) {
                                record.addPublicationType(xmler.getElementText());
                                depth--;
                                continue;
                            }
                        }

                    }


                    //ISBN & ISSN & HOST NAME: relatedItem

                    if (isStart && xmle.asStartElement().getName().getLocalPart().equals("relatedItem")) {


                        setRelatedItemInfo(xmler, record);

                    }



                    if (isEnd && xmle.asEndElement().getName().getLocalPart().equals("mods")) {


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



                        if (record.containsSupportedLanguageText()) {

                            //PROSES TITLE(S)

                            List<TextAndLang> titles = record.getTitle();

                            for (TextAndLang tl : titles) {

                                if (tl.getLang().equals("swe")) {

                                    swe_title = true;

                                    String temp = tl.getText();
                                    List<String> list = SimpleParser.parse(temp, true, swedishStopWords60, swedishStemmer);

                                    for (String t : list) record.addTermsFromTitleAbstract(prefixFromTIABSwe.concat(t));


                                }

                                if (tl.getLang().equals("eng")) {

                                    eng_title = true;
                                    String temp = tl.getText();
                                    List<String> list = SimpleParser.parse(temp, true, englishStopWords60, englishStemmer);


                                    for (String t : list) record.addTermsFromTitleAbstract(prefixFromTIABEng.concat(t));

                                }


                            } //loop over titles ends


                            //PROCESS ABSTRACTS(S)

                            List<TextAndLang> summaries = record.getSummary();

                            for (TextAndLang tl : summaries) {

                                if (tl.getLang().equals("swe")) {

                                    swe_abstract = true;

                                    String temp = tl.getText();
                                    List<String> list = SimpleParser.parse(temp, true, swedishStopWords60, swedishStemmer);

                                    for (String t : list) {
                                        record.addTermsFromTitleAbstract(prefixFromTIABSwe.concat(t));
                                    }


                                }


                                if (tl.getLang().equals("eng")) {

                                    eng_abstract = true;

                                    String temp = tl.getText();
                                    List<String> list = SimpleParser.parse(temp, true, englishStopWords60, englishStemmer);


                                    for (String t : list) {
                                        record.addTermsFromTitleAbstract(prefixFromTIABEng.concat(t));
                                    }


                                }

                            } //loop over abstracts ends


                        } //contain supported language if-construction ends otherwise we don't bother to parse ti/ab

                        record.setFullEnglishText( eng_abstract & eng_title  );
                        record.setFullSwedishText( swe_abstract & swe_title   );
                        record.setContainsEnglish( eng_abstract || eng_title );
                        record.setContainsSwedish( swe_abstract || swe_title );

                        record.setMapDBKey(docs);
                        recordsList.add(record);
                        break;
                    }


                }
            }

        } //loop until end of XML

        xmler.close();
        reader.close();


        return recordsList;
    }
}
