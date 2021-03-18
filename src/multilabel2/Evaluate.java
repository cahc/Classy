package multilabel2;

import jsat.classifiers.CategoricalResults;
import mulan.classifier.MultiLabelOutput;
import mulan.evaluation.GroundTruth;
import mulan.evaluation.measure.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Evaluate {


    public static GroundTruth groundTruth(int[] indicatorArray) {

        boolean[] groundtruth = new boolean[indicatorArray.length];

        for(int i=0; i<indicatorArray.length; i++) {

            if(indicatorArray[i] == 1) groundtruth[i] = true;

        }

        return new GroundTruth(groundtruth);
    }


    public static MultiLabelOutput multiLabelOutput(CategoricalResults cr, boolean onlyTheBest) {

        boolean[] predicted = new boolean[cr.size()];
        int best = cr.mostLikely();

        if(onlyTheBest) {

            predicted[best] = true;

            return( new MultiLabelOutput(predicted) );

        } else {

            double probabilityOfBest = cr.getProb(best);

            if(probabilityOfBest < 0.5) {

                //no category is above 0.5, so we return the one with the highest probability
                predicted[best] = true;
                return( new MultiLabelOutput(predicted) );

            } else {

                for(int i=0; i<predicted.length; i++) {

                    if(cr.getProb(i) > 0.5 ) predicted[i] = true;

                }

                return( new MultiLabelOutput(predicted) );
            }




        }


    }


    public static boolean hasMultiplePredictedCategories(MultiLabelOutput multiLabelOutput) {

        int labels = 0;

        for(int i=0; i<multiLabelOutput.getBipartition().length; i++) {

            if(multiLabelOutput.getBipartition()[i]) labels++;
        }

        if(labels > 1) return true;

        return false;
    }

    public static List<Integer> getMultipleindicesAboveThreshold(MultiLabelOutput multiLabelOutput) {

        List<Integer> indices = new ArrayList<>(2);

        for(int i=0; i<multiLabelOutput.getBipartition().length; i++) {

            if(multiLabelOutput.getBipartition()[i]) indices.add(i);
        }

        return indices;
    }

    public static boolean completelyWrong(MultiLabelOutput predictions, GroundTruth groundTruth) {

        boolean[] pre = predictions.getBipartition();
        boolean[] tru = groundTruth.getTrueLabels();

        for(int i=0; i<tru.length; i++) {

            if(tru[i]) {

                if(pre[i]) return false;

            }

        }

        return true;
    }


    public static int getBestIndex(int indicatorValues[]) {

        int best = -1;

        for(int i=0; i<indicatorValues.length; i++) {

            if(indicatorValues[i] > best) best = i;

        }

        return best;
    }


    public static void main(String[] arg) {


        ExampleBasedPrecision precision = new ExampleBasedPrecision();
        ExampleBasedAccuracy accuracy = new ExampleBasedAccuracy();

        GroundTruth groundTruth = groundTruth( new int[] {0,0,1});

        CategoricalResults cr = new CategoricalResults(3);
        cr.setProb(0,0.3);
        cr.setProb(1,0.501);
        cr.setProb(2,0.49);

        MultiLabelOutput out = multiLabelOutput(cr,false);

        System.out.println(Arrays.toString( out.getBipartition() ) );

        precision.update(out,groundTruth);
        System.out.println(precision.getValue());




    }
}
