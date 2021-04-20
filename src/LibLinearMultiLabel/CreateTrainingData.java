package LibLinearMultiLabel;

import Database.FileHashDB;
import SwePub.HsvCodeToName;
import SwePub.Record;
import java.io.IOException;
import java.util.*;

public class CreateTrainingData {



    public static void main(String[] arg) throws IOException {


        SimpleIndex simpleIndexEngLevel5 = new SimpleIndex();
        simpleIndexEngLevel5.load("E:\\Desktop\\JSON_SWEPUB\\multiLabelExperiment\\simpleIndexEngLevel5.ser");

        FileHashDB fileHashDB = new FileHashDB();
        fileHashDB.setPathToFile("E:\\Desktop\\JSON_SWEPUB\\SWEPUB20210411.db");
        fileHashDB.createOrOpenDatabase();


        List<TrainingPair> trainingPairList = new ArrayList<>();


        System.out.println("Creating training data..");

        for (Map.Entry<String, Record> r : fileHashDB.database.entrySet()) {

            Record record = r.getValue();

            //everything below is conditional on this
            if( !(record.containsLevel5Classification() && record.isFullEnglishText() ) ) continue;

            Set<Integer> classLabels = record.getClassificationCodes();
            Set<Integer> finalClassLabels = new HashSet<>();

            for(Integer label : classLabels) {

              Integer levelFiveLabel = HsvCodeToName.firstFiveDigitsOrNull(label);
              if(levelFiveLabel != null) finalClassLabels.add(levelFiveLabel);

            }

            //we know this is a valid training record based on classLabel level and language so lets proceed

            List<String> terms = record.getLanguageSpecificTerms("eng");
            terms.addAll( record.getTermsFromAffiliation() );
            terms.addAll( record.getTermsFromHost()  );
            terms.addAll( record.getUnkontrolledKkeywords() );
            String ISBN = record.getISBN();
            if(ISBN != null) terms.add( ISBN  );
            terms.addAll( record.getIssn());


            TrainingPair trainingPair = simpleIndexEngLevel5.getTrainingPair(terms,finalClassLabels,record.getURI());
            trainingPairList.add(trainingPair);
            //System.out.println(trainingPair);

        }


        System.out.println("Serializing..");

        TrainingPair.save(trainingPairList, "E:\\Desktop\\JSON_SWEPUB\\multiLabelExperiment\\TrainingPairsEngLevel5.ser");


        fileHashDB.closeDatabase();





    }


}
