package multiLabel;

import Database.CategoryDistributionForATerm;
import Database.MyOwnException;
import LibLinear.FeatureNodeComparator;
import SwePub.HsvCodeToName;
import SwePub.Record;
import com.koloboke.collect.map.*;
import com.koloboke.collect.map.hash.HashIntDoubleMaps;
import com.koloboke.collect.map.hash.HashIntIntMaps;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.map.hash.HashObjIntMaps;
import de.bwaldvogel.liblinear.FeatureNode;
import jsat.linear.SparseVector;
import jsat.utils.IntSet;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentMap;


public class EntropyTermWeights {

    private static final double LN_2 = Math.log(2);

    public static double log21p( double x )
    {

        return Math.log(x+1.0) / LN_2;

    }




    //Term(string) to feature index(int)
    private ObjIntMap<String> termToIndex;

    private IntIntMap termToDocFreq;

    // frequency distribution of term t over categories f(t,C_i)
    private IntObjMap<CategoryDistributionForATerm> indexToFrequencyDistributiony;

    //frequency sum of terms in category f(c_i)
    private IntIntMap categoryToNmberOTerms = HashIntIntMaps.getDefaultFactory().withDefaultValue(0).newMutableMap();
    private int currentIndex = 0;


    private IntDoubleMap indexToGlobalWeight;


    String language;
    int level;



    public EntropyTermWeights(String lang, int level) throws MyOwnException {
        if (!lang.equals("swe") && !lang.equals("eng")) throw new MyOwnException("language must be swe or eng");
        if (level != 3 && level != 5) throw new MyOwnException("level must be 3 or 5");

        this.language = lang;
        this.level = level;

        //see here http://leventov.github.io/Koloboke/api/1.0/java8/index.html#jdk-equivalents

        //for mapping a String  term to an integer index (initialize, but prune later?)
        this.termToIndex = HashObjIntMaps.getDefaultFactory().withDefaultValue(-1).withNullKeyAllowed(false).<String>newMutableMap(2000);

        //for pruning rare terms
        this.termToDocFreq = HashIntIntMaps.getDefaultFactory().withDefaultValue(0).newMutableMap(2000);
        //for mapping the integer index frequency distribution over categories
        this.indexToFrequencyDistributiony = HashIntObjMaps.getDefaultFactory().newMutableMap(2000);


    }


    public boolean indexRecord(Record record) throws MyOwnException {



        if (this.language.equals("swe")) {

            if (!record.isFullSwedishText()) return false;
        }

        if (this.language.equals("eng")) {

            if (!record.isFullEnglishText()) return false;

        }

        HashSet<Integer> classes = new HashSet<>();

        if (this.level == 3) {

            if (!record.containsLevel3Classification()) return false;

            Set<Integer> classificationCodes = record.getClassificationCodes();

            for(Integer code : classificationCodes) {

                Integer level3Code = HsvCodeToName.firstThreeDigitsOrNull(code);
                if(level3Code != null) classes.add(level3Code);
            }

        }


        if (this.level == 5) {

            if (!record.containsLevel5Classification()) return false;

            Set<Integer> classificationCodes = record.getClassificationCodes();

            for(Integer code : classificationCodes) {

                Integer level5Code = HsvCodeToName.firstFiveDigitsOrNull(code);
                if(level5Code != null) classes.add(level5Code);
            }

        }


        List<String> terms;


        if (this.language.equals("eng")) {
            terms = record.getLanguageSpecificTerms("eng");
        } else {

            terms = record.getLanguageSpecificTerms("swe");
        }

        terms.addAll( record.getTermsFromAffiliation() );
        terms.addAll( record.getTermsFromHost()  );
        terms.addAll( record.getUnkontrolledKkeywords() );
        String ISBN = record.getISBN();
        if(ISBN != null) terms.add( ISBN  );
        terms.addAll( record.getIssn());


        //if multi-label, add terms to each category and count term multiple times..

        IntSet keepTrack = new IntSet();

        for(Integer hsv_code : classes) {

            for (String t : terms) {

                this.categoryToNmberOTerms.addValue(hsv_code, 1);


                int index = this.termToIndex.getInt(t);


                if (index == -1) {

                    this.termToIndex.put(t, currentIndex);

                    keepTrack.add(currentIndex); //first time ever
                    this.termToDocFreq.addValue(currentIndex,1);

                    CategoryDistributionForATerm distribution_of_t_over_categories = new CategoryDistributionForATerm(this.level);
                    distribution_of_t_over_categories.incrementCategorySize(hsv_code);
                    this.indexToFrequencyDistributiony.put(currentIndex, distribution_of_t_over_categories);
                    currentIndex++;


                } else { //the term is already mapped to an integer index

                    CategoryDistributionForATerm distribution_of_t_over_categories = this.indexToFrequencyDistributiony.get(index);

                    if(!keepTrack.contains(index)) {

                        termToDocFreq.addValue(index,1);
                        keepTrack.add(index);
                    }


                    //increse the number of occuring terms in the category by one
                    distribution_of_t_over_categories.incrementCategorySize(hsv_code);


                }


            } //term processing done

        }

        return true; //terms from document addedd




    }

    public void printStat(String fileName) throws IOException {

        BufferedWriter writer = new BufferedWriter( new FileWriter( new File(fileName)));


        for(ObjIntCursor<String> cur = termToIndex.cursor(); cur.moveNext();) {


            writer.write(cur.key() +"\t" + cur.value() +"\t" + this.termToDocFreq.get( cur.value() ) +"\t" + this.indexToFrequencyDistributiony.get(cur.value()).getTotalFrequency() );
            writer.newLine();


        }

        writer.flush();
        writer.close();

    }

    public void indexSize() {

        System.out.println("index size");
        System.out.println(this.termToIndex.size() +" " +  this.indexToFrequencyDistributiony.size() + " " + this.termToDocFreq.size() );

    }

    public void reduceIndex(int minDocFreq) {


        for(ObjIntCursor<String> cur = this.termToIndex.cursor(); cur.moveNext(); ) {

            //String term = cur.key();
            int index = cur.value();

            CategoryDistributionForATerm distribution_for_term_t = this.indexToFrequencyDistributiony.get(index);
            int docFreq = this.termToDocFreq.get(index);


            if(docFreq <= minDocFreq) {

                //remove the term mapping & index to to term distribution

                for(IntIntCursor cur2 = distribution_for_term_t.categoryToFrequency.cursor(); cur2.moveNext();) {


                    int category = cur2.key();
                    int t_freq_in_cat = cur2.value();
                    this.categoryToNmberOTerms.addValue(category,-t_freq_in_cat); //add negative number

                }


                this.indexToFrequencyDistributiony.remove(index);
                this.termToDocFreq.remove(index);
                cur.remove(); //remove from this.termToIndex



            }



        }


        this.termToDocFreq.shrink();
        this.termToIndex.shrink();
        this.indexToFrequencyDistributiony.shrink();



        //reorder index from 0...new_N

        int newIndex = 0;

        IntObjMap<CategoryDistributionForATerm> newIndexToFrequencyDistributiony = HashIntObjMaps.getDefaultFactory().newMutableMap(2000);
        IntIntMap newTermToDocFreq = HashIntIntMaps.getDefaultFactory().withDefaultValue(0).newMutableMap(2000);


        for(ObjIntCursor<String> cur = this.termToIndex.cursor(); cur.moveNext();) {

            int currentIndex = cur.value();

            CategoryDistributionForATerm distributionForATerm = this.indexToFrequencyDistributiony.get(currentIndex);

            int docFreq = this.termToDocFreq.get(currentIndex);

            cur.setValue(newIndex);
            newIndexToFrequencyDistributiony.put(newIndex,distributionForATerm);
            newTermToDocFreq.put(newIndex,docFreq);

            this.indexToFrequencyDistributiony.remove(currentIndex);

            newIndex++;


        }

        //overwrite
        this.indexToFrequencyDistributiony = newIndexToFrequencyDistributiony;
        this.termToDocFreq = newTermToDocFreq;

    }

    public int getIndex(String term) {

        return this.termToIndex.getInt(term);
    }

    public CategoryDistributionForATerm getCategoryDistFor(String term) {

        int index = getIndex(term);
        if(index == -1) return null;

        return this.indexToFrequencyDistributiony.get(index);

    }
    public CategoryDistributionForATerm getCategoryDistFor(int index) {

        return this.indexToFrequencyDistributiony.get(index);

    }




    public void calculateEntropyWeights() {

        System.out.println("calculating global weights..");
        /*

            Balanced distributional concentration

            btc(t) = 1 - BH(t) / log(|C|)

            :

             p(t|Ci) =  f(t,Ci)/f(Ci)


           btc(t)=  1 + sum_i_to_|C|: [p(t|Ci)/sum_i_to_|C|:p(t|Ci)] *log( [p(t|Ci)/sum_i_to_|C|:p(t|Ci)]  ) / norm constant


           p(t|Ci) =  f(t,Ci)/f(Ci)



           from Entropy-based Term Weighting schemes for text categorization in SVM (btc)
         */



        this.indexToGlobalWeight = HashIntDoubleMaps.getDefaultFactory().withDefaultValue(-1).newMutableMap();

        int nrCategories = categoryToNmberOTerms.size(); // |C|

        for(ObjIntCursor<String> cur = termToIndex.cursor(); cur.moveNext();) { //for each term

            //String term = cur.key();
            int index = cur.value();

            List<Integer> f_of_t_given_Ci = new ArrayList<>();

            List<Integer> f_of_Ci = new ArrayList<>();

            CategoryDistributionForATerm dist = this.indexToFrequencyDistributiony.get(index);


            for(IntIntCursor cur2 = dist.categoryToFrequency.cursor(); cur2.moveNext();) {

                int category_index = cur2.key();
                int frequency_of_t = cur2.value();

                f_of_t_given_Ci.add(frequency_of_t);
                f_of_Ci.add(  this.categoryToNmberOTerms.get(category_index)   );


            }


            List<Double> p_of_t_given_c = new ArrayList<>();


            for(int i=0; i<f_of_t_given_Ci.size(); i++) {

                p_of_t_given_c.add( f_of_t_given_Ci.get(i)/  ( (double)f_of_Ci.get(i) ) );
            }


            double sum_of_t_given_c = 0;

            for(int i=0; i<p_of_t_given_c.size(); i++) { sum_of_t_given_c += p_of_t_given_c.get(i); }


            double btc = 0;

            //as we are normalizing with log(C) it dosent matter which base we use.. but we can use log2(term_freq +1) later

            for(int i=0; i<p_of_t_given_c.size(); i++) {


                btc +=    (p_of_t_given_c.get(i) / sum_of_t_given_c) * Math.log(  (p_of_t_given_c.get(i) / sum_of_t_given_c)       );
            }


            this.indexToGlobalWeight.put(index,  1 +  (btc/Math.log(nrCategories)) );
        }


        System.out.println("Global term weights calculated for: " + this.indexToGlobalWeight.size() + " terms");

    }


    public void printEntropyWeights(String fileName) throws IOException {


        BufferedWriter writer = new BufferedWriter( new FileWriter( new File(fileName)));


        for(ObjIntCursor<String> cur = termToIndex.cursor(); cur.moveNext();) {


            writer.write(cur.key() +"\t" + cur.value() +"\t" + this.termToDocFreq.get( cur.value() ) +"\t" + this.indexToGlobalWeight.get(cur.value()));
            writer.newLine();


        }

        writer.flush();
        writer.close();

    }




    public void writeToMapDB () throws IOException
    {

        System.out.println("Saving term mapping and term weights to file (termIndex.db)");
        File persist = new File("termIndex." + this.language+"." +this.level +".db");

        if (persist.exists()){
            persist.delete();
        }


        DB dbPersist = DBMaker.fileDB(persist)
                .fileMmapEnableIfSupported()
                .fileMmapPreclearDisable()   // Make mmap file faster
                .allocateStartSize(94*1024*1024) // 94MB
                .allocateIncrement(4*1024*1024) //  4MB
                .executorEnable() // Enables background executor
                .closeOnJvmShutdown()
                .make();

        ConcurrentMap<String, Integer> termToIndexDB = dbPersist.hashMap("TermToIndex")
                .counterEnable()
                .keySerializer(   Serializer.STRING  )
                .valueSerializer( Serializer.INTEGER )
                .layout(8,24,4)  //Maximal Hash Table Size is calculated as: segment * node size ^ level count: here: 2654208
                .create(); //or open, this just overwrites


        ConcurrentMap<Integer, Double> indexToGlobalWeightDB = dbPersist.hashMap("IndexToGlobalWeight")
                .counterEnable()
                .keySerializer(   Serializer.INTEGER  )
                .valueSerializer( Serializer.DOUBLE )
                .layout(8,24,4)  //Maximal Hash Table Size is calculated as: segment * node size ^ level count: here: 2654208
                .create(); //or open, this just overwrites


        for(ObjIntCursor<String> cur = this.termToIndex.cursor(); cur.moveNext(); ) {

            termToIndexDB.put(cur.key(), cur.value());

        }


        for(IntDoubleCursor cur = this.indexToGlobalWeight.cursor(); cur.moveNext(); ) {

            indexToGlobalWeightDB.put(cur.key(), cur.value());

        }


        dbPersist.close();
    }



    public void readFromMapDB(String directory) {

        File persist;
        if(directory == null) {

            persist = new File("termIndex." + this.language+"." +this.level +".db");

        } else {
            persist = new File(directory + "termIndex." + this.language+"." +this.level +".db");


        }


        DB dbPersist = DBMaker.fileDB(persist)
                .fileMmapEnableIfSupported()
                .fileMmapPreclearDisable()   // Make mmap file faster
                .allocateStartSize(94*1024*1024) // 94MB
                .allocateIncrement(4*1024*1024) //  4MB
                .executorEnable() // Enables background executor
                .closeOnJvmShutdown()
                .make();

        ConcurrentMap<String, Integer> termToIndexDB = dbPersist.hashMap("TermToIndex")
                .counterEnable()
                .keySerializer(   Serializer.STRING  )
                .valueSerializer( Serializer.INTEGER )
                .layout(8,24,4)  //Maximal Hash Table Size is calculated as: segment * node size ^ level count: here: 2654208
                .createOrOpen(); //or open, this just overwrites

        ConcurrentMap<Integer, Double> indexToGlobalWeightDB = dbPersist.hashMap("IndexToGlobalWeight")
                .counterEnable()
                .keySerializer(   Serializer.INTEGER  )
                .valueSerializer( Serializer.DOUBLE )
                .layout(8,24,4)  //Maximal Hash Table Size is calculated as: segment * node size ^ level count: here: 2654208
                .createOrOpen(); //or open, this just overwrites


        this.termToIndex = HashObjIntMaps.getDefaultFactory().withDefaultValue(-1).withNullKeyAllowed(false).<String>newMutableMap(10000);
        this.indexToGlobalWeight = HashIntDoubleMaps.getDefaultFactory().withDefaultValue(-1).newMutableMap();


        for (Map.Entry<Integer, Double> entry : indexToGlobalWeightDB.entrySet()) {

            this.indexToGlobalWeight.put(entry.getKey().intValue(), entry.getValue().doubleValue() );

        }


        for(Map.Entry<String,Integer> entry : termToIndexDB.entrySet()) {

            this.termToIndex.put(entry.getKey(),entry.getValue().intValue());

        }



        dbPersist.close();
        System.out.println("Read " + this.termToIndex.size()+ " term mappings and " + indexToGlobalWeight.size() + " global weights ");
    }


    public ArrayList<FeatureNode> getFeatureNodeList(Record record) {

        int N = this.termToIndex.size();

        ArrayList<String> terms;

        if (this.language.equals("eng")) {
            terms = record.getLanguageSpecificTerms("eng");
        } else {

            terms = record.getLanguageSpecificTerms("swe");
        }

        if(terms.size() < 1) return null;

        terms.addAll( record.getTermsFromAffiliation() );
        terms.addAll( record.getTermsFromHost()  );
        terms.addAll( record.getUnkontrolledKkeywords() );
        String ISBN = record.getISBN();
        if(ISBN != null) terms.add( ISBN  );
        terms.addAll( record.getIssn());

        HashMap<Integer,FeatureNode> featureNodes = new HashMap<>(20);


        for (String t : terms) {

            int index = getIndex(t)+1; //1-based..

            if(index != 0) { //-1 +1

                FeatureNode featureNode = featureNodes.get(index);

                if(featureNode == null) {

                    featureNode = new FeatureNode(index,1.0);
                    featureNodes.put(index, featureNode);

                } else {

                    featureNode.setValue(   featureNode.getValue()+1 );

                }

            }
        }



        if(featureNodes.size() == 0) return  null;

        //else add bias term and return List

        featureNodes.put(N+1, new FeatureNode(N+1,1.0));

        ArrayList<FeatureNode> featureNodeList = new ArrayList<>(featureNodes.size());
        featureNodeList.addAll( featureNodes.values() );
        Collections.sort(featureNodeList, new FeatureNodeComparator());


        return featureNodeList;


    }


    public void applyWeighting(List<FeatureNode> featureNodeList) {

        //the vector is sorted and the laste featureNode is the bias term
        for(int i=0; i<featureNodeList.size()-1; i++) {

            FeatureNode featureNode = featureNodeList.get(i);
            int index = featureNode.getIndex()-1; // zero based lookup
            double weight = this.indexToGlobalWeight.get(index);
            if(weight == -1) { System.out.println("catastrophic failure! No weight for index=" + index); System.exit(0); }

            double w = weight * log21p( featureNode.value );
            featureNode.setValue(w);


        }


    }



}
