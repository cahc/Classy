package Database;

import SwePub.Record;
import SwePub.TextAndLang;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.complexPhrase.ComplexPhraseQueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ModsIdentifyPedgogyTerms {

    public static void addTextFromMods(IndexWriter w, Record record) throws IOException {

        Document doc = new Document();

        doc.add( new StringField("year",  String.valueOf( record.getPublishedYear() ) , Field.Store.YES));

        for(String issn : record.getIssn()) {

            doc.add( new StringField("issn",  issn , Field.Store.YES));

        }


        String host = record.getHostName();


        if(host != null) doc.add( new TextField("host",  record.getHostName() , Field.Store.YES));


        doc.add(new StringField("pid", record.getDiva2Id(), Field.Store.YES ));

        for(String key : record.getUnkontrolledKkeywords()) {
            doc.add(new TextField("text", key.substring(2), Field.Store.YES)); //remove the K@-prefix add to title and abstract..

        }

        doc.add(new TextField("title", record.getTitle().get(0).getText(), Field.Store.YES ));

        //combine title and abstract

        List<TextAndLang> abstracts = record.getSummary();

        String text = "";
        if(abstracts.size() != 0) { text = record.getTitle().get(0).getText().concat( " " ).concat( record.getSummary().get(0).getText() ); } else { text = record.getTitle().get(0).getText();    }


        doc.add(new TextField("text", text, Field.Store.YES));

        w.addDocument(doc);





    }

    public static void main(String[] arg) throws IOException, XMLStreamException, ParseException {

        StandardAnalyzer analyzer = new StandardAnalyzer(); //TODO customize
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        Directory luceneIndex = FSDirectory.open(Paths.get("E:\\2022\\SWEPUB_JSON_20210805_UTBVET\\TAKETWO\\lucene"));


        ModsDivaFileParser modsDivaFileParser = new ModsDivaFileParser();
        List<Record> recordList = modsDivaFileParser.parse( "E:\\2022\\SWEPUB_JSON_20210805_UTBVET\\TAKETWO\\diva_2010-2020.xml" );
        System.out.println("size: " + recordList.size());

        IndexWriter writer = new IndexWriter(luceneIndex,config);

        for (Record r : recordList) {

            addTextFromMods(writer, r );

        }

        writer.flush();
        writer.commit();
        writer.close();






        //SEARCH diva mods

        luceneIndex = FSDirectory.open(Paths.get("E:\\2022\\SWEPUB_JSON_20210805_UTBVET\\TAKETWO\\lucene"));
        IndexReader reader = DirectoryReader.open(luceneIndex);
        IndexSearcher searcher = new IndexSearcher(reader);

        System.out.println("Docs in index: " + reader.numDocs());
        String query0 = "(2010 2011 2012 2013 2014 2015 2016 2017 2018 2019 2020)"; //implicit OR
        Query years = new ComplexPhraseQueryParser("year",analyzer).parse(query0);


        //not

        String queryExlude = "text:(clinic* OR prevalence OR cancer OR \\\"weight gain\\\" OR \\\"marital sat*\\\" OR (care* AND (nursing OR nurse)) OR overweight OR genetic* OR epidem* OR diabet* OR asthma* OR obstetric* drug* OR disease* OR metabolic* or \"parental leave\" OR refugee* OR patient* or midwife* OR disorder* OR diagnosis OR injection* OR intramuscular OR agricult* OR oncolog* OR \"public health problem*\" OR tobacco OR \"physical labour\" OR \"rural labor\" OR \"sleeping difficulties\")";
        Query qrExlude = new ComplexPhraseQueryParser("text", analyzer).parse(queryExlude);



        //utbildning allmänt
        String query1 = "(text:(educat*) OR text:(utbild* OR lärarutbild* OR vuxenutbild* OR högskoleutbild* OR universitetsutbild* OR yrkesutbild* OR distansutbild* OR komvuxutbild* OR grundskoleutbild* OR gymnasieutbild* OR spetsubild* OR lärlingsutbild* OR YH-utbild*))";
        Query qr1 = new ComplexPhraseQueryParser("text", analyzer).parse(query1);

        //lärarroll
        String query2 = "text:(teacher* OR lärare* OR matematiklär* OR historielär* OR ämneslärar* OR svensklär* OR mattelär* OR naturlärar* OR geografilärar* OR samhällslärar* OR bildlärar* OR idrottslärar* OR komvuxlära* OR yrkeslär* OR grundskolelär* OR gymnasielär* OR universitetslär* OR filosofilär* OR religionslärar* OR tekniklärar* OR engelsklär* OR spansklär* OR tysklär* OR språklärar* OR förskollär* OR fransklär* OR slöjdlär* OR hemkunskapslär* OR musiklär* OR biologilär* OR fysiklär* OR kemilär* OR speciallärar* OR särskolelär*)";
        Query qr2 = new ComplexPhraseQueryParser("text", analyzer).parse(query2);
        //undervisning
        String query3 = "text:(teach* OR undervis* OR matematikundervis* OR historieundervis* OR svenskundervis* OR matteundervis* OR naturundervis* OR geografiundervis* OR samhällsundervis* OR bildundervis* OR idrottsundervis* OR komvuxundervis* OR yrkesundervis* OR grundskoleundervis* OR gymnasieundervis* OR universitetsundervis* OR filosofiundervis* OR religionsundervis* OR teknikundervis* OR engelskundervis* OR spanskundervis* OR tyskundervis* OR språkundervis* OR förskoleundervis* OR franskundervis* OR slöjdundervis* OR hemkunskapsundervis* OR musikundervis* OR biologiundervis* OR fysikundervis* OR kemiundervis*)";
        Query qr3 = new ComplexPhraseQueryParser("text", analyzer).parse(query3);
        //skola
        String query4 = "text:(school* OR university OR preschool OR  skol* OR förskol* OR grundskol* OR särskol* OR sameskol* OR högskol* OR specialskol* OR gymnasiesärskol* OR grundsärskol* OR musikskol* OR friskol* OR folkhögskol*)";
        Query qr4 = new ComplexPhraseQueryParser("text", analyzer).parse(query4);
        //elev
        String query5 = "(text:(student* OR elev* OR gymnasi*) AND text:(undervis* OR skol* OR betyg* OR utbild* OR pedago* OR didak*)) OR (text:(student* OR pupil*) AND text:(teach* OR school* OR educ* OR pedago* OR didac*))";
        Query qr5 = new ComplexPhraseQueryParser("text", analyzer).parse(query5);

        //klassrum vägledning
        String query6 = "text:(klassrum* OR classroom* OR förskol* OR fritidshem* OR studievägled* OR \"student counsel*\" OR \"student advis*\" )";
        Query qr6 = new ComplexPhraseQueryParser("text", analyzer).parse(query6);

        //pedagogik/didaktik
        String query7 = "text:(pedago* OR didac* OR didakti* OR högskolepedag* OR förskolepedag* OR montessoripedag* OR idrottspedag* OR bildpedag* OR slöjdpedag* OR waldorfpedag* OR fritidspedag* OR vårdpedag* OR arbetslivspedag* OR matematikdidak* OR historiedidak* OR svenskdidak* OR mattedidak* OR naturdidak* OR geografididak* OR samhällsdidak* OR bilddidak* OR idrottsdidak* OR komvuxdidak* OR yrkesdidak* OR grundskoledidak* OR gymnasiedidak* OR universitetdidak* OR filosofiididak* OR religionsdidak* OR teknikdidak* OR engelsdidak* OR spanskdidak* OR tyskdidak* OR språkdidak* OR förskoledidak* OR franskdidak* OR slöjddidak* OR hemkunskapsdidak* OR musikdidak* OR biologididak* OR fysikdidak* OR kemididak* OR specialdidak* OR särskoledidak* OR allmändidak* OR ämnesdidak*)";
        Query qr7 = new ComplexPhraseQueryParser("text", analyzer).parse(query7);


        //alternative structure on query: Q1 AND (Q2 OR Q3 OR Q4 OR Q5 OR Q6 OR Q7) NOT (Q8)

        Query init = new BooleanQuery.Builder().add(years, BooleanClause.Occur.MUST).add(qr1, BooleanClause.Occur.MUST).build();
        Query predicate = new BooleanQuery.Builder().add(qr2,BooleanClause.Occur.SHOULD).add(qr3,BooleanClause.Occur.SHOULD).add(qr4,BooleanClause.Occur.SHOULD).add(qr5,BooleanClause.Occur.SHOULD).add(qr6,BooleanClause.Occur.SHOULD).add(qr7,BooleanClause.Occur.SHOULD).build();
        Query combined = new BooleanQuery.Builder().add(init, BooleanClause.Occur.MUST).add(predicate,BooleanClause.Occur.MUST).add(qrExlude,BooleanClause.Occur.MUST_NOT).build();



        List<String> queries = new ArrayList<>();
        queries.add(query1); queries.add(query2); queries.add(query3); queries.add(query4); queries.add(query5); queries.add(query6); queries.add(query7);


        BufferedWriter writerResult = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( new File("E:\\2022\\SWEPUB_JSON_20210805_UTBVET\\TAKETWO\\TERM_SEARCH.txt") ), StandardCharsets.UTF_8) );

        int count =1;
        HashSet<String> total = new HashSet<>();

        for(String q : queries) {

            Query q1 = new ComplexPhraseQueryParser("text", analyzer).parse(q);
            Query master = new BooleanQuery.Builder().add(years, BooleanClause.Occur.MUST).add(q1, BooleanClause.Occur.MUST).add(qrExlude,BooleanClause.Occur.MUST_NOT).build();
            TopDocs returnedDocs = searcher.search(master, 10000); // max 10000 hits
            ScoreDoc[] hits = returnedDocs.scoreDocs;

            for (int i = 0; i < hits.length; i++) {

                int docID = hits[i].doc;
                Document d = searcher.doc(docID);
                total.add(d.get("pid"));
                writerResult.write(d.get("pid") + "\t" + d.get("title") +"\t" + "q"+count);
                writerResult.newLine();
            }


            System.out.println("Number of hits: " + "q"+count + " : " + hits.length);
            count++;
        }
        System.out.println("union of results: " +  total.size());
        writerResult.flush();
        writerResult.close();



        writerResult = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( new File("E:\\2022\\SWEPUB_JSON_20210805_UTBVET\\TAKETWO\\TermSearchRestricted.txt") ), StandardCharsets.UTF_8) );

        TopDocs returnedDocs = searcher.search(combined, 10000); // max 10000 hits
        ScoreDoc[] hits = returnedDocs.scoreDocs;

        for (int i = 0; i < hits.length; i++) {

            int docID = hits[i].doc;
            Document d = searcher.doc(docID);
            writerResult.write(d.get("pid") + "\t" + d.get("title") +"\t" + "combinedQuery");
           writerResult.newLine();
        }

        writerResult.flush();
        writerResult.close();

        System.out.println("Number of hits on combined: " + hits.length);






        reader.close();
    }



}
