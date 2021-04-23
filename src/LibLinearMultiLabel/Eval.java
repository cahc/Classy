package LibLinearMultiLabel;

import mulan.classifier.MultiLabelOutput;
import mulan.evaluation.GroundTruth;

import java.util.Collections;
import java.util.List;

public class Eval {

    public static GroundTruth getGroundTruth(TrainingPair trainingPair, List<Integer> classLabelsInOder) {

        boolean[] groundtruth = new boolean[classLabelsInOder.size()];

        for(Integer label : trainingPair.getClassLabels())  {

            int indice = Collections.binarySearch(classLabelsInOder,label);

            if(indice < 0) {System.out.println("Warning, binary search returned negative indice"); continue;}

            groundtruth[indice] = true;


        }


        return new GroundTruth(groundtruth);
    }

    public static MultiLabelOutput multiLabelOutput(double[] probabilityDist, double cutOff) {

        boolean[] predicted = new boolean[probabilityDist.length];


        for(int i=0; i<predicted.length; i++)   {

            if(probabilityDist[i] > cutOff) predicted[i] = true;

        }

        return( new MultiLabelOutput(predicted) );
    }



}

