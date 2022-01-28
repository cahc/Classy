package LibLinearMultiLabel;

import mulan.classifier.MultiLabelOutput;
import mulan.evaluation.GroundTruth;
import mulan.evaluation.measure.ExampleBasedPrecision;
import mulan.evaluation.measure.ExampleBasedRecall;
import mulan.evaluation.measure.MacroFMeasure;
import mulan.evaluation.measure.MicroFMeasure;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ParameterSearchOnTest {

    public static class Ev {

        double C;
        double minorityWeight;

        boolean onTestSet;
        String evalMetric; //precision recall etc
        double value; // value of the evalMetric


        public Ev(double C, double minorityWeight, boolean onTestSet, String evalMetric, double value) {

            this.C = C;
            this.minorityWeight = minorityWeight;
            this.onTestSet = onTestSet;
            this.evalMetric = evalMetric;
            this.value = value;
        }


        @Override
        public String toString() {
            return C + "\t" + minorityWeight + "\t" + onTestSet + "\t" + evalMetric + "\t" + value;
        }


    }


    public static List<List<TrainingPair>> split(List<TrainingPair> trainingPairList, List<Integer> sortedClassLabels, double fractionPerClassKeptForTesting ) {

        if(fractionPerClassKeptForTesting > 0.5 || fractionPerClassKeptForTesting < 0.01) {System.out.println("Unreasonable fraction specified!"); System.exit(0); }

        System.out.println("Number of classes: " + sortedClassLabels.size());

          Map<Integer,List<TrainingPair>> classLabelToObservationTrainingPairs = new TreeMap<>(); //TreeMap if we want to keep the order of keys



        for(TrainingPair trainingPair : trainingPairList) {

            Set<Integer> classLabels = trainingPair.getClassLabels();

                for(Integer label : classLabels) {

                    List<TrainingPair> observationPairList = classLabelToObservationTrainingPairs.get(label);
                    if(observationPairList == null) {

                        observationPairList = new ArrayList<>(50);
                        observationPairList.add(trainingPair);
                        classLabelToObservationTrainingPairs.put(label,observationPairList);

                    } else {

                        observationPairList.add(trainingPair);

                    }



                }



        }



        //split
        //TODO om split endast om klassen är tillräckligt stor, t.ex. ceiling(size*0.1) >= 5


        Set<TrainingPair> train = new HashSet<>();
        Set<TrainingPair> test = new HashSet<>();


        for(Map.Entry<Integer,List<TrainingPair>> entry : classLabelToObservationTrainingPairs.entrySet()) {

            List<TrainingPair> trainingPairsForLabel = entry.getValue();

            int n = trainingPairsForLabel.size();
            int nrToHoldOut = (int)Math.ceil(n*fractionPerClassKeptForTesting);
            int nrToKeep = n-nrToHoldOut;

            Collections.shuffle( trainingPairsForLabel );

            for(int i=0; i <nrToHoldOut; i++) {

                test.add(  trainingPairsForLabel.get(i)  );

            }

            for(int i=nrToHoldOut; i<trainingPairsForLabel.size(); i++) {

                train.add(  trainingPairsForLabel.get(i)  );
            }


            System.out.println(entry.getKey() +" " +entry.getValue().size() + " train_n:" +nrToKeep + " test_n:" +nrToHoldOut + (nrToHoldOut<5 ? " too small hold out!" : " ok hold out size!") ) ;

        }


        List<List<TrainingPair>> trainAndTest = new ArrayList<>();
        trainAndTest.add( new ArrayList<>(train) );
        trainAndTest.add( new ArrayList<>(test));

        return trainAndTest;
    }



    public static void main(String[] arg) throws IOException {

        //model
        String language = "eng";
        int level = 5;
        boolean useTermWeights = true;


        BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( new File("E:\\SWEPUB\\parameterSearch_"+language+"_"+level+".txt")), StandardCharsets.UTF_8));

        //load training data
        List<TrainingPair> trainingPairList =  TrainingPair.load("E:\\SWEPUB\\trainingPairs_"+language+"_"+level+".ser");

        int dim = trainingPairList.get(0).dimensions;

        SimpleIndex simpleIndex = new SimpleIndex();

        if(useTermWeights) {

            System.out.println("Term weighting..");

            simpleIndex.load("E:\\SWEPUB\\simpleIndex_" +language+"_"+level+".ser");
            simpleIndex.loadTermWeightsOptional("E:\\SWEPUB\\termWeights_"+language+"_"+level+".ser");


            for(int i=0; i<trainingPairList.size(); i++) simpleIndex.applyTermWeights(trainingPairList.get(i));


        }

        //L2-normalize data
        System.out.println("L2-normalize");
        for(TrainingPair trainingPair : trainingPairList) trainingPair.L2normalize();

        System.out.println("Dataset n: " + trainingPairList.size());

        //identify all classLabels
        Set<Integer> classLabels = new HashSet<>();
        for(TrainingPair trainingPair : trainingPairList) classLabels.addAll( trainingPair.getClassLabels() );
        List<Integer> sortedClassLabels = new ArrayList<>(classLabels);
        Collections.sort(sortedClassLabels);

        //split in train and test
        List<List<TrainingPair>> trainAndTestSet = split(trainingPairList,sortedClassLabels,0.1);
        System.out.println("Train set: " + trainAndTestSet.get(0).size() + " Test set: " + trainAndTestSet.get(1).size() );

        //search parameters C= 2^2,3,4,5,6,7..
        double[] factors = new double[]{2,6};
        //minority weight 1,2,4,8..
        double[] minorityWeights = new double[]{5,10,15,20};


        List<Ev> evList = new ArrayList<>();

        for(double factor : factors ) {

            double C = Math.pow(2,factor);


                for(double minorityWeight : minorityWeights) {

                    OneVsAllLibLinear oneVsAllLibLinear = new OneVsAllLibLinear(trainAndTestSet.get(0), sortedClassLabels, trainAndTestSet.get(0).size(), dim );
                    oneVsAllLibLinear.train(C, minorityWeight);


                    ExampleBasedPrecision exampleBasedPrecision = new ExampleBasedPrecision();
                    ExampleBasedRecall exampleBasedRecall = new ExampleBasedRecall();



                    for (TrainingPair trainingPair : trainAndTestSet.get(0)) {

                        double[] probability = oneVsAllLibLinear.predict(trainingPair);
                        GroundTruth groundTruth = Eval.getGroundTruth(trainingPair, sortedClassLabels);
                        MultiLabelOutput multiLabelOutput = Eval.multiLabelOutput(probability, 0.5);

                        exampleBasedRecall.update(multiLabelOutput, groundTruth);
                        exampleBasedPrecision.update(multiLabelOutput, groundTruth);



                    }


                    evList.add( new Ev(C, minorityWeight, false, "recall", exampleBasedRecall.getValue() ) );
                    evList.add( new Ev(C, minorityWeight, false, "precision", exampleBasedPrecision.getValue() ) );




                    System.out.println("Training error multi-label Recall: " + exampleBasedRecall.getValue() + " C=" + oneVsAllLibLinear.getC() + " minorityWeighting=" + oneVsAllLibLinear.getWeightForMinorityClass());
                    System.out.println("Training error multi-label Precision: " + exampleBasedPrecision.getValue() + " C=" + oneVsAllLibLinear.getC() + " minorityWeighting=" + oneVsAllLibLinear.getWeightForMinorityClass());


                    System.out.println("Now on test set:");


                    exampleBasedPrecision = new ExampleBasedPrecision();
                    exampleBasedRecall = new ExampleBasedRecall();


                    for (TrainingPair trainingPair : trainAndTestSet.get(1)) {

                        double[] probability = oneVsAllLibLinear.predict(trainingPair);
                        GroundTruth groundTruth = Eval.getGroundTruth(trainingPair, sortedClassLabels);
                        MultiLabelOutput multiLabelOutput = Eval.multiLabelOutput(probability, 0.5);


                        exampleBasedRecall.update(multiLabelOutput, groundTruth);
                        exampleBasedPrecision.update(multiLabelOutput, groundTruth);


                    }

                    evList.add( new Ev(C, minorityWeight, true, "recall", exampleBasedRecall.getValue() ) );
                    evList.add( new Ev(C, minorityWeight, true, "precision", exampleBasedPrecision.getValue() ) );



                    System.out.println("test multi-label Recall: " + exampleBasedRecall.getValue() + " C=" + oneVsAllLibLinear.getC() + " minorityWeighting=" + oneVsAllLibLinear.getWeightForMinorityClass());
                    System.out.println("test multi-label Precision: " + exampleBasedPrecision.getValue() + " C=" + oneVsAllLibLinear.getC() + " minorityWeighting=" + oneVsAllLibLinear.getWeightForMinorityClass());


                } //inner loop for minority weight search

        } //search for C over



        for(Ev ev : evList) {

            System.out.println(ev);
            writer.write(ev.toString());
            writer.newLine();
        }


        writer.flush();

    }



}
