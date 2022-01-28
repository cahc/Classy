package LibLinearMultiLabel;
import java.util.*;

public class TrainAndSaveModel {

    public static void main(String[] arg) {


        String language = "eng";
        int level = 5;
        boolean useTermWeights = true;

        //language and level specific level 3
        double C = 4;
        double minorityWeight = 10; //increase to 10 for english level 5, 4 for swedish

        //ENG level 3
        //double C = 4;
        //double minorityWeight = 10;

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


        //identify all classLabels
        Set<Integer> classLabels = new HashSet<>();
        for(TrainingPair trainingPair : trainingPairList) classLabels.addAll( trainingPair.getClassLabels() );
        List<Integer> sortedClassLabels = new ArrayList<>(classLabels);
        Collections.sort(sortedClassLabels);


        OneVsAllLibLinear oneVsAllLibLinear = new OneVsAllLibLinear(trainingPairList,sortedClassLabels,trainingPairList.size(), dim );
        oneVsAllLibLinear.train(C,minorityWeight);

        System.out.println("Serializing models");
        oneVsAllLibLinear.save("E:\\SWEPUB\\multilabel_"+language+"_"+level+".ser");

    }

}
