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
import java.io.IOException;
import java.nio.file.Paths;
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
            doc.add(new TextField("keyword", key, Field.Store.YES));

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

        /*
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

*/




        //SEARCH diva mods

        luceneIndex = FSDirectory.open(Paths.get("E:\\2022\\SWEPUB_JSON_20210805_UTBVET\\TAKETWO\\lucene"));
        IndexReader reader = DirectoryReader.open(luceneIndex);
        IndexSearcher searcher = new IndexSearcher(reader);

        System.out.println("Docs in index: " + reader.numDocs());
        String query0 = "(2010 OR 2011 OR 2012 OR 2013 OR 2014 OR 2015 OR 2016 OR 2017 OR 2018 OR 2019 OR 2020)";
        Query years = new ComplexPhraseQueryParser("year",analyzer).parse(query0);

        String query1 = " bibliometric* scientometric* informetric* infometric*";
        Query q1 = new ComplexPhraseQueryParser("text",analyzer).parse(query1);

        Query master = new BooleanQuery.Builder().add(years, BooleanClause.Occur.MUST).add(q1, BooleanClause.Occur.MUST).build();


        TopDocs returnedDocs = searcher.search(master, 10000); // max 10000 hits
        ScoreDoc[] hits = returnedDocs.scoreDocs;

        for (int i = 0; i < hits.length; i++) {

            int docID = hits[i].doc;
            Document d = searcher.doc(docID);
            System.out.println(d.get("pid") + "\t" + d.get("title"));
        }

        System.out.println("Number of hits: " + master.toString() + " : " + hits.length);



        reader.close();
    }



}
