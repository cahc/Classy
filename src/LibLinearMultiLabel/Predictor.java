package LibLinearMultiLabel;


import Database.FileHashDB;
import SwePub.Record;
import com.koloboke.collect.map.ObjIntCursor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class Predictor {


    public static void main(String[] arg) throws IOException {


        //load models, index and termweights
        System.out.println("Loading model..");
        OneVsAllLibLinear classifierSwe = OneVsAllLibLinear.load("E:\\SWEPUB_JSON_20210805\\multilabel_"+"swe"+"_"+3+".ser");
        OneVsAllLibLinear classifierEng = OneVsAllLibLinear.load("E:\\SWEPUB_JSON_20210805\\multilabel_"+"eng"+"_"+3+".ser");

        List<Integer> targetsLevel3 = classifierEng.getOrderedClassLabels();


        SimpleIndex simpleIndexSwe = new SimpleIndex();
        SimpleIndex simpleIndexEng = new SimpleIndex();

        System.out.println("Load indices and termWeights");

        simpleIndexSwe.load("E:\\SWEPUB_JSON_20210805\\simpleIndex_" +"swe"+"_"+3+".ser");
        simpleIndexSwe.loadTermWeightsOptional("E:\\SWEPUB_JSON_20210805\\termWeights_"+"swe"+"_"+3+".ser");

        simpleIndexEng.load("E:\\SWEPUB_JSON_20210805\\simpleIndex_" +"eng"+"_"+3+".ser");
        simpleIndexEng.loadTermWeightsOptional("E:\\SWEPUB_JSON_20210805\\termWeights_"+"eng"+"_"+3+".ser");

        FileHashDB fileHashDB = new FileHashDB();
        fileHashDB.setPathToFile("E:\\SWEPUB_JSON_20210805\\adHocSwePub.db");
        fileHashDB.createOrOpenDatabase();


        int unsupported = 0;
        int notClassified = 0;
        int withSoughtTarget = 0;


        BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( new File("E:\\SWEPUB_JSON_20210805\\AutoClassEducation2010-2020.txt")), StandardCharsets.UTF_8));


        for (Map.Entry<String, Record> r : fileHashDB.database.entrySet()) {

            Record rec = r.getValue();
            //this should seldom (never?) be the case i guess
            if(rec.getPublishedYear() == null) continue;

            //Between 2010 and 2020
            if(rec.getPublishedYear().compareTo(2010) < 0 || rec.getPublishedYear().compareTo(2020) > 0 ) continue;

            TrainingPair trainingPair = null;


            boolean baseClassificationOnEnglishModel = rec.isContainsEnglish();

            if(baseClassificationOnEnglishModel) {

               trainingPair = simpleIndexEng.getVectorForUnSeenRecord(rec,"eng");


            } else if(rec.isContainsSwedish()) {

                trainingPair = simpleIndexSwe.getVectorForUnSeenRecord(rec,"swe");

            } else {

                //unsupported language
                unsupported++;
                continue;
            } //trainingPair is null if not supported language so continue to next record


            List<Eval.Prediction> predictions = null;
            if(baseClassificationOnEnglishModel) {

                simpleIndexEng.applyTermWeights(trainingPair);
                trainingPair.L2normalize();
                double[] prob = classifierEng.predict(trainingPair);
                predictions = Eval.PredictedClassesWithProb(prob,0.5,targetsLevel3);



            } else if(rec.isContainsSwedish()) {

                simpleIndexSwe.applyTermWeights(trainingPair);
                trainingPair.L2normalize();
                double[] prob = classifierSwe.predict(trainingPair);
                predictions = Eval.PredictedClassesWithProb(prob,0.5,targetsLevel3);

            }

            if(predictions.size() == 0) notClassified++;

            //
            //Check if it contains: 503 Utbildningsvetenskap
            //
            //it must be among top 3 and have probability > 0.8

            boolean isEducation = false;
            int toCheck = Math.min(3,predictions.size());
            for(int i=0; i<toCheck; i++) {

                if(predictions.get(i).classNr.equals(503) && predictions.get(i).prob >=0.8) {isEducation = true; break; }

            }

           if(isEducation) {
               writer.write(rec.getMasterURI() + "\t" + predictions +"\t" + (baseClassificationOnEnglishModel ? "English" : "Swedish") +"\t" + rec.getAffiliationUris().contains("umu.se") +"\t" + rec.getSecondaryUris() +"\t" + rec.getUmUUri());
               writer.newLine();
               withSoughtTarget++;
           }


        } //for each record



        fileHashDB.closeDatabase();
        writer.flush();
        writer.close();
        System.out.println("Number non classifyed based on language: " + unsupported );
        System.out.println("Number of instances with no predictions: " + notClassified);
        System.out.println("With sought target (503) : " + withSoughtTarget);
    }


}

