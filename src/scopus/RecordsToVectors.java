package scopus;


import com.koloboke.collect.map.hash.HashObjIntMap;
import com.koloboke.collect.map.hash.HashObjIntMaps;
import misc.LanguageTools.RemoveCopyRightFromAbstract;
import misc.Parsers.SimpleParser;
import misc.Stemmers.EnglishStemmer;
import misc.stopwordLists.EnglishStopWords60;
import smile.data.SparseDataset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by crco0001 on 1/30/2018.
 */
public class RecordsToVectors {

    private final HashObjIntMap<String> termToIndiceText = HashObjIntMaps.getDefaultFactory().withNullKeyAllowed(false).withDefaultValue(-1).newMutableMap();
    private final HashObjIntMap<String> termToIndiceReferences = HashObjIntMaps.getDefaultFactory().withNullKeyAllowed(false).withDefaultValue(-1).newMutableMap();

    public int getZeroBasedIndexText(String term) {

        int indice = termToIndiceText.getInt(term);

        if(indice != -1) return indice;

        //else we havent seen this term before..

        indice = termToIndiceText.size();

        termToIndiceText.put(term,indice);

        return indice;

    }

    public int getZeroBasedIndexReferences(String term) {

        int indice = termToIndiceReferences.getInt(term);

        if(indice != -1) return indice;

        //else we havent seen this term before..

        indice = termToIndiceReferences.size();

        termToIndiceReferences.put(term,indice);

        return indice;

    }



    public static void main(String[] arg) throws IOException {


        System.out.println("Reading in serialized records..");
        List<ScopusRecord> records = SimplePersistor.deserializeList("records.ser");

        EnglishStemmer stemmer = new EnglishStemmer();
        EnglishStopWords60 englishStopWords60 = new EnglishStopWords60();


        SparseDataset textData = new SparseDataset();
        SparseDataset citationData = new SparseDataset();

        int recordId = 0;

        for(ScopusRecord record : records) {

            boolean isEnglish = "eng".equals(record.getLanguage());
            if(!isEnglish) continue;

            String text = record.getTitle();
            String summary = record.getSummaryText();
            if(summary != null) {

               summary =  RemoveCopyRightFromAbstract.cleanedAbstract(summary);
               text = text.concat(". ").concat(summary);
            }

            List<String> terms = SimpleParser.parse(text,true,englishStopWords60,stemmer);

            System.out.println(terms);

            List<String> aKeywords = record.getAuthorKeywords();
            if(aKeywords.size() > 0) {


              ListIterator<String> iter = aKeywords.listIterator();

              while(iter.hasNext()) {

                 String akey =  iter.next();

                String fixed = stemmer.stem( akey.toLowerCase() );
                iter.set(fixed);

              }

            }
            System.out.println(aKeywords);
            System.out.println("");



            List<CitedReference> citedReferenceList = record.getCitedReferences();
            List<String> citationKeys = new ArrayList<>();
            for(int i=0; i< citedReferenceList.size(); i++)  citationKeys.add( citedReferenceList.get(i).getRefID() );
            System.out.println(citationKeys);

            System.out.println("");




        }


    }









}
