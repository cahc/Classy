package LuceneSearch;

import Diva.CreateDivaTable;
import Diva.ReducedDiVAColumnIndices;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by crco0001 on 1/3/2018.
 */
public class Indexer {


    public static void addTextFromDiVA(IndexWriter w, String[] docRow) throws IOException {

        Document doc = new Document();

         doc.add(new StringField("pid", docRow[ReducedDiVAColumnIndices.PID.getValue()], Field.Store.YES ));
         doc.add(new TextField("keyword", docRow[ReducedDiVAColumnIndices.Keywords.getValue()], Field.Store.YES ));
         //combine title and abstract
         doc.add(new TextField("text", docRow[ReducedDiVAColumnIndices.Title.getValue()].concat(" ").concat(docRow[ReducedDiVAColumnIndices.Abstract.getValue()]), Field.Store.YES ));

        w.addDocument(doc);





    }


    public static void main(String[] args) throws IOException {


        if(args.length != 2) {System.out.println("Supply csv2 all file & a name for lucene index dir."); System.exit(0);}

        File file = new File(  args[0] );

        if(!file.exists()) {System.out.println("Supply csv2 all file.."); System.exit(0);}

        //setup lucene index
        StandardAnalyzer analyzer = new StandardAnalyzer(); //TODO customize
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        Directory luceneIndex = FSDirectory.open(Paths.get(args[1]) );

        IndexWriter writer = new IndexWriter(luceneIndex,config);
        //parse csv2 all from DiVA
        CreateDivaTable createDivaTable = new CreateDivaTable(file);

        createDivaTable.parse();

        int N = createDivaTable.nrRows();

        System.out.println(N + " document parsed from CSV");

        for(int i=0; i< N; i++) {

            addTextFromDiVA(writer, createDivaTable.getRowInTable(i));

        //For searching by keywords..
         //   System.out.println(  createDivaTable.getRowInTable(i)[ReducedDiVAColumnIndices.PID.getValue()]  );
         //   System.out.println(  createDivaTable.getRowInTable(i)[ReducedDiVAColumnIndices.Title.getValue()]  );
         //   System.out.println(  createDivaTable.getRowInTable(i)[ReducedDiVAColumnIndices.Abstract.getValue()]  );
         //   System.out.println(  createDivaTable.getRowInTable(i)[ReducedDiVAColumnIndices.Keywords.getValue()]  );

        }

        writer.flush();
        writer.commit();
        writer.close();


    }


}
