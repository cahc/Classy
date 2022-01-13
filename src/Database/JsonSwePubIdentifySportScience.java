package Database;

import SwePub.ClassificationCategory;
import SwePub.HsvCodeToName;
import SwePub.Record;
import SwePub.TextAndLang;
import jsat.classifiers.CategoricalResults;
import jsat.classifiers.Classifier;
import jsat.classifiers.DataPoint;
import jsat.linear.Vec;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.complexPhrase.ComplexPhraseQueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JsonSwePubIdentifySportScience {


    /*

    Use three methods for identify idrottsvetenskap, "sport science"

    1) HSV classifications (automatic and manual)

    2) lucene term queries

    4) journals internationella samt nationella (name and ISSN)


     */


    public static void addTextFromSwePub(IndexWriter w, Record record) throws IOException {

        Document doc = new Document();

        doc.add( new StringField("year",  String.valueOf( record.getPublishedYear() ) , Field.Store.YES));

        for(String issn : record.getIssn()) {

            doc.add( new StringField("issn",  issn , Field.Store.YES));

        }


        String host = record.getHostName();


       if(host != null) doc.add( new TextField("host",  record.getHostName() , Field.Store.YES));


        doc.add(new StringField("pid", record.getMasterURI(), Field.Store.YES ));

        for(String key : record.getUnkontrolledKkeywords()) {
            doc.add(new TextField("keyword", key, Field.Store.YES));

        }

        doc.add(new TextField("title", record.getTitle().get(0).getText(), Field.Store.YES ));

        //combine title and abstract

        List<TextAndLang> abstracts = record.getSummary();

        String text = "";
        if(abstracts.size() != 0) { text = record.getTitle().get(0).getText().concat( " " ).concat( record.getSummary().get(0).getText() ); } else { text = record.getTitle().get(0).getText();    }


        doc.add(new TextField("text", text, Field.Store.YES ));

        w.addDocument(doc);





    }

    public static void main(String[] arg) throws IOException, MyOwnException, ClassNotFoundException, ParseException {

        FileHashDB fileHashDB = new FileHashDB();
        fileHashDB.setPathToFile("E:\\Desktop\\JSON_SWEPUB\\SWEPUB20210421.db");
        fileHashDB.createOrOpenDatabase();
         Set<String> lackingYears = new HashSet<>();

        BufferedWriter uriWriter = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( new File("E:\\Desktop\\JSON_SWEPUB\\SportScienceUris_NEW.txt") ), StandardCharsets.UTF_8) );

        //manually classified
        Set<String> manual = new HashSet<>();


        System.out.println("Finding records pre-classified with 30308");
        int manuallyHsv = 0;
        for (Map.Entry<String, Record> entry : fileHashDB.database.entrySet()) {

            Record record = entry.getValue();

            if(record.getPublishedYear() == null) { lackingYears.add(record.getMasterURI()); continue;}

            if(record.getPublishedYear().compareTo(2015) < 0 || record.getPublishedYear().compareTo(2020) > 0 ) continue;

            if( record.containsLevel5Classification() ) {

                if( record.getClassificationCodes().contains(30308) )  {manuallyHsv++; manual.add(record.getMasterURI()); }

            }

        }



        //classify automatically

        int autoHsvSwe = 0;
        int autoHsvEng = 0;


        System.out.println("Reading index and term weights..");
        IndexAndGlobalTermWeights englishLevel5 = new IndexAndGlobalTermWeights("eng", 5);
        IndexAndGlobalTermWeights swedishLevel5 = new IndexAndGlobalTermWeights("swe", 5);
        englishLevel5.readFromMapDB("E:\\swepub20200701\\");
        swedishLevel5.readFromMapDB("E:\\swepub20200701\\");

        System.out.println("Loading classifiers..");
        Classifier classifierlevel5eng = TrainAndPredict.HelperFunctions.readSerializedClassifier("E:\\swepub20200701\\classifier.eng.5.ser");
        Classifier classifierLevel5swe = TrainAndPredict.HelperFunctions.readSerializedClassifier("E:\\swepub20200701\\classifier.swe.5.ser");


        double adHocProbThreshold = 0.3d;
        Set<String> auto = new HashSet<>();

        System.out.println("Classifying everything and looking for 30308 matches");
        for (Map.Entry<String, Record> entry : fileHashDB.database.entrySet()) {

            Record record = entry.getValue();
            if(record.getPublishedYear() == null) { lackingYears.add(record.getMasterURI()); continue;}

            if(record.getPublishedYear().compareTo(2015) < 0 || record.getPublishedYear().compareTo(2020) > 0 ) continue;

            if(record.isContainsEnglish()) {

                //use english level 5
                Vec vec = englishLevel5.getVecForUnSeenRecord(record);

                if(vec != null) {

                    vec.normalize();

                    CategoricalResults result = classifierlevel5eng.classify( new DataPoint(vec) );

                     int code = result.mostLikely();
                     double prob = result.getProb(code);

                    ClassificationCategory true_hsv =  HsvCodeToName.getCategoryInfo( IndexAndGlobalTermWeights.level5ToCategoryCodes.inverse().get(code)    );
                    if( prob > adHocProbThreshold && true_hsv.getCode() == 30308) { autoHsvEng++; auto.add(record.getMasterURI()); } //System.out.println("eng: " + prob + "\t" +true_hsv.getSwe_description() + "\t" + record.getURI());

                }




            } else if(record.isContainsSwedish()) {

                //use english level 5
                Vec vec = swedishLevel5.getVecForUnSeenRecord(record);

                if(vec != null) {

                    vec.normalize();

                    CategoricalResults result = classifierLevel5swe.classify( new DataPoint(vec) );

                    int code = result.mostLikely();
                    double prob = result.getProb(code);

                    ClassificationCategory true_hsv =  HsvCodeToName.getCategoryInfo( IndexAndGlobalTermWeights.level5ToCategoryCodes.inverse().get(code)    );

                    if( prob > adHocProbThreshold && true_hsv.getCode() == 30308) { autoHsvSwe++;  auto.add(record.getMasterURI()); } //System.out.println("swe: " + prob + "\t" +true_hsv.getSwe_description() + "\t" + record.getURI());


                }


            }


        } //for each record


        System.out.println("Auto-classified 30308 (eng): "+ autoHsvEng); //?
        System.out.println("Auto-classified 30308 (swe): "+ autoHsvSwe); //?

        System.out.println("Manually 30308 classification (swe/eng): " + manuallyHsv); //4592



        Set<String> ukaHits = Stream.concat(manual.stream(),auto.stream()).collect(Collectors.toSet());
        Set<String> intersect = manual.stream().filter(auto::contains).collect(Collectors.toSet());

        System.out.println("union: " + ukaHits.size());
        System.out.println("intersection: " + intersect.size());

        for(String uri : ukaHits) { uriWriter.write(uri + "\t" + "UKÄ"); uriWriter.newLine(); };




        //lucene search-based query



        //INDEX SWEPUB

        StandardAnalyzer analyzer = new StandardAnalyzer(); //TODO customize
          IndexWriterConfig config = new IndexWriterConfig(analyzer);
        Directory luceneIndex = FSDirectory.open(Paths.get("E:\\Desktop\\JSON_SWEPUB\\lucene"));
        IndexWriter writer = new IndexWriter(luceneIndex,config);

         for (Map.Entry<String, Record> entry : fileHashDB.database.entrySet()) {

            addTextFromSwePub(writer, entry.getValue() );

        }


         writer.flush();
         writer.commit();
         writer.close();



        //SEARCH SWEPUB

        Directory luceneIndex2 = FSDirectory.open(Paths.get("E:\\Desktop\\JSON_SWEPUB\\lucene"));
        IndexReader reader = DirectoryReader.open(luceneIndex2);
        IndexSearcher searcher = new IndexSearcher(reader);

        System.out.println("Docs in index: " + reader.numDocs());

        //String query0 = "2008 OR 2009 OR 2010 OR 2011 OR 2012 OR 2013 OR 2014 OR 2015 OR 2016"; //this was in the old report
        String query0 = "year:(2015 OR 2016 OR 2017 OR 2018 OR 2019 OR 2020)";


        String query1 = "text:(sport* OR idrott*)"; //title
        String query2 = "text:(athlete* OR fitness* OR kondition) AND text:(workout OR trän* OR exercise* OR coach* OR motion* OR gymnas*)"; //title or abstract
        String query3 = "text:\"physical activity*\"~3 AND text:(train* OR exercise*)"; //title or abstract
        String query4 = "text:\"fysisk* aktivitet*\"~3 AND text:(trän* OR motion* OR gymnas*)"; //title or abstract
        String query5 = "keyword:(sport* OR idrott*)"; //keyword



        Query years = new ComplexPhraseQueryParser("year",analyzer).parse(query0);

        Query q1 = new ComplexPhraseQueryParser("text",analyzer).parse(query1);
        Query q2 = new ComplexPhraseQueryParser("text",analyzer).parse(query2);
        Query q3 = new ComplexPhraseQueryParser("text",analyzer).parse(query3);
        Query q4 = new ComplexPhraseQueryParser("text",analyzer).parse(query4);
        Query q5 = new ComplexPhraseQueryParser("keyword",analyzer).parse(query5);


        List<Query> queryList = new ArrayList<>( ); queryList.add(q1); queryList.add(q2); queryList.add(q3); queryList.add(q4); queryList.add(q5);
        Set<String> textSearchHits = new HashSet<>();

        for(Query query : queryList) {

            Query master = new BooleanQuery.Builder().add(years, BooleanClause.Occur.MUST).add(query, BooleanClause.Occur.MUST).build();


            TopDocs returnedDocs = searcher.search(master, 20000); // max 20000 hits
            ScoreDoc[] hits = returnedDocs.scoreDocs;


            for (int i = 0; i < hits.length; i++) {

                int docID = hits[i].doc;
                Document d = searcher.doc(docID);

                textSearchHits.add(d.get("pid"));

                //System.out.println(d.get("pid") + "\t" + d.get("title"));
            }

            System.out.println("Number of hits: " + master.toString() + " : " + hits.length);


        }

        System.out.println("Total records text search: " + textSearchHits.size());

        for(String uri : textSearchHits) { uriWriter.write(uri + "\t" + "TERM_SEARCH"); uriWriter.newLine(); }


        //search based on ISSN

        Set<String> issn = new HashSet<>();

        issn.add("2213-2961");
        issn.add("2157-3913");
        issn.add("2157-3905");
        issn.add("2095-2546");
        issn.add("1930-076X");
        issn.add("1750-9858");
        issn.add("1750-984X");
        issn.add("1747-9541");
        issn.add("1746-1391");
        issn.add("1746-031X");
        issn.add("1728-869X");
        issn.add("1715-5312");
        issn.add("1618-4742");
        issn.add("1600-0838");
        issn.add("1558-6235");
        issn.add("1555-0273");
        issn.add("1555-0265");
        issn.add("1550-2783");
        issn.add("1543-8635");
        issn.add("1543-8627");
        issn.add("1543-3072");
        issn.add("1543-2920");
        issn.add("1543-2904");
        issn.add("1543-2793");
        issn.add("1543-2785");
        issn.add("1543-2777");
        issn.add("1543-2769");
        issn.add("1543-2742");
        issn.add("1543-270X");
        issn.add("1543-267X");
        issn.add("1538-3008");
        issn.add("1538-3008");
        issn.add("1537-8918");
        issn.add("1537-890X");
        issn.add("1536-3724");
        issn.add("1533-4295");
        issn.add("1533-4287");
        issn.add("1532-9321");
        issn.add("1527-0025");
        issn.add("1526-484X");
        issn.add("1524-1602");
        issn.add("1476-3141");
        issn.add("1474-8185");
        issn.add("1473-0480");
        issn.add("1469-0292");
        issn.add("1466-853X");
        issn.add("1466-447X");
        issn.add("1441-3523");
        issn.add("1440-2440");
        issn.add("1439-3964");
        issn.add("1357-3322");
        issn.add("1351-0029");
        issn.add("1303-2968");
        issn.add("1120-3137");
        issn.add("1091-5397");
        issn.add("1084-1288");
        issn.add("1077-5552");
        issn.add("1073-6840");
        issn.add("1064-8011");
        issn.add("1063-8652");
        issn.add("1062-8592");
        issn.add("1062-6050");
        issn.add("1060-1872");
        issn.add("1056-6716");
        issn.add("1050-642X");
        issn.add("1050-1606");
        issn.add("1041-696X");
        issn.add("1041-3200");
        issn.add("1012-6902");
        issn.add("0966-6362");
        issn.add("0959-3020");
        issn.add("0952-3367");
        issn.add("0905-7188");
        issn.add("0899-8493");
        issn.add("0895-2779");
        issn.add("0888-4781");
        issn.add("0888-4773");
        issn.add("0860-021X");
        issn.add("0765-1597");
        issn.add("0741-1235");
        issn.add("0736-5829");
        issn.add("0364-9857");
        issn.add("0363-5465");
        issn.add("0306-7297");
        issn.add("0306-3674");
        issn.add("0278-5919");
        issn.add("0273-5024");
        issn.add("0270-1367");
        issn.add("0268-0033");
        issn.add("0264-0414");
        issn.add("0195-9131");
        issn.add("0193-7235");
        issn.add("0190-6011");
        issn.add("0172-4622");
        issn.add("0167-9457");
        issn.add("0112-1642");
        issn.add("0094-8705");
        issn.add("0091-6331");
        issn.add("0091-6331");
        issn.add("0091-3847");
        issn.add("0047-0767");
        issn.add("0024-9890");
        issn.add("0022-4707");
        issn.add("0022-2895");

        //INTERNATIONAL JOURNAL OF SPORT AND EXERCISE PSYCHOLOGY
        //QUALITATIVE RESEARCH IN SPORT EXERCISE AND HEALTH
        //SPORT IN SOCIETY
        //SPORT MANAGEMENT REVIEW
        //SPORT PSYCHOLOGIST
        issn.add("1612-197X");
        issn.add("2159-676X");
        issn.add("1743-0437");
        issn.add("1441-3523");
        issn.add("0888-4781");
        issn.add("1557-251X");
        issn.add("2159-6778");
        issn.add("1743-0445");
        issn.add("1441-3523");
        issn.add("1543-2793");




        Set<String>  journalHits = new HashSet<>();

        BooleanQuery.Builder issnBuilder = new BooleanQuery.Builder();

        for(String nr : issn) {

            issnBuilder.add( new TermQuery(new Term("issn", nr)), BooleanClause.Occur.SHOULD  );

        }

        Query issnNrs =  issnBuilder.build();
        Query issnMaster = new BooleanQuery.Builder().add(years, BooleanClause.Occur.MUST).add(issnNrs, BooleanClause.Occur.MUST).build();

        TopDocs returnedDocs = searcher.search(issnMaster, 20000); // max 20000 hits
        ScoreDoc[] hits = returnedDocs.scoreDocs;


        for (int i = 0; i < hits.length; i++) {

            int docID = hits[i].doc;
            Document d = searcher.doc(docID);

            journalHits.add( d.get("pid") );
            //System.out.println(d.get("pid") + "\t" + d.get("title"));
        }

        System.out.println("Number of hits ISSN: " + issnMaster.toString() + " : " + hits.length);


        for(String uri : journalHits) { uriWriter.write(uri + "\t" + "ISSN"); uriWriter.newLine(); }


        System.out.println("##");
        System.out.println("Records without year: " + lackingYears.size());
        Set<String> uniqueUris = new HashSet<>();

        for(String s : ukaHits) uniqueUris.add(s);
        for(String s: journalHits) uniqueUris.add(s);
        for(String s: textSearchHits) uniqueUris.add(s);

        System.out.println("Total number of pubs: " + uniqueUris.size());
        uriWriter.flush();
        uriWriter.close();

        reader.close();
        luceneIndex2.close();
        fileHashDB.closeDatabase();



    }

}




