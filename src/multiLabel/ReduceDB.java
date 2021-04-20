package multiLabel;

import Database.FileHashDB;
import SwePub.HsvCodeToName;
import SwePub.Record;
import com.koloboke.collect.map.IntIntCursor;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ReduceDB {

    String language;
    int level;
    FileHashDB fileHashDB;

    public ReduceDB(String lang, int level) {

        this.language = lang;
        this.level = level;
        fileHashDB = new FileHashDB();
        fileHashDB.createOrOpenDatabase();
    }


    public void potentialTrainingPosts() {

        FileHashDB reducedDB = new FileHashDB();
        reducedDB.setPathToFile("Records." + this.language +"." +this.level +".db");
        reducedDB.create();

        /*

        Must have title and abstract to be a training exemplar
         */

        int total = 0;
        int reduced = 0;
        int numberOfLabels = 0;

        IntIntMap categoryToNmberOfDocs = HashIntIntMaps.getDefaultFactory().withDefaultValue(0).newMutableMap();


        for (Map.Entry<String, Record> entry : fileHashDB.database.entrySet()) {

            total++;
            Record record =  entry.getValue();

            if (this.language.equals("swe")) {

                if (!record.isFullSwedishText()) continue;
            }

            if (this.language.equals("eng")) {

                if (!record.isFullEnglishText()) continue;
            }


            HashSet<Integer> classes = new HashSet<>();

            if (this.level == 3) {

                if (!record.containsLevel3Classification()) continue;

                Set<Integer> classificationCodes = record.getClassificationCodes();

                for(Integer code : classificationCodes) {

                    Integer level3Code = HsvCodeToName.firstThreeDigitsOrNull(code);
                    if(level3Code != null) classes.add(level3Code);
                }


                numberOfLabels += classes.size();
            }

            if (this.level == 5) {

                if (!record.containsLevel5Classification()) continue;

                Set<Integer> classificationCodes = record.getClassificationCodes();

                for(Integer code : classificationCodes) {

                    Integer level5Code = HsvCodeToName.firstFiveDigitsOrNull(code);
                    if(level5Code != null) classes.add(level5Code);
                }

                numberOfLabels += classes.size();
            }

            for(Integer i : classes) {


                categoryToNmberOfDocs.addValue(i,1);

            }


            reduced++;



            reducedDB.put(record.getURI(),record);

        }

        reducedDB.closeDatabase();

        System.out.println("total: " + total + " reduced: " + reduced);
        System.out.println("Label cardinality: " + numberOfLabels/(float)reduced);
        fileHashDB.closeDatabase();

        int minCat = Integer.MAX_VALUE;
        int maxCat = Integer.MIN_VALUE;
        for(IntIntCursor cur = categoryToNmberOfDocs.cursor(); cur.moveNext();) {

            int category = cur.key();
            int nr_doc = cur.value();

            if(nr_doc > maxCat) maxCat = nr_doc;
            if(nr_doc < minCat) minCat = nr_doc;

            System.out.println(category +"\t" + nr_doc +"\t" + HsvCodeToName.getCategoryInfo(category).getEng_description());

        }

        System.out.println();
        System.out.println("Total # categories: " + categoryToNmberOfDocs.size() + " smallest category= " + minCat + " largest category=" +maxCat);

    }


}
