package LibLinearMultiLabel;

import mulan.classifier.MultiLabelOutput;
import mulan.evaluation.GroundTruth;
import mulan.evaluation.measure.ExampleBasedPrecision;
import mulan.evaluation.measure.ExampleBasedRecall;

import java.util.List;

public class Tester {


    public static void main(String[] arg) {


        //load training data
        //List<TrainingPair> trainingPairList =  TrainingPair.load("E:\\Desktop\\JSON_SWEPUB\\multiLabelExperiment\\TrainingPairsEngLevel5.ser");
        //L2-normalize data
        //System.out.println("L2-normalize");
        //for(TrainingPair trainingPair : trainingPairList) trainingPair.L2normalize();

        System.out.println("Reading models..");
        OneVsAllLibLinear classifier = OneVsAllLibLinear.load("E:\\Desktop\\JSON_SWEPUB\\multiLabelExperiment\\OneVsAllEngLevel5.ser");

        //L2-normalized if it was at training  time
        List<TrainingPair> trainingPairList = classifier.getTrainingPairList();
        List<Integer> classLabelsInOrder = classifier.getOrderedClassLabels();

        ExampleBasedPrecision exampleBasedPrecision = new ExampleBasedPrecision();
        ExampleBasedRecall exampleBasedRecall = new ExampleBasedRecall();

        System.out.println("Classifying everything..");
        for(TrainingPair trainingPair : trainingPairList) {

            double[] probability = classifier.predict(trainingPair);
            GroundTruth groundTruth = Eval.getGroundTruth(trainingPair,classLabelsInOrder);
            MultiLabelOutput multiLabelOutput = Eval.multiLabelOutput(probability,0.7);


            exampleBasedRecall.update(multiLabelOutput,groundTruth);
            exampleBasedPrecision.update(multiLabelOutput,groundTruth);


        }

        System.out.println("multi-label Recall: "  + exampleBasedRecall.getValue() + " C=" + classifier.getC());
        System.out.println("multi-label Precision: "  + exampleBasedPrecision.getValue() + " C=" + classifier.getC());

        //c=2 seems we are predicting on average more classes than in ground truth? Problem with class imbalance and class weights, cutoff?
        //multi-label Recall: 0.9407424912403894
        //multi-label Precision: 0.6599490706251856

        //multi-label Recall: 0.9877642882875671 C=6
        //multi-label Precision: 0.7524690359455993 C=6

        //c=6, cutoff now arbitrarily 0.6..0.7..
        //multi-label Recall: 0.9669661856695109 C=6 //0.91
        //multi-label Precision: 0.7975799902362364 C=6 //0.81
    }
}
