package TrainAndPredict;

import Database.FileHashDB;
import Database.IndexAndGlobalTermWeights;
import Database.MyOwnException;
import SwePub.Record;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import jsat.classifiers.CategoricalData;
import jsat.classifiers.ClassificationModelEvaluation;
import jsat.classifiers.linear.PassiveAggressive;
import jsat.distributions.Distribution;
import jsat.distributions.LogUniform;
import jsat.linear.IndexValue;
import jsat.linear.SparseVector;
import misc.Stemmers.EnglishStemmer;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Cristian on 2017-04-05.
 */
public class SetupClassifiers {


    public static void main(String[] arg) throws MyOwnException, IOException {

        String[] languages = {"eng","swe"};
        int[] levels = {3,5};


        for(int i=0; i<languages.length; i++) {

                    for(int j=0; j < levels.length; j++) {

                        FileHashDB fileHashDB = new FileHashDB();
                        fileHashDB.createOrOpenDatabase();

                        System.out.println("Setting up");
                        IndexAndGlobalTermWeights indexAndGlobalTermWeights = new IndexAndGlobalTermWeights(languages[i], levels[j]);
                        indexAndGlobalTermWeights.setUpClassDistribution(fileHashDB);

                        System.out.println("now building index..");

                        int counter = 0;
                        int total = 0;
                        for (Map.Entry<Integer, Record> entry : fileHashDB.database.entrySet()) {

                            total++;

                            if (indexAndGlobalTermWeights.addTermsToIndex(entry.getValue())) counter++;


                        }

                        System.out.println("done");
                        System.out.println("Added terms from " + counter + " records out of a total of " + total + " records");
                        fileHashDB.closeDatabase();

                        System.out.println("term mappings: " + indexAndGlobalTermWeights.getNrTerms());

                        indexAndGlobalTermWeights.sanityCheck();

                        System.out.println("printing stat to file..");

                        indexAndGlobalTermWeights.printStat("takeOne." + languages[i] +"." + levels[j] + ".txt");

                        System.out.println("reducing..");

                        indexAndGlobalTermWeights.removeRareTerms(3);

                        System.out.println("new mappinga: " + indexAndGlobalTermWeights.getNrTerms());

                        System.out.println("printing stat to file..");

                        indexAndGlobalTermWeights.printStat("takeTwo." + languages[i] +"." + levels[j] + ".txt");

                        indexAndGlobalTermWeights.sanityCheck();

                        indexAndGlobalTermWeights.calculateGlobalTermWeights();

                        indexAndGlobalTermWeights.printTermAndGlobalWeights("TermWeights." + languages[i] +"." + levels[j] + ".txt");

                        indexAndGlobalTermWeights.writeToMapDB();


                    }
        }

    }

}
