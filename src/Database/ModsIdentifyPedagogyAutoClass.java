package Database;

import LibLinearMultiLabel.Eval;
import LibLinearMultiLabel.OneVsAllLibLinear;
import LibLinearMultiLabel.SimpleIndex;
import LibLinearMultiLabel.TrainingPair;
import SwePub.Record;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ModsIdentifyPedagogyAutoClass {


    public static void main(String[] arg) throws IOException, XMLStreamException {

        //load models, index and termweights
        System.out.println("Loading model..");
        OneVsAllLibLinear classifierSwe = OneVsAllLibLinear.load("E:\\SWEPUB\\multilabel_"+"swe"+"_"+3+".ser");
        OneVsAllLibLinear classifierEng = OneVsAllLibLinear.load("E:\\SWEPUB\\multilabel_"+"eng"+"_"+5+".ser");

        List<Integer> SweTargetsLevel3 = classifierSwe.getOrderedClassLabels();
        List<Integer> EngTargetsLevel5 = classifierEng.getOrderedClassLabels();

        SimpleIndex simpleIndexSwe = new SimpleIndex();
        SimpleIndex simpleIndexEng = new SimpleIndex();

        System.out.println("Load indices and termWeights");

        simpleIndexSwe.load("E:\\SWEPUB\\simpleIndex_" +"swe"+"_"+3+".ser");
        simpleIndexSwe.loadTermWeightsOptional("E:\\SWEPUB\\termWeights_"+"swe"+"_"+3+".ser");

        simpleIndexEng.load("E:\\SWEPUB\\simpleIndex_" +"eng"+"_"+5+".ser");
        simpleIndexEng.loadTermWeightsOptional("E:\\SWEPUB\\termWeights_"+"eng"+"_"+5+".ser");


        //choose to classify parsed SwePub or parse DiVA XML on the fly
        //FileHashDB fileHashDB = new FileHashDB();
        //fileHashDB.setPathToFile("E:\\SWEPUB\\swepub20220105.db");
        //fileHashDB.createOrOpenDatabase();


        ModsDivaFileParser modsDivaFileParser = new ModsDivaFileParser();
        List<Record> recordList = modsDivaFileParser.parse( "E:\\2022\\SWEPUB_JSON_20210805_UTBVET\\TAKETWO\\diva_2010-2020.xml" );




        int unsupported = 0;
        int notClassified = 0;
        int withSoughtTarget = 0;
        int noAbstract = 0;


        BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( new File("E:\\2022\\SWEPUB_JSON_20210805_UTBVET\\TAKETWO\\AUTOCLASSIFICATION_2010-2020.txt")), StandardCharsets.UTF_8));

        //for (Map.Entry<String, Record> r : fileHashDB.database.entrySet()) {
        for(Record rec : recordList) {

            //Record rec = r.getValue();
            //this should seldom (never?) be the case i guess
            if(rec.getPublishedYear() == null) continue;

            //Between 2010 and 2020
            if(rec.getPublishedYear().compareTo(2010) < 0 || rec.getPublishedYear().compareTo(2020) > 0 ) continue;

            TrainingPair trainingPair = null;


            if( !(rec.isFullSwedishText() || rec.isFullEnglishText()) ) {

                noAbstract++;
                continue;

            }

            boolean baseClassificationOnEnglishModel = rec.isContainsEnglish();

            if(baseClassificationOnEnglishModel) {

                trainingPair = simpleIndexEng.getVectorForUnSeenRecord(rec,"eng",false);


            } else if(rec.isContainsSwedish()) {

                trainingPair = simpleIndexSwe.getVectorForUnSeenRecord(rec,"swe",false);

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
                predictions = Eval.PredictedClassesWithProb(prob,0.5,EngTargetsLevel5);



            } else if(rec.isContainsSwedish()) {

                simpleIndexSwe.applyTermWeights(trainingPair);
                trainingPair.L2normalize();
                double[] prob = classifierSwe.predict(trainingPair);
                predictions = Eval.PredictedClassesWithProb(prob,0.5,SweTargetsLevel3);

            }

            if(predictions.size() == 0) notClassified++;

            //
            //Check if it contains: 503 Utbildningsvetenskap
            //
            //it must be among top 3 and have probability > 0.51

            boolean isEducation = false;
            int toCheck = Math.min(3,predictions.size());
            for(int i=0; i<toCheck; i++) {

                if(baseClassificationOnEnglishModel) {

                    if( (predictions.get(i).classNr.equals(50301) || predictions.get(i).classNr.equals(50302) || predictions.get(i).classNr.equals(50303) || predictions.get(i).classNr.equals(50304) ) && predictions.get(i).prob >=0.51) {isEducation = true; break; }

                } else {

                    if(predictions.get(i).classNr.equals(503) && predictions.get(i).prob >=0.51) {isEducation = true; break; }

                }

            }

            if(isEducation) {
                writer.write(rec.getDiva2Id() +"\t" + rec.getMasterURI() + "\t" + predictions +"\t" + (baseClassificationOnEnglishModel ? "English" : "Swedish") +"\t" + rec.getAffiliationUris().contains("umu.se") +"\t" + rec.getSecondaryUris() +"\t" + rec.getUmUUri() +"\t" + rec.getTitle());
                writer.newLine();
                withSoughtTarget++;
            }


        } //for each record



        // fileHashDB.closeDatabase();
        writer.flush();
        writer.close();
        System.out.println("Number non classifyed based on language: " + unsupported );
        System.out.println("Number ignored for lacking abstract " + noAbstract);
        System.out.println("Number of instances with no predictions: " + notClassified);
        System.out.println("With sought target (503) : " + withSoughtTarget);





    }



}
