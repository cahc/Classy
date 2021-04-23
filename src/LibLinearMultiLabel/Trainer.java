package LibLinearMultiLabel;

import LibLinearMultiLabel.cc.fork.FeatureNode;
import LibLinearMultiLabel.cc.fork.Problem;
import LibLinearMultiLabel.cc.fork.Linear;
import LibLinearMultiLabel.cc.fork.Model;
import LibLinearMultiLabel.cc.fork.Parameter;
import LibLinearMultiLabel.cc.fork.SolverType;

import java.util.*;

public class Trainer {

    public static void main(String[] arg) {

        //LETS TRY WITHOUT TERM WEIGHTING FIRST!


        //TODO not really needed here, using it for getting the number of features
        SimpleIndex simpleIndex = new SimpleIndex();
        simpleIndex.load("E:\\Desktop\\JSON_SWEPUB\\multiLabelExperiment\\simpleIndexEngLevel5.ser");


        //load training data
        List<TrainingPair> trainingPairList =  TrainingPair.load("E:\\Desktop\\JSON_SWEPUB\\multiLabelExperiment\\TrainingPairsEngLevel5.ser");

        //L2-normalize data
        System.out.println("L2-normalize");
        for(TrainingPair trainingPair : trainingPairList) trainingPair.L2normalize();

        //identify all classLabels
        Set<Integer> classLabels = new HashSet<>();
        for(TrainingPair trainingPair : trainingPairList) classLabels.addAll( trainingPair.getClassLabels() );
        List<Integer> sortedClassLabels = new ArrayList<>(classLabels);
        Collections.sort(sortedClassLabels);
        //System.out.print(sortedClassLabels);



        OneVsAllLibLinear oneVsAllLibLinear = new OneVsAllLibLinear(trainingPairList,sortedClassLabels,trainingPairList.size(), simpleIndex.size());
        oneVsAllLibLinear.train(6);

        System.out.println("Serializing models");
        oneVsAllLibLinear.save("E:\\Desktop\\JSON_SWEPUB\\multiLabelExperiment\\OneVsAllEngLevel5.ser");

        /*


        //set up problem for LibLinear

        Problem problem = new Problem();
        problem.l = trainingPairList.size(); //observations
        problem.n = simpleIndex.size(); //features

        //n * featurNode[]
        FeatureNode[][] matrix = new FeatureNode[ trainingPairList.size() ][];
        for(int i=0; i<trainingPairList.size(); i++) matrix[i] = trainingPairList.get(i).getFeatureNodes();
        problem.x = matrix;





        //one vs all, example with first classLabel (10101)
        int orderedClass = 0;

        double[] GROUP_ARRAY = new double[matrix.length];

        for(int j=0; j<matrix.length; j++) {

            GROUP_ARRAY[j] = trainingPairList.get(j).getClassLabels().contains(  sortedClassLabels.get(0)  ) ? 1 : 0;

        }
        problem.y = GROUP_ARRAY;

        //
        //PROBLEM SET UP OMPLETE FOR TEST CLASS
        //


        SolverType solver = SolverType.L2R_LR_DUAL; // -s 7
        double C = 2.0; // cost of constraints violation
        double eps = 0.001; // stopping criteria

        Parameter parameter = new Parameter(solver, C, eps);
        double[] classWeights = new double[2];
        classWeights[0] = 15.0; //more weight to the rare class
        classWeights[1] = 1.0;
        int[] weightLabels = new int[]{1,0};
        parameter.setWeights(classWeights,weightLabels);

        problem.bias = 1.0D;


        System.out.println("Training..");

        Model model = Linear.train(problem, parameter);

        //the order is dependant on what which label is seen first..
        System.out.println( Arrays.toString( model.getLabels() ) );


        //try some

        int max = 0;
        for(TrainingPair trainingPair : trainingPairList) {

            double[] pred = new double[2];
            Linear.predictProbability(model, trainingPair.getFeatureNodes(), pred);
            System.out.println(Arrays.toString(pred));

            max++;
            if(max >= 20) break;
        }




        */



    }

}
