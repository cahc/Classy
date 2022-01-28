package LibLinearMultiLabel;

import Database.FileHashDB;
import Database.JsonSwePubParser;
import SwePub.HsvCodeToName;
import SwePub.Record;
import java.io.IOException;
import java.util.*;

public class CreateTrainingData {



    public static void main(String[] arg) throws IOException, InterruptedException {


        //FIRST PARSE SWEPUB

        /*
        FileHashDB fileHashDBSwePub = new FileHashDB();
        //fileHashDB.setPathToFile("E:\\Desktop\\JSON_SWEPUB\\SWEPUB20210421.db");
        fileHashDBSwePub.setPathToFile("E:\\SWEPUB\\swepub20220105.db");
        fileHashDBSwePub.create();

        JsonSwePubParser jsonSwePubParser = new JsonSwePubParser("E:\\SWEPUB\\swepub-deduplicated.jsonl");
        jsonSwePubParser.parse(fileHashDBSwePub);

        fileHashDBSwePub.closeDatabase();
        */




        //which model
        String language = "eng";
        int level = 5;
        boolean addAffilFeatures = false;

        System.out.println("Setting up for a " + language + "  model on level " + language + " usingAffil=" + addAffilFeatures);

        /*

        STEP ONE CREATE INDEX


         */
        SimpleIndex simpleIndex = new SimpleIndex();

        FileHashDB fileHashDB = new FileHashDB();
        fileHashDB.setPathToFile("E:\\SWEPUB\\swepub20220105.db");
        fileHashDB.createOrOpenDatabase();


        for (Map.Entry<String, Record> r : fileHashDB.database.entrySet()) {

            Record rec = r.getValue();
            if(rec.isAutoClassedBySwepub()) continue;

            simpleIndex.addRecord(r.getValue(),level,language,addAffilFeatures);

        }



         /*


            REMOVE RARE TERMS

        */


        simpleIndex.removeRareTerms(3);


        /*

        Serialize index

         */
        System.out.println("Serializing index.. # " + simpleIndex.size());
        simpleIndex.save("E:\\SWEPUB\\simpleIndex_" +language+"_"+level+".ser");


        /*

        CREATE TRAINING PAIRS HERE


         */

        System.out.println("Creating Training Pairs..");

        List<TrainingPair> trainingPairList = new ArrayList<>();

        for (Map.Entry<String, Record> r : fileHashDB.database.entrySet()) {

            Record record = r.getValue();
            if (record.isAutoClassedBySwepub()) continue;

            //conditional on language and level

            if(level == 3 && !record.containsLevel3Classification()) continue;
            if(level == 5 && !record.containsLevel5Classification()) continue;


            if( "eng".equals(language) && !record.isFullEnglishText() )  continue;
            if( "swe".equals(language) && !record.isFullSwedishText() ) continue;




            Set<Integer> classLabels = record.getClassificationCodes();
            Set<Integer> finalClassLabels = new HashSet<>();

            for(Integer label : classLabels) {

                if(level == 5) {

                    Integer levelFiveLabel = HsvCodeToName.firstFiveDigitsOrNull(label);
                    if (levelFiveLabel != null) finalClassLabels.add(levelFiveLabel);

                } else if(level == 3) {

                    Integer levelThreeLabel = HsvCodeToName.firstThreeDigitsOrNull(label);
                    if (levelThreeLabel != null) finalClassLabels.add(levelThreeLabel);
                }


            }

            List<String> terms = null;
            if("eng".equals(language) ) terms = record.getLanguageSpecificTerms("eng");
            if("swe".equals(language) ) terms = record.getLanguageSpecificTerms("swe");

            if(addAffilFeatures) terms.addAll( record.getTermsFromAffiliation() );
            terms.addAll( record.getTermsFromHost()  );
            terms.addAll( record.getUnkontrolledKkeywords() );
            String ISBN = record.getISBN();
            if(ISBN != null) terms.add( ISBN  );
            terms.addAll( record.getIssn());

            TrainingPair trainingPair = simpleIndex.getTrainingPair(terms,finalClassLabels,record.getMasterURI(), simpleIndex.size() );
            trainingPairList.add(trainingPair);

        }


        /*

        Serialize training pair

         */

        System.out.println("Serializing training pairs.. # " + trainingPairList.size());
        TrainingPair.save(trainingPairList, "E:\\SWEPUB\\trainingPairs_"+language+"_"+level+".ser");


        /*

        Weight entropy

         */

        System.out.println("Creating TermWeights..");

        simpleIndex.addEntropyWeighting(trainingPairList);
        System.out.println("Serializing weighting...");
        simpleIndex.saveWeightingSchemeOptional("E:\\SWEPUB\\termWeights_"+language+"_"+level+".ser");



        //done, (1) index (2) training pairs and (3) term weights


        fileHashDB.closeDatabase();


    }

}



