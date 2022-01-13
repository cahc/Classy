package LibLinearMultiLabel;

import mulan.classifier.MultiLabelOutput;
import mulan.evaluation.GroundTruth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Eval {

    static class Prediction implements Comparable<Prediction> {

        Integer classNr;
        double prob;

        public Prediction(Integer classNr, double prob) {

            this.classNr = classNr;
            this.prob = prob;
        }


        @Override
        public int compareTo(Prediction other) {

            if( this.prob > other.prob ) return -1;
            if(this.prob < other.prob) return 1;
            return 0;

        }

        @Override
        public String toString() {
            return "Prediction{" +
                    "classNr=" + classNr +
                    ", prob=" + prob +
                    '}';
        }
    }

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


    public static List<Prediction> PredictedClassesWithProb(double[] probabilityDist,double cutOff, List<Integer> classLabelsInOrder) {


        List<Prediction> predictionList = new ArrayList<>();

        for(int i=0; i<probabilityDist.length; i++) {

            if(probabilityDist[i] > cutOff) predictionList.add( new Prediction(classLabelsInOrder.get(i),probabilityDist[i]) );

        }

        Collections.sort(predictionList);

    return predictionList;

    }

    public static int numberOfClassesPredictedOrAssigned(MultiLabelOutput output) {


        int count = 0;
        for(int i=0; i< output.getBipartition().length; i++ ) {

            if(output.getBipartition()[i]) count++;

        }


        return count;

    }

    public static int numberOfClassesPredictedOrAssigned(GroundTruth input) {


        int count = 0;
        for(int i=0; i< input.getTrueLabels().length; i++ ) {

            if(input.getTrueLabels()[i]) count++;

        }

        return count;

    }

}

