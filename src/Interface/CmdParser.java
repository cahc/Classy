package Interface;
import Database.*;
import SwePub.ClassificationCategory;
import SwePub.HsvCodeToName;
import SwePub.Record;
import TrainAndPredict.HelperFunctions;
import TrainAndPredict.Splitter;
import TrainAndPredict.VecHsvPair;
import com.google.common.collect.BiMap;
import com.google.common.collect.MapDifference;
import jsat.classifiers.*;
import jsat.classifiers.linear.*;
import jsat.distributions.Distribution;
import jsat.distributions.LogUniform;
import jsat.linear.Vec;
import jsat.math.decayrates.PowerDecay;
import org.apache.commons.cli.*;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.*;

/**
 * Created by Cristian on 2016-12-05.
 */
public class CmdParser {

    public static void printHelpAndExit(Options options) {

        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("Missing arguments:", options);
        System.exit(0);
    }
    Options options;
    CommandLine cmd;
    public CmdParser(String[] arg) throws ParseException {


        options = new Options();
        options.addOption("XMLtoMapDB", false, "parse SwePub XML and save to MapDB");
        options.addOption("MapDBToText", false, "read MapDB with records and print to ASCII");
        options.addOption("CreateIndex",false,"create index and save to mapDB");

        Option language = Option.builder("language").hasArg().optionalArg(true).argName("swe or eng").desc("model for which language").build();
        options.addOption(language);

        Option level = Option.builder("level").hasArg().optionalArg(true).argName("3 or 5").desc("model for specified hsv level").build();
        options.addOption(level);
        options.addOption("CreateTrainingAndHoldOut",false,"read index from mapDB and create sparse vectors");
        options.addOption("Train", false,"train and serialize model");

        Option C = Option.builder("C").hasArg().optionalArg(true).argName("int").desc("regularization parameter [0,100]").build();
        options.addOption(C);

        options.addOption("OneVsOneFindC",false,"search for optimal C");


        Option help = new Option("help","print this information");
        options.addOption(help);

        CommandLineParser parser = new DefaultParser();
        this.cmd = parser.parse(options,arg);

        if(arg.length == 0) {

            printHelpAndExit(options);
        }

    }


    public boolean hasOption(String opt) {

       return this.cmd.hasOption(opt);
    }

    public static void main(String[] arg) throws ParseException, IOException, XMLStreamException, MyOwnException, ClassNotFoundException {


        CmdParser parser = new CmdParser(arg);

        if( parser.hasOption("XMLtoMapDB") ) {

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

            System.exit(0);

        }

        if(parser.hasOption("MapDBToText")) {
                System.out.println("Writing Records from MapDB to textFile");
                FileHashDB fileHashDB = new FileHashDB();
                fileHashDB.createOrOpenDatabase();

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(new File("RecordOutput.txt")), "UTF-8") );
                System.out.println("Starting to read and write..");
                long start = System.currentTimeMillis();
                int docs = 0;

                for (Map.Entry<Integer, Record> entry : fileHashDB.database.entrySet()) {

                    docs++;
                    writer.write( entry.getValue().toString() ); writer.newLine();

                }

                long stop = System.currentTimeMillis();
                writer.flush();
                writer.close();
                System.out.println("Read " + docs + "in: " + (stop - start) / 1000.0 + "seconds");
                fileHashDB.closeDatabase();

                System.exit(0);

            }


        if(parser.hasOption("CreateIndex")) {


            if( !(parser.hasOption("language") && parser.hasOption("level"))  ) {

                System.out.println("must supply both language (swe or eng) and level (3 or 5)");
                System.exit(0);
            }


            String lang = parser.cmd.getOptionValue("language");
            int level = Integer.valueOf(parser.cmd.getOptionValue("level"));


            FileHashDB fileHashDB = new FileHashDB();
            fileHashDB.createOrOpenDatabase();

            System.out.println("Setting up");
            IndexAndGlobalTermWeights indexAndGlobalTermWeights = new IndexAndGlobalTermWeights(lang, level);
            indexAndGlobalTermWeights.setUpClassDistribution(fileHashDB);

            System.out.println("now building index..");

            int counter = 0;
            int total = 0;
            for (Map.Entry<Integer, Record> entry : fileHashDB.database.entrySet()) {

                total++;

                if( indexAndGlobalTermWeights.addTermsToIndex(entry.getValue()) ) counter++;




            }

            System.out.println("done");
            System.out.println("Added terms from " + counter + " records out of a total of " + total + " records" );
            fileHashDB.closeDatabase();

            System.out.println("term mappings: " + indexAndGlobalTermWeights.getNrTerms() );

            indexAndGlobalTermWeights.sanityCheck();

            System.out.println("printing stat to file..");

            indexAndGlobalTermWeights.printStat("takeOne." +lang + "." + level +".txt");

            System.out.println("reducing..");

            indexAndGlobalTermWeights.removeRareTerms(3);

            System.out.println("new mappinga: " + indexAndGlobalTermWeights.getNrTerms() );

            System.out.println("printing stat to file..");

            indexAndGlobalTermWeights.printStat("takeTwo." + lang + "." + level +".txt");

            indexAndGlobalTermWeights.sanityCheck();

            indexAndGlobalTermWeights.calculateGlobalTermWeights();

            indexAndGlobalTermWeights.printTermAndGlobalWeights("TermWeights." + lang + "." + level +".txt");

            indexAndGlobalTermWeights.writeToMapDB();

        }




        if(parser.hasOption("CreateTrainingAndHoldOut")) { // writing theee datasets to disk: training, holdout and full! Use full for creating a classifier

            if( !(parser.hasOption("language") && parser.hasOption("level"))  ) {

                System.out.println("must supply both language (swe or eng) and level (3 or 5)");
                System.exit(0);
            }


            String lang = parser.cmd.getOptionValue("language");
            int level = Integer.valueOf(parser.cmd.getOptionValue("level"));



            System.out.println("Reading index from termIndex.db");
            IndexAndGlobalTermWeights indexAndGlobalTermWeights = new IndexAndGlobalTermWeights(lang, level);
            indexAndGlobalTermWeights.readFromMapDB(null);

            FileHashDB fileHashDB = new FileHashDB();
            fileHashDB.createOrOpenDatabase();




            List<VecHsvPair> trainingVectors = new ArrayList<>();


            BufferedWriter writerTrain = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("RecordsUsedForTraining." +indexAndGlobalTermWeights.getLang() + "." + indexAndGlobalTermWeights.getLevel() +".txt" )), "UTF-8"));
            BufferedWriter writerHouldOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("RecordsUsedForValidation." + indexAndGlobalTermWeights.getLang() +"." +indexAndGlobalTermWeights.getLevel() +".txt")), "UTF-8"));

            HashMap<Integer,Integer> remappedIndex = new HashMap<Integer,Integer>();


            int counterUsed = 0;


            for (Map.Entry<Integer, Record> entry : fileHashDB.database.entrySet()) {

                Record r = entry.getValue();
                int mapdbKey = r.getMapDBKey();

                VecHsvPair vecHsvPair = indexAndGlobalTermWeights.trainingDocToVecHsvPair(r);

                //if(sparseVector == null ) {System.out.println("no vector created..");}

                if (vecHsvPair != null) {

                    //System.out.println(vecHsvPair.getVector().nnz() +" size:" + vecHsvPair.getVector().length());
                    //System.out.println("befor: " + indexAndGlobalTermWeights.printSparseVector(vecHsvPair.getVector()));
                    indexAndGlobalTermWeights.applyGlobalTermWeights(vecHsvPair.getVector());
                    //System.out.println("after: " + indexAndGlobalTermWeights.printSparseVector(vecHsvPair.getVector()));
                    trainingVectors.add(vecHsvPair);


                    remappedIndex.put(counterUsed, mapdbKey);
                    counterUsed++;

                }

            }


            BiMap<Integer, Integer> categoryCodeMapper = null;
            CategoricalData categoryInformation = null;

            if (indexAndGlobalTermWeights.getLevel() == 3) {

                categoryCodeMapper = IndexAndGlobalTermWeights.level3ToCategoryCodes;
                categoryInformation = IndexAndGlobalTermWeights.level3CategoryInformation;

            } else if (indexAndGlobalTermWeights.getLevel() == 5) {

                categoryCodeMapper = IndexAndGlobalTermWeights.level5ToCategoryCodes;
                categoryInformation = IndexAndGlobalTermWeights.level5CategoryInformation;

            } else {

                System.out.println("Unknown hsv-level, aborting!");
                System.exit(0);
            }


            System.out.println(trainingVectors.size() + " vectors created");



            ClassificationDataSet classificationDataSet = new ClassificationDataSet(trainingVectors.get(0).getVector().length(), new CategoricalData[]{}, categoryInformation);


            for (int i = 0; i < trainingVectors.size(); i++) {

                VecHsvPair vecHsvPair = trainingVectors.get(i);



                        classificationDataSet.addDataPoint(vecHsvPair.getVector(), categoryCodeMapper.get(vecHsvPair.getHsvCode()));


            }


            System.out.println("Writing Training And hold-out data to disk");

            Splitter splitter = new Splitter(classificationDataSet,0.07);

            List<ClassificationDataSet> trainingAndHoldOut = splitter.splitInTrainingAndHoldOut();

            System.out.println("Writing full data to disk, # " + classificationDataSet.getSampleSize());
            HelperFunctions.writeJSATdata(classificationDataSet, "FullDataSet." +indexAndGlobalTermWeights.getLang() +"." + indexAndGlobalTermWeights.getLevel() +".bin");

            System.out.println("Training: " + trainingAndHoldOut.get(0).getSampleSize() + " holdout: " + trainingAndHoldOut.get(1).getSampleSize());
            System.out.println("Saving to disk..");
            HelperFunctions.writeJSATdata(trainingAndHoldOut.get(0), "TrainSet." + indexAndGlobalTermWeights.getLang() +"." + indexAndGlobalTermWeights.getLevel() +".bin");
            HelperFunctions.writeJSATdata(trainingAndHoldOut.get(1), "HoldOutSet." +indexAndGlobalTermWeights.getLang() +"." + indexAndGlobalTermWeights.getLevel() +".bin");

            System.out.println("saving records in text files..");

            HashSet<Integer> holdOutIndices = splitter.getHoldOutIndices();

            for(int i=0; i< classificationDataSet.getSampleSize(); i++) {

               Record r = fileHashDB.database.get( remappedIndex.get(i) );


                if(holdOutIndices.contains(i)) { writerHouldOut.write( r.toString() ); writerHouldOut.write("\n"); } else {

                    writerTrain.write( r.toString() ); writerTrain.write("\n");
                }

            }


            writerTrain.flush();
            writerHouldOut.flush();
            writerTrain.close();
            writerHouldOut.close();
            fileHashDB.closeDatabase();

        }



        if(parser.hasOption("OneVsOneFindC")) {

            //////////////////////READ IN DATA//////////////////////////////

            if (!(parser.hasOption("language") && parser.hasOption("level"))) {

                System.out.println("must supply both language (swe or eng) and level (3 or 5)");
                System.exit(0);
            }

            String lang = parser.cmd.getOptionValue("language");
            int level = Integer.valueOf(parser.cmd.getOptionValue("level"));

            ClassificationDataSet cds = TrainAndPredict.HelperFunctions.readJSATdata("TrainSet." + lang + "." + level + ".bin");
            ClassificationDataSet holdoutCds = TrainAndPredict.HelperFunctions.readJSATdata("HoldOutSet." + lang + "." + level + ".bin");


            System.out.println("L2-normalize vectors");
            for (Vec v : cds.getDataVectors()) {
                v.normalize();
            }

            for (Vec v : holdoutCds.getDataVectors()) {
                v.normalize();
            }



            ///////////////////////////////////////////////////////


            double[] gridSearch = new double[12];

            gridSearch[0] = Math.pow(2,0.5);
            gridSearch[1] = Math.pow(2, 1);
            gridSearch[2] = Math.pow(2, 1.5);
            gridSearch[3] = Math.pow(2, 2);
            gridSearch[4] = Math.pow(2, 2.5);
            gridSearch[5] = Math.pow(2, 3);
            gridSearch[6] = Math.pow(2, 3.5);
            gridSearch[7] = 10;
            gridSearch[8] = Math.pow(2,4);
            gridSearch[9] = Math.pow(2,4.5);
            gridSearch[10] = Math.pow(2,5);
            gridSearch[11] = 1;

            for (int j = 0; j < gridSearch.length; j++) {

                LogisticRegressionDCD baseClassifier = new LogisticRegressionDCD();

                baseClassifier.setUseBias(true);
                baseClassifier.setC( gridSearch[j] ); //todo crossvalidate larger values reduce the amount of regularization, and smaller values increase the regularization
                baseClassifier.setMaxIterations(250); //todo check convergance

                OneVSAll metaClassifyer = new OneVSAll(baseClassifier, true);

                long start = System.currentTimeMillis();
                metaClassifyer.trainC(cds);
                double timeRunned =  (System.currentTimeMillis() - start) / 1000.0;

                int errorsTrain = 0;
                for (int i = 0; i < cds.getSampleSize(); i++) {

                    DataPoint dataPoint = cds.getDataPoint(i);//It is important not to mix these up, the class has been removed from data points in 'cDataSet'
                    int truth = cds.getDataPointCategory(i);//We can grab the true category from the data set
                    CategoricalResults predictionResults = metaClassifyer.classify(dataPoint);
                    int predicted = predictionResults.mostLikely();
                    if (truth != predicted) errorsTrain++;


                }

                int errorsHoldOut = 0;
                for (int i = 0; i < holdoutCds.getSampleSize(); i++) {

                    DataPoint dataPoint = holdoutCds.getDataPoint(i);//It is important not to mix these up, the class has been removed from data points in 'cDataSet'
                    int truth = holdoutCds.getDataPointCategory(i);//We can grab the true category from the data set
                    CategoricalResults predictionResults = metaClassifyer.classify(dataPoint);
                    int predicted = predictionResults.mostLikely();
                    if (truth != predicted) errorsHoldOut++;

                }



                System.out.println("C="+gridSearch[j] +"\t" +"TrainingTime=" +timeRunned +"\t" +"TrainingError=" + (100.0 * errorsTrain / cds.getSampleSize()) +"\t" +"HoldOutError=" + (100.0 * errorsHoldOut / holdoutCds.getSampleSize()) );


            }

        }


        if(parser.hasOption("Train")) {

            if( !(parser.hasOption("language") && parser.hasOption("level"))  ) {

                System.out.println("must supply both language (swe or eng) and level (3 or 5)");
                System.exit(0);
            }


            String lang = parser.cmd.getOptionValue("language");
            int level = Integer.valueOf(parser.cmd.getOptionValue("level"));

            int reg =0;
            if(parser.hasOption("C")) {


                reg = Integer.valueOf( parser.cmd.getOptionValue("C") );
            } else {System.out.println("Supply regularization term -C"); System.exit(0); }


            ClassificationDataSet cds = TrainAndPredict.HelperFunctions.readJSATdata("FullDataSet." + lang + "." + level +".bin");

            System.out.println("L2-normalize vectors");
            for (Vec v : cds.getDataVectors()) {
                v.normalize();
            }


            LogisticRegressionDCD baseClassifier = new LogisticRegressionDCD();
            baseClassifier.setUseBias(true);
            baseClassifier.setC( reg ); //todo crossvalidate larger values reduce the amount of regularization, and smaller values increase the regularization
            baseClassifier.setMaxIterations(250); //todo check convergance

            OneVSAll metaClassifyer = new OneVSAll(baseClassifier, true);



            long start = System.currentTimeMillis();
            System.out.println("Training with " +  metaClassifyer.getClass().getCanonicalName() + " using " + cds.getSampleSize() + " samples");
            System.out.println("Using regularization parameter C=" + reg);
            metaClassifyer.trainC(cds);
            System.out.println("Training took: " + (System.currentTimeMillis() - start)/1000.0 + " seconds" );

            System.out.println("Serializing..");


            TrainAndPredict.HelperFunctions.writeSerializedClassifier(metaClassifyer,"classifier." + lang +"." +level +".ser");

            System.out.println("Training error:");
            int errors = 0;

            for(int i = 0; i < cds.getSampleSize(); i++) {

                DataPoint dataPoint = cds.getDataPoint(i);//It is important not to mix these up, the class has been removed from data points in 'cDataSet'
                int truth = cds.getDataPointCategory(i);//We can grab the true category from the data set
                //Categorical Results contains the probability estimates for each possible target class value.
                //Classifiers that do not support probability estimates will mark its prediction with total confidence.
                CategoricalResults predictionResults = metaClassifyer.classify(dataPoint);
                int predicted = predictionResults.mostLikely();
                if(truth != predicted) errors++;



            }

            System.out.println(errors + " errors were made, " + 100.0*errors/cds.getSampleSize() + "% error rate" );


        }





    }

}
