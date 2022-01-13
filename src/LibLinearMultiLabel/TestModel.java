package LibLinearMultiLabel;

import mulan.classifier.MultiLabelOutput;
import mulan.evaluation.GroundTruth;
import mulan.evaluation.measure.ExampleBasedPrecision;
import mulan.evaluation.measure.ExampleBasedRecall;

import java.util.IntSummaryStatistics;
import java.util.List;

public class TestModel {


    public static void main(String[] arg) {

        String language = "swe";
        int level = 3;
        boolean useTermWeights = true;

        //load model
        System.out.println("Loading model..");
        OneVsAllLibLinear classifier = OneVsAllLibLinear.load("E:\\SWEPUB_JSON_20210805\\multilabel_"+language+"_"+level+".ser");

        //load training or test data
        System.out.println("Loading training data");

        List<TrainingPair> trainingPairList =  TrainingPair.load("E:\\SWEPUB_JSON_20210805\\trainingPairs_"+language+"_"+level+".ser");

        SimpleIndex simpleIndex = new SimpleIndex();
        if(useTermWeights) {

            System.out.println("TermWeighting..");

            simpleIndex.load("E:\\SWEPUB_JSON_20210805\\simpleIndex_" +language+"_"+level+".ser");
            simpleIndex.loadTermWeightsOptional("E:\\SWEPUB_JSON_20210805\\termWeights_"+language+"_"+level+".ser");

            for(int i=0; i<trainingPairList.size(); i++) simpleIndex.applyTermWeights(trainingPairList.get(i));

        }


        //L2-normalized if it was at training  time
        System.out.println("L2-normalize..");
        for(TrainingPair trainingPair : trainingPairList) trainingPair.L2normalize();


        List<Integer> classLabelsInOrder = classifier.getOrderedClassLabels();


            System.out.println("Classifying everything..");

            ExampleBasedPrecision exampleBasedPrecision = new ExampleBasedPrecision();
            ExampleBasedRecall exampleBasedRecall = new ExampleBasedRecall();

            IntSummaryStatistics meanLablesGroundTruth = new IntSummaryStatistics();
            IntSummaryStatistics meanLablesPredicted = new IntSummaryStatistics();

            double cut = 0.5;
            System.out.println(classLabelsInOrder);

            int max = 0;
            int nr_zero = 0;

            for (TrainingPair trainingPair : trainingPairList) {

                double[] probability = classifier.predict(trainingPair);
                GroundTruth groundTruth = Eval.getGroundTruth(trainingPair, classLabelsInOrder);
                MultiLabelOutput multiLabelOutput = Eval.multiLabelOutput(probability, cut);

                List<Eval.Prediction> predictions = Eval.PredictedClassesWithProb(probability,0.5,classLabelsInOrder);

                if(predictions.size() == 0) nr_zero++;
                if(predictions.size() >= 10) {

                    System.out.println(trainingPair.getUri() + " " +predictions);

                    max++;
                }


                meanLablesGroundTruth.accept(Eval.numberOfClassesPredictedOrAssigned(groundTruth));
                meanLablesPredicted.accept(Eval.numberOfClassesPredictedOrAssigned(multiLabelOutput));

                exampleBasedRecall.update(multiLabelOutput, groundTruth);
                exampleBasedPrecision.update(multiLabelOutput, groundTruth);


            }

            System.out.println("multi-label Recall: " + exampleBasedRecall.getValue() + " C=" + classifier.getC() +  " minorityClassWeight=" + classifier.getWeightForMinorityClass() +  " prob. cutoff: " + cut);
            System.out.println("multi-label Precision: " + exampleBasedPrecision.getValue() + " C=" + classifier.getC() +  " minorityClassWeight=" + classifier.getWeightForMinorityClass() +" prob. cutoff: " + cut);

            System.out.println("Mean number of ground truth labels: " + meanLablesGroundTruth.getAverage());
            System.out.println("Mean number of predicted labels: " + meanLablesPredicted.getAverage());

            System.out.print("");
            System.out.println("Zero suggestions: " + nr_zero + " out of " + trainingPairList.size());
            System.out.println("Max classes for one instance have 10 or more classes: " + max);



    }
}
