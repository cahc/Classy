package Diva;

import Database.IndexAndGlobalTermWeights;
import Database.ModsDivaFileParser;
import Database.MyOwnException;
import SwePub.ClassificationCategory;
import SwePub.HsvCodeToName;
import SwePub.Record;
import WebApp.ClassProbPair;
import jsat.classifiers.CategoricalResults;
import jsat.classifiers.Classifier;
import jsat.classifiers.DataPoint;
import jsat.linear.Vec;

import javax.xml.stream.XMLStreamException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by Cristian on 2017-06-19.
 */
public class ClassyDiVA {



    public static void main(String[] arg) throws IOException, XMLStreamException, MyOwnException, ClassNotFoundException {


        //TODO include number in output

        if(arg.length != 1) {System.out.println("Supply mods input file.."); System.exit(0); }



        ModsDivaFileParser modsDivaFileParser = new ModsDivaFileParser();
        List<Record> recordList = modsDivaFileParser.parse(arg[0]);

        BufferedWriter writer = new BufferedWriter( new FileWriter( new File("ClassificationResult.txt") ));

        System.out.println("Parsed: " + recordList.size());


        IndexAndGlobalTermWeights englishLevel5 = new IndexAndGlobalTermWeights("eng", 5);

        IndexAndGlobalTermWeights swedishLevel3 = new IndexAndGlobalTermWeights("swe", 3);

        englishLevel5.readFromMapDB();

        swedishLevel3.readFromMapDB();


        Classifier classifierlevel5eng = TrainAndPredict.HelperFunctions.readSerializedClassifier("classifier.eng.5.ser");
        Classifier classifierLevel3swe = TrainAndPredict.HelperFunctions.readSerializedClassifier("classifier.swe.3.ser");


        System.out.println("Now Classifying");


        int classed = 0;
        int notClassed = 0;

        for(Record r : recordList) {

            HashMap<Integer,Double> level2ToProb = new HashMap<>();
            HashMap<Integer,Double> level1ToProb = new HashMap<>();
            HashMap<Integer,Double> level3ToProb = new HashMap<>();

            if(r.isContainsEnglish()) {

                //use english level 5
                Vec vec = englishLevel5.getVecForUnSeenRecord(r);

                if(vec != null) {


                    vec.normalize();
                    int nnz = vec.nnz();
                    //builder.append( this.englishLevel5.printSparseVector(vec)  );
                    // builder.append("nnz:" + nnz);

                    CategoricalResults result = classifierlevel5eng.classify( new DataPoint(vec) );

                        //  int hsv = result.mostLikely();
                        //   double prob = result.getProb(hsv);

                   // ClassificationCategory true_hsv =  HsvCodeToName.getCategoryInfo( IndexAndGlobalTermWeights.level5ToCategoryCodes.inverse().get(hsv)    );

                    //builder.append("<p>");
                    //builder.append("UKÄ/SCB: <b>" + true_hsv.getCode() + "</b> : " + true_hsv.getEng_description().replaceAll("-->","&rarr;") +  " (probability: " + df.format(prob) +")");
                    //builder.append("</p>");


                    Vec probabilities = result.getVecView();

                    for(int i=0; i<probabilities.length(); i++) {

                        ClassificationCategory hsv =  HsvCodeToName.getCategoryInfo( IndexAndGlobalTermWeights.level5ToCategoryCodes.inverse().get(i)    );

                        double prob = probabilities.get(i);

                        Integer level1Code = HsvCodeToName.firstOneDigitOrNull( hsv.getCode()  );
                        Integer level2Code = HsvCodeToName.firstThreeDigitsOrNull(  hsv.getCode() );
                        Integer level3Code = HsvCodeToName.firstFiveDigitsOrNull( hsv.getCode() );

                        Double problevel1 = level1ToProb.get(level1Code);

                        if(problevel1 != null) {  level1ToProb.put(level1Code, problevel1+prob ); } else { level1ToProb.put(level1Code,prob); }

                        Double problevel2 = level2ToProb.get(level2Code);

                        if(problevel2 != null) { level2ToProb.put(level2Code, problevel2+ prob); } else {level2ToProb.put(level2Code, prob); }


                        //dosent make any sense..
                        Double problevel3 = level3ToProb.get(level3Code);
                        if(problevel3 != null) { level3ToProb.put(level3Code, problevel3+ prob); } else {level3ToProb.put(level3Code, prob); }


                    }




                }


            } else

            if(r.isContainsSwedish()) {

                Vec vec = swedishLevel3.getVecForUnSeenRecord(r);

                if(vec != null) {

                    vec.normalize();
                    int nnz = vec.nnz();

                    CategoricalResults result = classifierLevel3swe.classify( new DataPoint(vec) );
                    Vec probabilities = result.getVecView();


                    for(int i=0; i<probabilities.length(); i++) {

                        ClassificationCategory hsv =  HsvCodeToName.getCategoryInfo( IndexAndGlobalTermWeights.level3ToCategoryCodes.inverse().get(i)    );

                        double prob = probabilities.get(i);
                        Integer level2Code = HsvCodeToName.firstThreeDigitsOrNull(  hsv.getCode() );
                        Integer level1Code = HsvCodeToName.firstOneDigitOrNull( hsv.getCode()  );

                        Double problevel1 = level1ToProb.get(level1Code);

                        if(problevel1 != null) {  level1ToProb.put(level1Code, problevel1+prob ); } else { level1ToProb.put(level1Code,prob); }

                        Double problevel2 = level2ToProb.get(level2Code);

                        if(problevel2 != null) { level2ToProb.put(level2Code, problevel2+ prob); } else {level2ToProb.put(level2Code, prob); }


                    }



                }


        }

        List<ClassProbPair> classProbPairsLevel1 = new ArrayList<>(5);


            for(Map.Entry<Integer,Double> entry : level1ToProb.entrySet() ) {

                Integer code = entry.getKey();
                Double prob = entry.getValue();

                classProbPairsLevel1.add(  new ClassProbPair( code, prob )  );

            }

            Collections.sort(classProbPairsLevel1, Comparator.reverseOrder());


            List<ClassProbPair> classProbPairsLevel2 = new ArrayList<>(5);


        for(Map.Entry<Integer,Double> entry : level2ToProb.entrySet() ) {

                Integer code = entry.getKey();
                Double prob = entry.getValue();

                classProbPairsLevel2.add(  new ClassProbPair( code, prob )  );


            }

            Collections.sort(classProbPairsLevel2, Comparator.reverseOrder());



            List<ClassProbPair> classProbPairsLevel3 = new ArrayList<>(5);


            for(Map.Entry<Integer,Double> entry : level3ToProb.entrySet() ) {

                Integer code = entry.getKey();
                Double prob = entry.getValue();

                classProbPairsLevel3.add(  new ClassProbPair( code, prob )  );


            }

            Collections.sort(classProbPairsLevel3, Comparator.reverseOrder());



            if(classProbPairsLevel1.size() > 0) {

                ClassificationCategory bestGuessLevel1 = HsvCodeToName.getCategoryInfo(classProbPairsLevel1.get(0).getClassCode());
                Double confidence1 = classProbPairsLevel1.get(0).getProbability();


                ClassificationCategory bestGuessLevel2 = HsvCodeToName.getCategoryInfo(classProbPairsLevel2.get(0).getClassCode());
                Double confidence2 = classProbPairsLevel2.get(0).getProbability();

                if(r.isContainsEnglish()) {

                    ClassificationCategory bestGuessLevel3 = HsvCodeToName.getCategoryInfo(classProbPairsLevel3.get(0).getClassCode());
                    Double confidence3 = classProbPairsLevel3.get(0).getProbability();


                    //level 1, 2, 3
                    writer.write(r.getURI() + "\t" + "eng_based" +"\t" + bestGuessLevel1.getEng_description() + "\t" + confidence1 + "\t" + bestGuessLevel2.getEng_description() + "\t" + confidence2 +"\t"  +bestGuessLevel3.getEng_description() + "\t" + confidence3);


                } else {

                    //level 1,2,2!
                    writer.write(r.getURI() + "\t" + "swe_based" +"\t" + bestGuessLevel1.getEng_description() + "\t" + confidence1 + "\t" + bestGuessLevel2.getEng_description() + "\t" + confidence2 +"\t" +bestGuessLevel2.getEng_description() + "\t" + confidence2);
                }
                writer.newLine();

                classed++;
            } else {



                writer.write(r.getURI()   + "\t" + "NOT CLASSIFIED" + "\t" + 0.0 + "\t" + "NOT CLASSIFIED" + "\t"  + 0.0  ); ;
                writer.newLine();
                notClassed++;

            }






        } // for each record


        writer.flush();
        writer.close();
        System.out.println(classed + " classified records and " + notClassed + " records not classified");


    }


}
