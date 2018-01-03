package LuceneSearch;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.complexPhrase.ComplexPhraseQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by crco0001 on 1/3/2018.
 */
public class Searcher {


    public static void main(String[] args) throws IOException {

        if(args.length != 1) {System.out.println("Supply c lucene index"); System.exit(0);}

        List<String> queries = new ArrayList<>();

        while (true) {

            System.out.print("Enter query (press n if done): ");

            queries.add( System.console().readLine() );

            if ("n".equals( queries.get( queries.size()-1  ) )) {
                System.out.println("OK..");
                break;
            }

            System.out.println("query : " + queries.get( queries.size()-1 ));
            System.out.println("-----------\n");
        }

        String query = null;
        if(queries.size() == 1) {query = queries.get(0); } else {

            query = queries.get( queries.size()-2);
        }



        //setup lucene index
        Directory luceneIndex = FSDirectory.open(Paths.get(args[0]) );



        //create query
        //fields pid, keyword, text
        //q = new QueryParser("title", analyzer).parse(" title:(idrott* OR sport*) OR summary:(idrott* OR sport*) OR title:(coach* AND fitness*) OR summary:(coach* AND fitness*) OR title:(coach* AND athlete*) OR summary:(coach* AND athlete*)"); //title default field

        StandardAnalyzer analyzer = new StandardAnalyzer();

        Query q = null;
        try {
            //query = QueryParser.escape(query);
            // q = new QueryParser("title",analyzer).parse(query);
            // q = new QueryParser("title",analyzer).parse("\"jakarta apache\"~10");
            //so we can do "physi* excer*" ?
            q = new ComplexPhraseQueryParser("title",analyzer).parse(query);

        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("INVALID QUERY, CHECK SYNTAX!");
            luceneIndex.close();
            System.exit(0);
        }



        IndexReader reader = DirectoryReader.open(luceneIndex);
        IndexSearcher searcher = new IndexSearcher(reader);

        System.out.println("Docs in index: " + reader.numDocs());


        TopDocs returnedDocs = searcher.search(q,20000); // max 20000 hits

        ScoreDoc[] hits = returnedDocs.scoreDocs;

     //   BufferedWriter writer = new BufferedWriter( new FileWriter( new File("recordsTextSearch.txt")));


        //display

        for(int i=0; i<hits.length; i++) {

            int docID = hits[i].doc;
            Document d = searcher.doc(docID);
            System.out.println( d.get("pid") + " " + d.get("text") );
       //     writer.write(d.get("pid") +"\t" + d.get("title") );
        //    writer.newLine();
        }


        System.out.println("Total number of returned docs: " + hits.length);
        System.out.println("Using " + q.toString());
        reader.close();
        luceneIndex.close();
     //   writer.flush();
     //   writer.close();







    }





}
