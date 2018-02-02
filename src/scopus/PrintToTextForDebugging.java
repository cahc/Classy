package scopus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by crco0001 on 1/26/2018.
 */
public class PrintToTextForDebugging {




    public static void main(String[] arg) throws IOException {



        System.out.println("Reading in serialized records..");
        List<ScopusRecord> records = SimplePersistor.deserializeList("records.ser");


        BufferedWriter writer = new BufferedWriter( new FileWriter( new File("output.txt")));




        for(ScopusRecord record : records) {
            StringBuilder stringBuilder = new StringBuilder(100);

            stringBuilder.append(record.getEid()).append("\t");
            stringBuilder.append(record.getCitedBy() ).append("\t");
            stringBuilder.append(record.getAggregationType() ).append("\t");
            stringBuilder.append(record.getSrctype() ).append("\t");
            stringBuilder.append(record.getDocumentType() ).append("\t");
            stringBuilder.append(record.getPublicationYear() ).append("\t");
            stringBuilder.append(record.getPublicationName() ).append("\t");
            stringBuilder.append(record.getTitle() ).append("\t");

            String abstractText = record.getSummaryText();
            if(abstractText != null) {

                String fixed = abstractText.replaceAll("[\\n\\r\\s]+", " ") ;

                stringBuilder.append( fixed ).append("\t");
            } else {

                stringBuilder.append( "" ).append("\t");
            }


            stringBuilder.append(record.getAuthorKeywords() ).append("\t");
            stringBuilder.append(record.getIndexTerms() );

            writer.write(stringBuilder.toString());
            writer.newLine();


            //System.out.println(record.getEid());

        }

        writer.flush();
        writer.close();
        System.out.println("total number of records: " + records.size());




        BufferedWriter writerCitedRefs = new BufferedWriter( new FileWriter( new File("citedRefs.txt")));



        System.out.println("writing cited references to file");
        for(ScopusRecord record : records) {

           List<CitedReference> list = record.getCitedReferences();

           for(CitedReference citedReference : list) {

               String fixed =  citedReference.getRefFulltext().replaceAll("[\\n\\r\\s]+", " ") ;

               writerCitedRefs.write(record.getEid() +"\t" + citedReference.getRefID() +"\t" + citedReference.getRefIdType() + "\t" + fixed );
               writerCitedRefs.newLine();

           }

        }


        writerCitedRefs.flush();
        writerCitedRefs.close();



        BufferedWriter writerAffils = new BufferedWriter( new FileWriter( new File("AffilLevel1.txt")));



        System.out.println("writing Affils references to file");
        for(ScopusRecord record : records) {

           List<AffiliationLevel1> affiliationLevel1List = record.getAffiliationLevel1List();

           for(AffiliationLevel1 affiliationLevel1 : affiliationLevel1List) {
               writerAffils.write(record.getEid() + "\t" + affiliationLevel1.getAfid() +"\t" + affiliationLevel1.getAffiliation());
               writerAffils.newLine();
           }
        }



        writerAffils.flush();
        writerAffils.close();


        BufferedWriter writerAuthors = new BufferedWriter( new FileWriter( new File("Authors.txt")));

        System.out.println("writing Authors references to file");
        for(ScopusRecord record : records) {

            List<Author>  authorList = record.getAuthorList();

            for(Author author : authorList) {
                writerAuthors.write(record.getEid() + "\t" + author.getAuid() +"\t" + author.getSurnNme() +"\t" + author.getInitials() +"\t" +author.getSeq() + "\t" + author.getAffiliationsLevel2() );
                writerAuthors.newLine();
            }
        }

        writerAuthors.flush();
        writerAuthors.close();

    }


}
