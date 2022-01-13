package SwePub;

import Database.*;
import WebApp.ClassProbPair;
import jsat.classifiers.CategoricalResults;
import jsat.classifiers.Classifier;
import jsat.classifiers.DataPoint;
import jsat.linear.Vec;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * Created by crco0001 on 10/11/2019.
 */
public class ClassySwePub {


    public static void main(String arg[]) throws IOException, XMLStreamException, MyOwnException, ClassNotFoundException {

        /*
        System.out.println("Parsing SwePubXML and saving to MapBD");

        long start = System.currentTimeMillis();

        FileHashDB fileHashDB = new FileHashDB();
        fileHashDB.create();
        SwePubParser swePubParser = new SwePubParser();
        swePubParser.parse("swepub_dump_dedup.xml",fileHashDB);
        System.out.println("Records parsed and saved: " + fileHashDB.size() );

        long stop = System.currentTimeMillis();

        System.out.println("Parsed and saved to db in " + (stop - start) / 1000.0 + "seconds");
        fileHashDB.closeDatabase();

        */

        FileHashDB fileHashDB = new FileHashDB();
        fileHashDB.createOrOpenDatabase();


        IndexAndGlobalTermWeights englishLevel5 = new IndexAndGlobalTermWeights("eng", 5);

        System.out.println("Read term index");
        englishLevel5.readFromMapDB(null);

        System.out.println("read classification model");
        Classifier classifierlevel5eng = TrainAndPredict.HelperFunctions.readSerializedClassifier("classifier.eng.5.ser");


        int count=0;
        System.out.println("Total records: " + fileHashDB.database.size());

        BufferedWriter writer = new BufferedWriter( new FileWriter(new File("test.txt")));

        for (Map.Entry<String, Record> entry : fileHashDB.database.entrySet()) {


                Record swepubRecord = entry.getValue();

                if(swepubRecord.isFullEnglishText() && swepubRecord.getPublishedYear().compareTo(2011) > 0) {

                    Vec vec = englishLevel5.getVecForUnSeenRecord(swepubRecord);
                    vec.normalize();

                    CategoricalResults result = classifierlevel5eng.classify( new DataPoint(vec) );
                    Vec probabilities = result.getVecView();

                    String uri = swepubRecord.getMasterURI();

                    ArrayList<ClassProbPair> classProbPairArrayList = new ArrayList<>();

                    for(int i=0; i<probabilities.length(); i++) {

                        ClassProbPair classProbPair = new ClassProbPair();
                        ClassificationCategory hsv = HsvCodeToName.getCategoryInfo(IndexAndGlobalTermWeights.level5ToCategoryCodes.inverse().get(i));
                        classProbPair.setClassCode(hsv.getCode());
                        classProbPair.setProbability( probabilities.get(i) );
                        classProbPairArrayList.add(classProbPair);
                    }


                    Collections.sort(classProbPairArrayList,Collections.reverseOrder());
                    writer.write(uri);
                    writer.write("\t" + swepubRecord.getPublishedYear());

                    for(int i=0; i<= 2; i++) {


                        writer.write("\t" + classProbPairArrayList.get(i).getClassCode() +"\t" + classProbPairArrayList.get(i).getProbability() );
                    }

                    writer.newLine();
                    count++;

                } else continue;

        }






        writer.flush();
        writer.close();
        System.out.println("Test collection size: " + count);
        fileHashDB.closeDatabase();
    }


}
