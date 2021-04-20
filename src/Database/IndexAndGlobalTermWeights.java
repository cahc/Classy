package Database;

import SwePub.HsvCodeToName;
import SwePub.Record;
import TrainAndPredict.VecHsvPair;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.koloboke.collect.map.*;
import com.koloboke.collect.map.hash.*;
import jsat.classifiers.CategoricalData;
import jsat.linear.IndexValue;
import jsat.linear.SparseVector;
import jsat.linear.Vec;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Cristian on 2017-01-12.
 */
public class IndexAndGlobalTermWeights {

   // BiMap<Integer, Integer> hello = HashBiMap.create();
    public static final BiMap<Integer,Integer> level5ToCategoryCodes = HashBiMap.create();
    
    static {

        level5ToCategoryCodes.put(10101, 0);
        level5ToCategoryCodes.put(10102, 1);
        level5ToCategoryCodes.put(10103, 2);
        level5ToCategoryCodes.put(10104, 3);
        level5ToCategoryCodes.put(10105, 4);
        level5ToCategoryCodes.put(10106, 5);
        level5ToCategoryCodes.put(10199, 6);
        level5ToCategoryCodes.put(10201, 7);
        level5ToCategoryCodes.put(10202, 8);
        level5ToCategoryCodes.put(10203, 9);
        level5ToCategoryCodes.put(10204, 10);
        level5ToCategoryCodes.put(10205, 11);
        level5ToCategoryCodes.put(10206, 12);
        level5ToCategoryCodes.put(10207, 13);
        level5ToCategoryCodes.put(10208, 14);
        level5ToCategoryCodes.put(10209, 15);
        level5ToCategoryCodes.put(10299, 16);
        level5ToCategoryCodes.put(10301, 17);
        level5ToCategoryCodes.put(10302, 18);
        level5ToCategoryCodes.put(10303, 19);
        level5ToCategoryCodes.put(10304, 20);
        level5ToCategoryCodes.put(10305, 21);
        level5ToCategoryCodes.put(10306, 22);
        level5ToCategoryCodes.put(10399, 23);
        level5ToCategoryCodes.put(10401, 24);
        level5ToCategoryCodes.put(10402, 25);
        level5ToCategoryCodes.put(10403, 26);
        level5ToCategoryCodes.put(10404, 27);
        level5ToCategoryCodes.put(10405, 28);
        level5ToCategoryCodes.put(10406, 29);
        level5ToCategoryCodes.put(10407, 30);
        level5ToCategoryCodes.put(10499, 31);
        level5ToCategoryCodes.put(10501, 32);
        level5ToCategoryCodes.put(10502, 33);
        level5ToCategoryCodes.put(10503, 34);
        level5ToCategoryCodes.put(10504, 35);
        level5ToCategoryCodes.put(10505, 36);
        level5ToCategoryCodes.put(10506, 37);
        level5ToCategoryCodes.put(10507, 38);
        level5ToCategoryCodes.put(10508, 39);
        level5ToCategoryCodes.put(10509, 40);
        level5ToCategoryCodes.put(10599, 41);
        level5ToCategoryCodes.put(10601, 42);
        level5ToCategoryCodes.put(10602, 43);
        level5ToCategoryCodes.put(10603, 44);
        level5ToCategoryCodes.put(10604, 45);
        level5ToCategoryCodes.put(10605, 46);
        level5ToCategoryCodes.put(10606, 47);
        level5ToCategoryCodes.put(10607, 48);
        level5ToCategoryCodes.put(10608, 49);
        level5ToCategoryCodes.put(10609, 50);
        level5ToCategoryCodes.put(10610, 51);
        level5ToCategoryCodes.put(10611, 52);
        level5ToCategoryCodes.put(10612, 53);
        level5ToCategoryCodes.put(10613, 54);
        level5ToCategoryCodes.put(10614, 55);
        level5ToCategoryCodes.put(10615, 56);
        level5ToCategoryCodes.put(10699, 57);
        level5ToCategoryCodes.put(10799, 58);
        level5ToCategoryCodes.put(20101, 59);
        level5ToCategoryCodes.put(20102, 60);
        level5ToCategoryCodes.put(20103, 61);
        level5ToCategoryCodes.put(20104, 62);
        level5ToCategoryCodes.put(20105, 63);
        level5ToCategoryCodes.put(20106, 64);
        level5ToCategoryCodes.put(20107, 65);
        level5ToCategoryCodes.put(20108, 66);
        level5ToCategoryCodes.put(20199, 67);
        level5ToCategoryCodes.put(20201, 68);
        level5ToCategoryCodes.put(20202, 69);
        level5ToCategoryCodes.put(20203, 70);
        level5ToCategoryCodes.put(20204, 71);
        level5ToCategoryCodes.put(20205, 72);
        level5ToCategoryCodes.put(20206, 73);
        level5ToCategoryCodes.put(20207, 74);
        level5ToCategoryCodes.put(20299, 75);
        level5ToCategoryCodes.put(20301, 76);
        level5ToCategoryCodes.put(20302, 77);
        level5ToCategoryCodes.put(20303, 78);
        level5ToCategoryCodes.put(20304, 79);
        level5ToCategoryCodes.put(20305, 80);
        level5ToCategoryCodes.put(20306, 81);
        level5ToCategoryCodes.put(20307, 82);
        level5ToCategoryCodes.put(20308, 83);
        level5ToCategoryCodes.put(20399, 84);
        level5ToCategoryCodes.put(20401, 85);
        level5ToCategoryCodes.put(20402, 86);
        level5ToCategoryCodes.put(20403, 87);
        level5ToCategoryCodes.put(20404, 88);
        level5ToCategoryCodes.put(20499, 89);
        level5ToCategoryCodes.put(20501, 90);
        level5ToCategoryCodes.put(20502, 91);
        level5ToCategoryCodes.put(20503, 92);
        level5ToCategoryCodes.put(20504, 93);
        level5ToCategoryCodes.put(20505, 94);
        level5ToCategoryCodes.put(20506, 95);
        level5ToCategoryCodes.put(20599, 96);
        level5ToCategoryCodes.put(20601, 97);
        level5ToCategoryCodes.put(20602, 98);
        level5ToCategoryCodes.put(20603, 99);
        level5ToCategoryCodes.put(20604, 100);
        level5ToCategoryCodes.put(20605, 101);
        level5ToCategoryCodes.put(20699, 102);
        level5ToCategoryCodes.put(20701, 103);
        level5ToCategoryCodes.put(20702, 104);
        level5ToCategoryCodes.put(20703, 105);
        level5ToCategoryCodes.put(20704, 106);
        level5ToCategoryCodes.put(20705, 107);
        level5ToCategoryCodes.put(20706, 108);
        level5ToCategoryCodes.put(20707, 109);
        level5ToCategoryCodes.put(20799, 110);
        level5ToCategoryCodes.put(20801, 111);
        level5ToCategoryCodes.put(20802, 112);
        level5ToCategoryCodes.put(20803, 113);
        level5ToCategoryCodes.put(20804, 114);
        level5ToCategoryCodes.put(20899, 115);
        level5ToCategoryCodes.put(20901, 116);
        level5ToCategoryCodes.put(20902, 117);
        level5ToCategoryCodes.put(20903, 118);
        level5ToCategoryCodes.put(20904, 119);
        level5ToCategoryCodes.put(20905, 120);
        level5ToCategoryCodes.put(20906, 121);
        level5ToCategoryCodes.put(20907, 122);
        level5ToCategoryCodes.put(20908, 123);
        level5ToCategoryCodes.put(20999, 124);
        level5ToCategoryCodes.put(21001, 125);
        level5ToCategoryCodes.put(21101, 126);
        level5ToCategoryCodes.put(21102, 127);
        level5ToCategoryCodes.put(21103, 128);
        level5ToCategoryCodes.put(21199, 129);
        level5ToCategoryCodes.put(30101, 130);
        level5ToCategoryCodes.put(30102, 131);
        level5ToCategoryCodes.put(30103, 132);
        level5ToCategoryCodes.put(30104, 133);
        level5ToCategoryCodes.put(30105, 134);
        level5ToCategoryCodes.put(30106, 135);
        level5ToCategoryCodes.put(30107, 136);
        level5ToCategoryCodes.put(30108, 137);
        level5ToCategoryCodes.put(30109, 138);
        level5ToCategoryCodes.put(30110, 139);
        level5ToCategoryCodes.put(30199, 140);
        level5ToCategoryCodes.put(30201, 141);
        level5ToCategoryCodes.put(30202, 142);
        level5ToCategoryCodes.put(30203, 143);
        level5ToCategoryCodes.put(30204, 144);
        level5ToCategoryCodes.put(30205, 145);
        level5ToCategoryCodes.put(30206, 146);
        level5ToCategoryCodes.put(30207, 147);
        level5ToCategoryCodes.put(30208, 148);
        level5ToCategoryCodes.put(30209, 149);
        level5ToCategoryCodes.put(30210, 150);
        level5ToCategoryCodes.put(30211, 151);
        level5ToCategoryCodes.put(30212, 152);
        level5ToCategoryCodes.put(30213, 153);
        level5ToCategoryCodes.put(30214, 154);
        level5ToCategoryCodes.put(30215, 155);
        level5ToCategoryCodes.put(30216, 156);
        level5ToCategoryCodes.put(30217, 157);
        level5ToCategoryCodes.put(30218, 158);
        level5ToCategoryCodes.put(30219, 159);
        level5ToCategoryCodes.put(30220, 160);
        level5ToCategoryCodes.put(30221, 161);
        level5ToCategoryCodes.put(30222, 162);
        level5ToCategoryCodes.put(30223, 163);
        level5ToCategoryCodes.put(30224, 164);
        level5ToCategoryCodes.put(30299, 165);
        level5ToCategoryCodes.put(30301, 166);
        level5ToCategoryCodes.put(30302, 167);
        level5ToCategoryCodes.put(30303, 168);
        level5ToCategoryCodes.put(30304, 169);
        level5ToCategoryCodes.put(30305, 170);
        level5ToCategoryCodes.put(30306, 171);
        level5ToCategoryCodes.put(30307, 172);
        level5ToCategoryCodes.put(30308, 173);
        level5ToCategoryCodes.put(30309, 174);
        level5ToCategoryCodes.put(30310, 175);
        level5ToCategoryCodes.put(30399, 176);
        level5ToCategoryCodes.put(30401, 177);
        level5ToCategoryCodes.put(30402, 178);
        level5ToCategoryCodes.put(30403, 179);
        level5ToCategoryCodes.put(30499, 180);
        level5ToCategoryCodes.put(30501, 181);
        level5ToCategoryCodes.put(30502, 182);
        level5ToCategoryCodes.put(30599, 183);
        level5ToCategoryCodes.put(40101, 184);
        level5ToCategoryCodes.put(40102, 185);
        level5ToCategoryCodes.put(40103, 186);
        level5ToCategoryCodes.put(40104, 187);
        level5ToCategoryCodes.put(40105, 188);
        level5ToCategoryCodes.put(40106, 189);
        level5ToCategoryCodes.put(40107, 190);
        level5ToCategoryCodes.put(40108, 191);
        level5ToCategoryCodes.put(40201, 192);
        level5ToCategoryCodes.put(40301, 193);
        level5ToCategoryCodes.put(40302, 194);
        level5ToCategoryCodes.put(40303, 195);
        level5ToCategoryCodes.put(40304, 196);
        level5ToCategoryCodes.put(40401, 197);
        level5ToCategoryCodes.put(40402, 198);
        level5ToCategoryCodes.put(40501, 199);
        level5ToCategoryCodes.put(40502, 200);
        level5ToCategoryCodes.put(40503, 201);
        level5ToCategoryCodes.put(40504, 202);
        level5ToCategoryCodes.put(40599, 203);
        level5ToCategoryCodes.put(50101, 204);
        level5ToCategoryCodes.put(50102, 205);
        level5ToCategoryCodes.put(50201, 206);
        level5ToCategoryCodes.put(50202, 207);
        level5ToCategoryCodes.put(50203, 208);
        level5ToCategoryCodes.put(50301, 209);
        level5ToCategoryCodes.put(50302, 210);
        level5ToCategoryCodes.put(50303, 211);
        level5ToCategoryCodes.put(50304, 212);
        level5ToCategoryCodes.put(50401, 213);
        level5ToCategoryCodes.put(50402, 214);
        level5ToCategoryCodes.put(50403, 215);
        level5ToCategoryCodes.put(50404, 216);
        level5ToCategoryCodes.put(50501, 217);
        level5ToCategoryCodes.put(50502, 218);
        level5ToCategoryCodes.put(50601, 219);
        level5ToCategoryCodes.put(50602, 220);
        level5ToCategoryCodes.put(50603, 221);
        level5ToCategoryCodes.put(50701, 222);
        level5ToCategoryCodes.put(50702, 223);
        level5ToCategoryCodes.put(50801, 224);
        level5ToCategoryCodes.put(50802, 225);
        level5ToCategoryCodes.put(50803, 226);
        level5ToCategoryCodes.put(50804, 227);
        level5ToCategoryCodes.put(50805, 228);
        level5ToCategoryCodes.put(50901, 229);
        level5ToCategoryCodes.put(50902, 230);
        level5ToCategoryCodes.put(50903, 231);
        level5ToCategoryCodes.put(50904, 232);
        level5ToCategoryCodes.put(50999, 233);
        level5ToCategoryCodes.put(60101, 234);
        level5ToCategoryCodes.put(60102, 235);
        level5ToCategoryCodes.put(60103, 236);
        level5ToCategoryCodes.put(60201, 237);
        level5ToCategoryCodes.put(60202, 238);
        level5ToCategoryCodes.put(60203, 239);
        level5ToCategoryCodes.put(60204, 240);
        level5ToCategoryCodes.put(60301, 241);
        level5ToCategoryCodes.put(60302, 242);
        level5ToCategoryCodes.put(60303, 243);
        level5ToCategoryCodes.put(60304, 244);
        level5ToCategoryCodes.put(60305, 245);
        level5ToCategoryCodes.put(60401, 246);
        level5ToCategoryCodes.put(60402, 247);
        level5ToCategoryCodes.put(60403, 248);
        level5ToCategoryCodes.put(60404, 249);
        level5ToCategoryCodes.put(60405, 250);
        level5ToCategoryCodes.put(60406, 251);
        level5ToCategoryCodes.put(60407, 252);
        level5ToCategoryCodes.put(60408, 253);
        level5ToCategoryCodes.put(60409, 254);
        level5ToCategoryCodes.put(60410, 255);
        level5ToCategoryCodes.put(60501, 256);
        level5ToCategoryCodes.put(60502, 257);
        level5ToCategoryCodes.put(60503, 258);
        level5ToCategoryCodes.put(60599, 259);
        
    }

    public static final BiMap<Integer, Integer> level3ToCategoryCodes = HashBiMap.create();
    static {
        level3ToCategoryCodes.put(101, 0);
        level3ToCategoryCodes.put(102, 1);
        level3ToCategoryCodes.put(103, 2);
        level3ToCategoryCodes.put(104, 3);
        level3ToCategoryCodes.put(105, 4);
        level3ToCategoryCodes.put(106, 5);
        level3ToCategoryCodes.put(107, 6);
        level3ToCategoryCodes.put(201, 7);
        level3ToCategoryCodes.put(202, 8);
        level3ToCategoryCodes.put(203, 9);
        level3ToCategoryCodes.put(204, 10);
        level3ToCategoryCodes.put(205, 11);
        level3ToCategoryCodes.put(206, 12);
        level3ToCategoryCodes.put(207, 13);
        level3ToCategoryCodes.put(208, 14);
        level3ToCategoryCodes.put(209, 15);
        level3ToCategoryCodes.put(210, 16);
        level3ToCategoryCodes.put(211, 17);
        level3ToCategoryCodes.put(301, 18);
        level3ToCategoryCodes.put(302, 19);
        level3ToCategoryCodes.put(303, 20);
        level3ToCategoryCodes.put(304, 21);
        level3ToCategoryCodes.put(305, 22);
        level3ToCategoryCodes.put(401, 23);
        level3ToCategoryCodes.put(402, 24);
        level3ToCategoryCodes.put(403, 25);
        level3ToCategoryCodes.put(404, 26);
        level3ToCategoryCodes.put(405, 27);
        level3ToCategoryCodes.put(501, 28);
        level3ToCategoryCodes.put(502, 29);
        level3ToCategoryCodes.put(503, 30);
        level3ToCategoryCodes.put(504, 31);
        level3ToCategoryCodes.put(505, 32);
        level3ToCategoryCodes.put(506, 33);
        level3ToCategoryCodes.put(507, 34);
        level3ToCategoryCodes.put(508, 35);
        level3ToCategoryCodes.put(509, 36);
        level3ToCategoryCodes.put(601, 37);
        level3ToCategoryCodes.put(602, 38);
        level3ToCategoryCodes.put(603, 39);
        level3ToCategoryCodes.put(604, 40);
        level3ToCategoryCodes.put(605, 41);
    }

    public static final CategoricalData level3CategoryInformation = new CategoricalData(42);

    public static final CategoricalData level5CategoryInformation = new CategoricalData(260);



    //Term(string) to feature index(int)
    private ObjIntMap<String> termToIndex;

    // frequency distribution of term t over categories f(t,C_i)
    private IntObjMap<CategoryDistributionForATerm> indexToFrequencyDistributiony;

    //number of docs in category #(c_i)
    private IntIntMap categoryToNmberOfDocs = HashIntIntMaps.getDefaultFactory().withDefaultValue(0).newMutableMap(); // for selecting which categry to choose to represent a record (choose the rare one)

    //frequency sum of terms in category f(c_i)
    private IntIntMap categoryToNmberOTerms = HashIntIntMaps.getDefaultFactory().withDefaultValue(0).newMutableMap();


    private IntDoubleMap indexToGlobalWeight;

    private int currentIndex = 0;

    String language;
    int level;


    public int getLevel() {



        return level;
    }

    public String getLang() {

        return language;
    }
    public int getNrTerms() {

        return this.termToIndex.size();
    }


    public void printStat(String fileName) throws IOException {

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream( new File(fileName)),"UTF-8"));

        for(ObjIntCursor<String> cur = this.termToIndex.cursor(); cur.moveNext(); ) {

            String term = cur.key();
            int index = cur.value();

            CategoryDistributionForATerm distribution_for_term_t = this.indexToFrequencyDistributiony.get(index);

            out.write(term +"\t" + index + "\t" + distribution_for_term_t.printCategoryDistributionForATerm());
            out.newLine();

        }


        out.flush();
        out.close();



    }


    public void printTermAndGlobalWeights(String fileName) throws IOException {


        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream( new File(fileName)),"UTF-8"));

        for(ObjIntCursor<String> cur = this.termToIndex.cursor(); cur.moveNext(); ) {

            String term = cur.key();
            int index = cur.value();

            CategoryDistributionForATerm distribution_for_term_t = this.indexToFrequencyDistributiony.get(index);

            out.write(term +"\t" + index + "\t" + distribution_for_term_t.printCategoryDistributionForATerm() +"\t" + this.indexToGlobalWeight.get(index)  );
            out.newLine();

        }


        out.flush();
        out.close();



    }



    //pre call this and use it to guide decisions on which class to choose when multiclassed records is seen
    public void  setUpClassDistribution( FileHashDB DB  ) throws MyOwnException {


        if (!this.language.equals("swe") && !this.language.equals("eng")) throw new MyOwnException("language must be swe or eng");
        if (this.level != 3 && this.level != 5) throw new MyOwnException("level must be 3 or 5");

        for(Map.Entry<String,Record> entry : DB.database.entrySet() ) {

            Record r = entry.getValue();

            if (this.language.equals("swe")) {

                if (!r.isFullSwedishText()) continue;
            }

            if (this.language.equals("eng")) {

                if (!r.isFullEnglishText()) continue;

            }


            if (this.level == 3) {

                if (!r.containsLevel3Classification()) continue;

                Set<Integer> classificationCodes = r.getClassificationCodes();

                for(Integer code : classificationCodes) {

                    Integer level3Code = HsvCodeToName.firstThreeDigitsOrNull(code);
                    if(level3Code != null) categoryToNmberOfDocs.addValue(level3Code.intValue(),1);
                }

            }


            if (this.level == 5) {

                if (!r.containsLevel5Classification()) continue;

                Set<Integer> classificationCodes = r.getClassificationCodes();

                for(Integer code : classificationCodes) {

                    Integer level5Code = HsvCodeToName.firstFiveDigitsOrNull(code);
                    if(level5Code != null) categoryToNmberOfDocs.addValue(level5Code.intValue(),1);
                }

            }


        }

        System.out.println("Lang=" + this.language +" level=" +this.level +". Mappings from HSV code to # docs (including dups): ");

        for(IntIntCursor cur = categoryToNmberOfDocs.cursor(); cur.moveNext();) {

            int category = cur.key();
            int nr_doc = cur.value();

            System.out.println(HsvCodeToName.getCategoryInfo(category) +"\t"+nr_doc);

            }



    }


    public IndexAndGlobalTermWeights(String lang, int level) throws MyOwnException {


        if (!lang.equals("swe") && !lang.equals("eng")) throw new MyOwnException("language must be swe or eng");
        if (level != 3 && level != 5) throw new MyOwnException("level must be 3 or 5");

        this.language = lang;
        this.level = level;

        //see here http://leventov.github.io/Koloboke/api/1.0/java8/index.html#jdk-equivalents

        //for mapping a String  term to an integer index (initialize, but prune later?)
        this.termToIndex = HashObjIntMaps.getDefaultFactory().withDefaultValue(-1).withNullKeyAllowed(false).<String>newMutableMap(2000);

        //for mapping the integer index frequency distribution over categories
        this.indexToFrequencyDistributiony = HashIntObjMaps.getDefaultFactory().newMutableMap(2000);


    }


    public boolean addTermsToIndex(Record record) throws MyOwnException {

        //for seleting category in cases of multiple categories for a document



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


        //select class

        int hsv_code = -99;

        if(classes.size() > 1) {

            //reduce to one
            int intMinSize = Integer.MAX_VALUE;
            for(Integer i : classes ) {

               int size = this.categoryToNmberOfDocs.get(i.intValue());

                if(size < intMinSize) {hsv_code = i; intMinSize = size;}
            }


        } else {hsv_code = classes.iterator().next(); }



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




        for (String t : terms) {

            this.categoryToNmberOTerms.addValue(hsv_code,1);


            int index = this.termToIndex.getInt(t);

            if (index == -1) {

                this.termToIndex.put(t, currentIndex);
                CategoryDistributionForATerm distribution_of_t_over_categories = new CategoryDistributionForATerm(this.level);
                distribution_of_t_over_categories.incrementCategorySize(hsv_code);
                this.indexToFrequencyDistributiony.put(currentIndex,distribution_of_t_over_categories);

                currentIndex++;


            } else { //the term is already mapped to an integer index

                CategoryDistributionForATerm distribution_of_t_over_categories = this.indexToFrequencyDistributiony.get(index);


                    //increse the number of occuring terms in the category by one
                    distribution_of_t_over_categories.incrementCategorySize(hsv_code);


            }


        } //term processing done


        return true; //terms from document addedd
    }


    public void removeRareTerms(int rareThreshold) {


        for(ObjIntCursor<String> cur = this.termToIndex.cursor(); cur.moveNext(); ) {

            //String term = cur.key();
            int index = cur.value();

            CategoryDistributionForATerm distribution_for_term_t = this.indexToFrequencyDistributiony.get(index);

            if(distribution_for_term_t.getTotalFrequency() < rareThreshold) {

            //remove the term mapping & index to to term distribution

                        for(IntIntCursor cur2 = distribution_for_term_t.categoryToFrequency.cursor(); cur2.moveNext();) {


                            int category = cur2.key();
                            int t_freq_in_cat = cur2.value();
                            this.categoryToNmberOTerms.addValue(category,-t_freq_in_cat); //add negative number

                        }


            this.indexToFrequencyDistributiony.remove(index);
            cur.remove(); //remove from this.termToIndex



            }



        }

        this.termToIndex.shrink();
        this.indexToFrequencyDistributiony.shrink();

        //reorder index from 0...new_N

        int newIndex = 0;

        IntObjMap<CategoryDistributionForATerm> newIndexToFrequencyDistributiony = HashIntObjMaps.getDefaultFactory().newMutableMap(2000);


        for(ObjIntCursor<String> cur = this.termToIndex.cursor(); cur.moveNext();) {

            int currentIndex = cur.value();

            CategoryDistributionForATerm distributionForATerm = this.indexToFrequencyDistributiony.get(currentIndex);

            cur.setValue(newIndex);
            newIndexToFrequencyDistributiony.put(newIndex,distributionForATerm);
            this.indexToFrequencyDistributiony.remove(currentIndex);

            newIndex++;


        }

        //overwrite
        this.indexToFrequencyDistributiony = newIndexToFrequencyDistributiony;



    }

    public void calculateGlobalTermWeights() {

        /*

            Balanced distributional concentration

            btc(t) = 1 - BH(t) / log(|C|)

            =

            1 + sum_i_to_|C|: [p(t|Ci)/sum_i_to_|C|:p(t|Ci)]*log(same) / norm constant

            p(t|Ci) =  f(t,Ci)/f(Ci)



           from Entropy-based Term Weighting schemes for text categorization in SVM (btc)
         */



        this.indexToGlobalWeight = HashIntDoubleMaps.getDefaultFactory().withDefaultValue(0).newMutableMap();

        int nrCategories = categoryToNmberOTerms.size();

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


            for(int i=0; i<p_of_t_given_c.size(); i++) {


                btc +=    (p_of_t_given_c.get(i) / sum_of_t_given_c) * Math.log(  (p_of_t_given_c.get(i) / sum_of_t_given_c)       );
            }


            this.indexToGlobalWeight.put(index,  1 +  (btc/Math.log(nrCategories)) );
        }


System.out.println("Global term weights calculated for: " + this.indexToGlobalWeight.size() + " terms");

    }


    public void sanityCheck() {

        System.out.println("Sum of terms based on f(t,C_i) should be equal sum of terms based on f(C_i) ");

        int sumOne = 0;
        int sumOne_v2 = 0;
        int sumTwo = 0;

        for(ObjIntCursor<String> cur = this.termToIndex.cursor(); cur.moveNext();) {


            int index = cur.value();

            CategoryDistributionForATerm dist = this.indexToFrequencyDistributiony.get(index);

            sumOne += dist.getTotalFrequency();


            for(IntIntCursor cur2 = dist.categoryToFrequency.cursor(); cur2.moveNext(); ) {

                sumOne_v2 += cur2.value();
            }

        }

        for(IntIntCursor cur =  this.categoryToNmberOTerms.cursor(); cur.moveNext();) {

            sumTwo += cur.value();

        }

        System.out.println("sum 1: " + sumOne + " sum 2 (v2): " + sumOne_v2 + "sumTwo: " + sumTwo );

    }

    public int getIndex(String term) {

        return this.termToIndex.getInt(term);
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


        ConcurrentMap<Integer, Integer> categoryToNumberOfDocsDB = dbPersist.hashMap("categoryToNumberOfDocs")
                .counterEnable()
                .keySerializer(   Serializer.INTEGER  )
                .valueSerializer( Serializer.INTEGER )
                .layout(8,24,4)  //Maximal Hash Table Size is calculated as: segment * node size ^ level count: here: 2654208
                .create(); //or open, this just overwrites



        for(ObjIntCursor<String> cur = this.termToIndex.cursor(); cur.moveNext(); ) {

            termToIndexDB.put(cur.key(), cur.value());

        }


        for(IntDoubleCursor cur = this.indexToGlobalWeight.cursor(); cur.moveNext(); ) {

            indexToGlobalWeightDB.put(cur.key(), cur.value());

        }



        for(IntIntCursor cur = this.categoryToNmberOfDocs.cursor(); cur.moveNext();) {


            categoryToNumberOfDocsDB.put(cur.key(),cur.value());


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


        ConcurrentMap<Integer, Integer> categoryToNumberOfDocsDB = dbPersist.hashMap("categoryToNumberOfDocs")
                .counterEnable()
                .keySerializer(   Serializer.INTEGER  )
                .valueSerializer( Serializer.INTEGER )
                .layout(8,24,4)  //Maximal Hash Table Size is calculated as: segment * node size ^ level count: here: 2654208
                .createOrOpen();

        this.termToIndex = HashObjIntMaps.getDefaultFactory().withDefaultValue(-1).withNullKeyAllowed(false).<String>newMutableMap(10000);
        this.indexToGlobalWeight = HashIntDoubleMaps.getDefaultFactory().withDefaultValue(0).newMutableMap();


        for (Map.Entry<Integer, Double> entry : indexToGlobalWeightDB.entrySet()) {

            this.indexToGlobalWeight.put(entry.getKey().intValue(), entry.getValue().doubleValue() );

        }


        for(Map.Entry<String,Integer> entry : termToIndexDB.entrySet()) {

            this.termToIndex.put(entry.getKey(),entry.getValue().intValue());

        }

        for(Map.Entry<Integer,Integer> entry : categoryToNumberOfDocsDB.entrySet()) {

            this.categoryToNmberOfDocs.put(entry.getKey().intValue(),entry.getValue().intValue());

        }


        dbPersist.close();
        System.out.println("Read " + this.termToIndex.size()+ " term mappings and " + indexToGlobalWeight.size() + " global weights " + this.categoryToNmberOfDocs.size() +" categories" );
    }


    public VecHsvPair trainingDocToVecHsvPair(Record record) {


        if (this.language.equals("swe")) {

            if (!record.isFullSwedishText()) return null;
        }

        if (this.language.equals("eng")) {

            if (!record.isFullEnglishText()) return null;

        }

        HashSet<Integer> classes = new HashSet<>();

        if (level == 3) {

            if (!record.containsLevel3Classification()) return null;

            Set<Integer> classificationCodes = record.getClassificationCodes();

            for(Integer code : classificationCodes) {

                Integer level3Code = HsvCodeToName.firstThreeDigitsOrNull(code);
                if(level3Code != null) classes.add(level3Code);
            }

        }


        if (level == 5) {

            if (!record.containsLevel5Classification()) return null;

            Set<Integer> classificationCodes = record.getClassificationCodes();

            for(Integer code : classificationCodes) {

                Integer level5Code = HsvCodeToName.firstFiveDigitsOrNull(code);
                if(level5Code != null) classes.add(level5Code);
            }

        }


        //select class

        int hsv_code = -99;

        if(classes.size() > 1) {

            //reduce to one
            int intMinSize = Integer.MAX_VALUE;
            for(Integer i : classes ) {

                int size = this.categoryToNmberOfDocs.get(i.intValue());

                if(size < intMinSize) {hsv_code = i; intMinSize = size;}
            }


        } else {hsv_code = classes.iterator().next(); }


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

        SparseVector sparseVector = new SparseVector(this.termToIndex.size(), terms.size()/2 ); //length and initial capacity

        for (String t : terms) {

          int index = getIndex(t);
            if(index != -1) sparseVector.increment( index, 1d  );
        }


        if( sparseVector.nnz() == 0) return null;

        return new VecHsvPair(sparseVector,hsv_code);
    }


    public SparseVector getVecForUnSeenRecord(Record record) {

        //call this method from correct IndexAndGloabalTermWeights object!

        List<String> terms;

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

        SparseVector sparseVector = new SparseVector(this.termToIndex.size(), terms.size()/2 ); //length and initial capacity

        for (String t : terms) {

            int index = getIndex(t);
            if(index != -1) sparseVector.increment( index, 1d  );
        }


        if( sparseVector.nnz() == 0) return null;

        applyGlobalTermWeights(sparseVector);

        return sparseVector;
    }



    public void applyGlobalTermWeights(SparseVector vec) {

        Iterator<IndexValue> iter = vec.getNonZeroIterator();

        //todo maybe apply regularization from paper "blancing between over-weighting and under-weighting in supervised term weighting".

        while(iter.hasNext()) {

            IndexValue indexValue = iter.next();
            vec.set( indexValue.getIndex(), indexValue.getValue()* this.indexToGlobalWeight.get( indexValue.getIndex() )  );

        }

    }


    public String printSparseVector(SparseVector vec) {

        StringBuilder stringbuilder = new StringBuilder();

        Iterator<IndexValue> iter = vec.getNonZeroIterator();
        stringbuilder.append("[");
        while(iter.hasNext()) {

            IndexValue indexValue = iter.next();
           stringbuilder.append(indexValue.getIndex()).append(",");
            stringbuilder.append(indexValue.getValue()).append(" ");

        }
        stringbuilder.append("]");
         return stringbuilder.toString();
    }

}
