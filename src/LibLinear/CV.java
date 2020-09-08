package LibLinear;
import Interface.CmdParser;
import de.bwaldvogel.liblinear.*;
import jsat.utils.IntList;
import meka.core.F;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;



public class CV {

    private static final long  DEFAULT_RANDOM_SEED = 0L;
    static Random              random              = new Random(DEFAULT_RANDOM_SEED);



    public static double randomHoldOut(Problem problem, Parameter parameters, double holdoutFraction, int runs) {

        //keep x Feature[][] intact

        //create a subproblem of size 90% approx

        //predict using the other 10% from x to test prediction error

        double[] labels = problem.y;


        IntList oneClassIndices = new IntList();
        IntList zeroClassIndices = new IntList();

        for(int i=0; i<labels.length; i++) {

            if( labels[i] == 1 )  { oneClassIndices.add(i); } else {zeroClassIndices.add(i); }
        }


        int n_hold_out_L1 = (int)Math.ceil( oneClassIndices.size() * holdoutFraction );
        int n_hould_out_L2 = (int)Math.ceil( zeroClassIndices.size() * holdoutFraction );


       double[] eval_metrics = new double[runs];
       //do nruns from here and take an average..

       for(int k=0; k<runs; k++) {

           Collections.shuffle(oneClassIndices);
           Collections.shuffle(zeroClassIndices);

           Problem subproblem = new Problem();
           subproblem.bias = problem.bias;
           subproblem.n = problem.n;
           subproblem.l = problem.l - (n_hold_out_L1 + n_hould_out_L2);


           Feature[][] x = new Feature[subproblem.l][];
           double y[] = new double[subproblem.l];


           int j = 0;

           for (int i = n_hold_out_L1; i < oneClassIndices.size(); i++) {

               x[j] = problem.x[oneClassIndices.get(i)];
               y[j] = 1;

               j++;
           }

           for (int i = n_hould_out_L2; i < zeroClassIndices.size(); i++) {

               x[j] = problem.x[zeroClassIndices.get(i)];
               y[j] = 0;

               j++;
           }

           subproblem.x = x;
           subproblem.y = y;

           System.out.println("training on random subsampling....");

           Model model = Linear.train(subproblem, parameters);


           //evaluate on holdout..


           int[] predicted = new int[n_hold_out_L1 + n_hould_out_L2];
           int[] groundTruth = new int[n_hold_out_L1 + n_hould_out_L2];


           j = 0;

           for (int i = 0; i < n_hold_out_L1; i++) {

               double label = Linear.predict(model, problem.x[oneClassIndices.get(i)]);

               predicted[j] = (int) label;
               groundTruth[j] = 1;
               j++;
           }


           for (int i = 0; i < n_hould_out_L2; i++) {

               double label = Linear.predict(model, problem.x[zeroClassIndices.get(i)]);

               predicted[j] = (int) label;
               groundTruth[j] = 0;
               j++;

           }

           double finalRate = CmdParser.getRateBinaryPostClassified(predicted, groundTruth);
           System.out.println("final rate: " + finalRate);

           double prec = meka.core.Metrics.P_Precision(groundTruth, predicted);
           double rec = meka.core.Metrics.P_Recall(groundTruth, predicted);
           System.out.println("P: " + prec);
           System.out.println("R: " + rec);
           System.out.println("F1: " + (2 * prec * rec) / (rec + prec));
           System.out.println();

           eval_metrics[k] = (2 * prec * rec) / (rec + prec);
       }

       double averageMetric = 0;

       for(int i=0; i<eval_metrics.length; i++) {

           averageMetric += eval_metrics[i];

       }

       return averageMetric/runs;
    }


    public static void main(String[] arg) {

        //randomHoldOut(0.5);

    }


}
