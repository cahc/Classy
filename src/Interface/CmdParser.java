package Interface;
import Database.*;
import LibLinear.CV;
import SwePub.HsvCodeToName;
import SwePub.Record;
import TrainAndPredict.HelperFunctions;
import TrainAndPredict.Splitter;
import TrainAndPredict.VecHsvPair;
import com.google.common.collect.BiMap;
import de.bwaldvogel.liblinear.*;
import jsat.classifiers.*;
import jsat.classifiers.linear.*;
import jsat.linear.Vec;
import multiLabel.EntropyTermWeights;
import multiLabel.ReduceDB;
import org.apache.commons.cli.*;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

import static Database.SimpleSerializer.deserializeLabelList;

/**
 * Created by Cristian on 2016-12-05.
 */
public class CmdParser {

    public static void printHelpAndExit(Options options) {

        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("Missing arguments:", options);
        System.exit(0);
    }


    public static double calculateWeightForTrueClass(double[] groundTruth) {

        double x = 0;

        for(int i=0; i<groundTruth.length; i++) if(groundTruth[i]==1) x++;

        return(groundTruth.length/ (x*2));

    }

    public static double getRateBinary(Model model, Feature[][] vectors, double[] groundTruth) {


        double correct=0;
        double R = 0;
        int predictedClass = 0;

        for(int i=0; i< vectors.length; i++) {

            double label = Linear.predict(model,vectors[i]);
            if(groundTruth[i] == 1) predictedClass++;

            if(label == groundTruth[i]) correct++;

            if(label == 1 && groundTruth[i]==1) R++;

        }

        System.out.println("n=" +vectors.length + " correct=" +correct + " predicted class size=" + predictedClass +" R:" +R/predictedClass );

        return correct/vectors.length;

    }

    public static double getRateBinaryPostClassified(int predicted[], int[] groundTruth) {


        double correct=0;
        double R = 0;
        int predictedClass = 0;

        for(int i=0; i< predicted.length; i++) {

            double label = predicted[i];

            if(groundTruth[i] == 1) predictedClass++;

            if(label == groundTruth[i]) correct++;

            if(label == 1 && groundTruth[i]==1) R++;

        }

        System.out.println("n=" +predicted.length + " correct=" +correct + " predicted class size=" + predictedClass +" R:" +R/predictedClass );

        return correct/predicted.length;

    }






    Options options;
    CommandLine cmd;
    public CmdParser(String[] arg) throws ParseException {


        options = new Options();
        options.addOption("XMLtoMapDB", false, "parse SwePub XML and save to MapDB");
        options.addOption("JsonToMapDB",false,"parse json and save to MapDb");

        options.addOption("MapDBToText", false, "read MapDB with records and print to ASCII");
        options.addOption("CreateIndex",false,"create index and save to mapDB");
        options.addOption("IdentifyTrainingRecords",false,"create index and save to mapDB");
        options.addOption("MultiLabelTermWeights",false,"Calculate entropy term weights");
        options.addOption("CreateMultiLabelTrainingData",false,"create vectors and lable info");
        options.addOption("trainMultiLabel",false,"train multi label model");

        Option language = Option.builder("language").hasArg().optionalArg(true).argName("swe or eng").desc("model for which language").build();
        options.addOption(language);

        Option level = Option.builder("level").hasArg().optionalArg(true).argName("3 or 5").desc("model for specified hsv level").build();
        options.addOption(level);
        options.addOption("CreateTrainingAndHoldOut",false,"read index from mapDB and create sparse vectors");
        options.addOption("Train", false,"train and serialize model");

        Option target = Option.builder("target").hasArg().optionalArg(true).argName("0..41, 0..259").desc("true class in onevsall").build();
        options.addOption(target);

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

    public static void main(String[] arg) throws ParseException, IOException, XMLStreamException, MyOwnException, ClassNotFoundException, InterruptedException {


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


        if(parser.hasOption("JsonToMapDB")) {


            System.out.println("Parsing Json and saving to MapBD");

            long start = System.currentTimeMillis();

            FileHashDB fileHashDB = new FileHashDB();
            fileHashDB.create();

           JsonSwePubParser jsonSwePubParser = new JsonSwePubParser();
           jsonSwePubParser.parse(fileHashDB);

            System.out.println("Records parsed and saved: " + fileHashDB.size() );

            long stop = System.currentTimeMillis();

            System.out.println("Parsed and saved to db in " + (stop - start) / 1000.0 + "seconds");
            fileHashDB.closeDatabase();



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



            if(parser.hasOption("IdentifyTrainingRecords")) {

                if( !(parser.hasOption("language") && parser.hasOption("level"))  ) {

                    System.out.println("must supply both language (swe or eng) and level (3 or 5)");
                    System.exit(0);
                }


                ReduceDB reduceDB = new ReduceDB(parser.cmd.getOptionValue("language"),Integer.valueOf(parser.cmd.getOptionValue("level")));

                reduceDB.potentialTrainingPosts();



            }


            if(parser.hasOption("MultiLabelTermWeights")) {


                if( !(parser.hasOption("language") && parser.hasOption("level"))  ) {

                    System.out.println("must supply both language (swe or eng) and level (3 or 5)");
                    System.exit(0);
                }

                String lang = parser.cmd.getOptionValue("language");
                int level = Integer.valueOf(parser.cmd.getOptionValue("level"));


                FileHashDB fileHashDB = new FileHashDB();
                fileHashDB.createOrOpenDatabase();


                System.out.println("now building index..");

                int counter = 0;
                int total = 0;

                EntropyTermWeights entropyTermWeights = new EntropyTermWeights(lang,level);

                for (Map.Entry<Integer, Record> entry : fileHashDB.database.entrySet()) {

                    total++;

                    if( entropyTermWeights.indexRecord(entry.getValue()) ) counter++;



                }

                System.out.println("done");
                System.out.println("Added terms from " + counter + " records out of a total of " + total + " records" );
                fileHashDB.closeDatabase();

                entropyTermWeights.printStat("termsTemp" + lang +".before"); // term, index, docfreq, total freq (counting duplicates here, as there is multi-labels!)


                entropyTermWeights.indexSize();

                entropyTermWeights.reduceIndex(2);

                entropyTermWeights.indexSize(); // new index

                entropyTermWeights.printStat("termsTemp" + lang +".after"); // term, index, docfreq, total freq (counting duplicates here, as there is multi-labels!)


                //int index = entropyTermWeights.getIndex("TE@nanotechnology");
                //System.out.println("TE@nanotechnology==" + index);
                //System.out.println( entropyTermWeights.getCategoryDistFor(index).printCategoryDistributionForATerm() );

                entropyTermWeights.calculateEntropyWeights();
                entropyTermWeights.printEntropyWeights("TermWeights" +lang +".txt");
                entropyTermWeights.writeToMapDB();


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


        if(parser.hasOption("CreateMultiLabelTrainingData")){

            if( !(parser.hasOption("language") && parser.hasOption("level"))  ) {

                System.out.println("must supply both language (swe or eng) and level (3 or 5)");
                System.exit(0);
            }

            String lang = parser.cmd.getOptionValue("language");
            int level = Integer.valueOf(parser.cmd.getOptionValue("level"));

            EntropyTermWeights entropyTermWeights = new EntropyTermWeights(lang, level );
            entropyTermWeights.readFromMapDB(null);

            FileHashDB records = new FileHashDB();
            records.setPathToFile("Records." + lang +"." + level +".db");
            records.createOrOpenDatabase();
            System.out.println("data set: " + records.size());


            ArrayList< ArrayList<FeatureNode> > featureVectors = new ArrayList<>( records.size() );
            ArrayList< HashSet<Integer> > labelInfo = new ArrayList<>( records.size()  );

            for (Map.Entry<Integer, Record> entry : records.database.entrySet()) {

                Record record = entry.getValue();

                HashSet<Integer> codes = record.getClassificationCodes();
                HashSet<Integer> hsv = new HashSet<>(2);

                if(level == 3) {

                    for(Integer code : codes) {

                        Integer level3Code = HsvCodeToName.firstThreeDigitsOrNull(code);

                        //hsv code to 0....42
                        if(level3Code != null) hsv.add(     IndexAndGlobalTermWeights.level3ToCategoryCodes.get(level3Code) );
                    }

                }

                if(level == 5) {

                    for(Integer code : codes) {

                        Integer level5Code = HsvCodeToName.firstFiveDigitsOrNull(code);
                        if(level5Code != null) hsv.add( IndexAndGlobalTermWeights.level5ToCategoryCodes.get(level5Code));
                    }

                }


                ArrayList<FeatureNode> featureNodes = entropyTermWeights.getFeatureNodeList( record );
                entropyTermWeights.applyWeighting(featureNodes); //todo add l2 or l1 normalization perhaps?
                featureVectors.add(featureNodes);
                labelInfo.add( hsv );



            }


            SerializeToMVStorage serializeToMVStorage = new SerializeToMVStorage("DB." + lang +"." +level +".bin");

            for(int i=0; i<featureVectors.size(); i++) {



                serializeToMVStorage.saveInstance(i,featureVectors.get(i),labelInfo.get(i));

            }

            serializeToMVStorage.forceCommit();
            serializeToMVStorage.close();

            //SimpleSerializer.serialiseList(featureVectors,"featureNodeLists." + lang + "." + level + ".ser");
            //SimpleSerializer.serialiseList(labelInfo, "labelList." + lang + "." + level +".ser");


            records.closeDatabase();
        }



        if(parser.hasOption("trainMultiLabel")) {

            if( !(parser.hasOption("language") && parser.hasOption("level"))  ) {

                System.out.println("must supply both language (swe or eng) and level (3 or 5)");
                System.exit(0);
            }

            int true_class = 0;
            if(parser.hasOption("target")) {


                true_class = Integer.valueOf(parser.cmd.getOptionValue("target"));
            }

            String lang = parser.cmd.getOptionValue("language");
            int level = Integer.valueOf(parser.cmd.getOptionValue("level"));

            System.out.println("Deserializing..");


            SerializeToMVStorage serializeToMVStorage = new SerializeToMVStorage("DB." + lang +"." +level +".bin");

            int N = serializeToMVStorage.dbSize();

            FeatureNode[][] x = new FeatureNode[N][];
            double[] y = new double[  N ]; //this will depend on one-vs-all run
            List<Set<Integer>> labels = new ArrayList<>();



            for(int i=0; i<N; i++) {


               x[i] = serializeToMVStorage.retrieveInstanceFeatures(i);
               labels.add( serializeToMVStorage.retrieveInstanceLabels(i)  );
            }

            serializeToMVStorage.close();

            System.out.println("building problem");

            Problem problem = new Problem();
            problem.bias = 1.0;
            problem.l = N; // number of training examples
            problem.n = x[0][ x[0].length-1 ].index; // number of features + 1 for bias

            System.out.println(N + "instances");
            System.out.println(problem.n +" features (including bias)");

            System.out.println("training with class=" + true_class);

            for(int i=0; i<N; i++) {

                if(labels.get(i).contains(true_class)) {

                    y[i] = 1;

                } else {

                    y[i] = 0;

                }


            }
            problem.x = x;
            problem.y = y;

            SolverType solver = SolverType.L2R_LR; // -s 0

            //Large Value of parameter C => small margin (small regularization)
            //Small Value of paramerter C => Large margin (large regularization)

            double C = 1.0;    // cost of constraints violation
            double eps = 0.0025; // stopping criteria
            Parameter parameter = new Parameter(solver, C, eps);
            //parameter.setMaxIters(15);

            double[] weights = new double[2];

            //double weightTrueClass = calculateWeightForTrueClass(problem.y);

            weights[0] = 5;
            weights[1] = 1;
            int[] lables = new int[2];
            lables[0] = 1;
            lables[1] = 0;
            parameter.setWeights(weights,lables);
            System.out.println("Using weights: " + Arrays.toString(weights));


            System.out.println("training..");

            Model model = Linear.train(problem, parameter);


            //File modelFile = new File("model");
            //model.save(modelFile);
            // load model or use it directly
            // model = Model.load(modelFile);




            double rate = getRateBinary(model,problem.x,problem.y);
            System.out.println("final rate: " + rate);

            int[] predicted = new int[problem.l];

            for(int i=0; i< problem.l; i++) {

                double label = Linear.predict(model,problem.x[i]);

                predicted[i] = (int)label;

            }

            int[] groundtruth  = new int[problem.l];
            for(int i=0; i< groundtruth.length; i++) groundtruth[i] = (int)problem.y[i];


            double prec = meka.core.Metrics.P_Precision(groundtruth,predicted);
            double rec = meka.core.Metrics.P_Recall(groundtruth,predicted);
            System.out.println("P: " + prec);
            System.out.println("R: " + rec );
            System.out.println("F1: "  + (2*prec*rec)/(rec+prec) );

            System.out.println( "jaccard: " + meka.core.Metrics.P_Accuracy(groundtruth,predicted) );


            System.out.println("Random subsample..");

            double averageF1 = CV.randomHoldOut(problem,parameter,0.05,3);

            System.out.println("random sample F1: " + averageF1);


            /*

                 //probability estimate
                 double[] probability = new double[model.getNrClass()];
                 double lable = Linear.predictProbability(model,problem.x[374], probability );
                 System.out.println(lable);
                 System.out.println(Arrays.toString(probability));

             */


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
                metaClassifyer.train(cds);
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
            metaClassifyer.train(cds);
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
