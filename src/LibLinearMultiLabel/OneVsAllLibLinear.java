package LibLinearMultiLabel;

import LibLinearMultiLabel.cc.fork.*;
import com.koloboke.collect.map.ObjIntCursor;
import jsat.utils.concurrent.ParallelUtils;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OneVsAllLibLinear implements Serializable {

    private static final long serialVersionUID = -1956047276742854034L;


    private Model[] models;
    private List<Integer> orderedClassLabels;
    private List<TrainingPair> trainingPairList;
    private final int n;
    private final int m;
    private int C=1; // cost of constraints violation, set with train()
    //todo weight for rare class

    public OneVsAllLibLinear(List<TrainingPair> trainingPairList, List<Integer> orderedClassLabels, int n, int m) {

        this.models = new Model[orderedClassLabels.size()];
        this.orderedClassLabels = orderedClassLabels;
        this.trainingPairList = trainingPairList;

        this.n =n; //nr of observations
        this.m = m; //nr of features


    }

    public int getC() {
        return C;
    }

    public int getN() {
        return n;
    }

    public int getM() {
        return m;
    }

    public List<Integer> getOrderedClassLabels() {
        return orderedClassLabels;
    }

    public List<TrainingPair> getTrainingPairList() {
        return trainingPairList;
    }

    public void train(int C) {

        this.C = C;

        //set up basic problem for LibLinear
        FeatureNode[][] matrix = new FeatureNode[ n ][];
        for(int i=0; i<n; i++) matrix[i] = this.trainingPairList.get(i).getFeatureNodes();

        Problem[] problems = new Problem[this.orderedClassLabels.size()];

        for(int i=0; i < this.orderedClassLabels.size(); i++) {

            Problem problem = new Problem();
            problem.l = this.n; //observations
            problem.n = this.m; //features
            problem.x = matrix;

            double[] GROUP_ARRAY = new double[matrix.length];
            for(int j=0; j<matrix.length; j++) {

                GROUP_ARRAY[j] = this.trainingPairList.get(j).getClassLabels().contains(  this.orderedClassLabels.get(i)  ) ? 1 : 0;
                problem.y = GROUP_ARRAY;

            }
            problem.bias = 1.0D;
            problems[i] = problem;

        }


        //Same for all, TODO can there be a problems with concurrency here?
        SolverType solver = SolverType.L2R_LR_DUAL; // -s 7
        double eps = 0.001; // stopping criteria

        Parameter parameter = new Parameter(solver, this.C, eps);
        double[] classWeights = new double[2];
        classWeights[0] = 10.0; //more weight to the rare class
        classWeights[1] = 1.0;
        int[] weightLabels = new int[]{1,0};
        parameter.setWeights(classWeights,weightLabels);


        ParallelUtils.range(0,this.orderedClassLabels.size(),true).forEach(i -> {

            System.out.println("Training model: " + i);
            this.models[i] = Linear.train(problems[i], parameter);

        });


    }


    public double[] predict(TrainingPair trainingPair) {

        //probability for class 1 in every model

        double[] predProb = new double[this.orderedClassLabels.size()];
        double[] binary = new double[2];

        for(int i=0; i< predProb.length; i++) {


            Linear.predictProbability(this.models[i],trainingPair.getFeatureNodes(), binary );
            int[] order = this.models[i].getLabels();

            if(order[0] == 1) { predProb[i] = binary[0]; } else { predProb[i] = binary[1]; }

        }



        return predProb;


    }


    public void save(String file) {

        try
        {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);


            oos.writeObject(this);
            oos.close();
            fos.close();
            System.out.println("Serialized OneVsAllLibLinear to: " + file);
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
        }


    }

    public static OneVsAllLibLinear load(String file) {
        OneVsAllLibLinear OneVsAllLibLinear = null;

        try
        {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);

            OneVsAllLibLinear = (OneVsAllLibLinear)ois.readObject();


            ois.close();
            fis.close();
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
            return null;
        }catch(ClassNotFoundException c)
        {
            System.out.println("Class not found");
            c.printStackTrace();
            return null;
        }

        return OneVsAllLibLinear;
    }



}
