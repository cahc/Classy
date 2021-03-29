package multilabel2;

import jsat.classifiers.CategoricalResults;
import jsat.classifiers.DataPoint;
import jsat.classifiers.linear.LinearBatch;
import jsat.classifiers.linear.LogisticRegressionDCD;
import jsat.lossfunctions.LogisticLoss;
import jsat.lossfunctions.SoftmaxLoss;
import mulan.classifier.MultiLabelOutput;
import mulan.evaluation.GroundTruth;
import mulan.evaluation.measure.*;

import java.util.List;

public class TrainExperiments {


    public static void main(String[] arg) {

        List<DataPoint> dataPointList = MultiLabelHSV.load("/Users/cristian/Desktop/JSON_SWEPUB/SwePubJsonDataPointsLevel5LangEng.ser");


        System.out.println("Started training..");
         double start = System.currentTimeMillis();


       //LinearBatch base = new LinearBatch( new LogisticLoss(), 0.000001 );
       // complete failure Training took:  470.456seconds

        /*
        Example-based Accuracy: institution classifier: 0,210
        Example-based Precision: institution classifier: 0,242
        Example-based Recall: institution classifier: 0,212
        Example-based F-measure: institution classifier: 0,220
        MacroF: institution classifier: 0,033

        predictions where no class was above 0.5..: 374740

         */




      LogisticRegressionDCD base = new LogisticRegressionDCD(7,500);
      base.setUseBias(true);

        MultiLabel multiLabelClassifier = new MultiLabel(base,true,dataPointList);
        System.out.println("Training multi-label classifier. # labels: " + multiLabelClassifier.numberOfLabels());
        multiLabelClassifier.train(0.0);
        double    stop = System.currentTimeMillis();

        //multiLabelClassifier.save("/Users/cristian/Desktop/JSON_SWEPUB/SwePubJsonClassifierLevel5LangEng.ser");

        System.out.println("Training took:  " + (stop - start) / 1000.0 + "seconds");


        ExampleBasedPrecision examplePrecision = new ExampleBasedPrecision();
        ExampleBasedAccuracy exampleAccuracy = new ExampleBasedAccuracy();
        ExampleBasedRecall exampleBasedRecall = new ExampleBasedRecall();
        ExampleBasedFMeasure exampleBasedFMeasure  = new ExampleBasedFMeasure();


        MacroFMeasure macroF = new MacroFMeasure( multiLabelClassifier.numberOfLabels() ); //label-based
      //  MicroFMeasure microF = new MicroFMeasure( multiLabelClassifier.numberOfLabels() ); //TODO requires mweka.core

        int i=0;
        int fail = 0;
        for(DataPoint dp : dataPointList) {


            CategoricalResults cr = multiLabelClassifier.predict(dp);

            if( cr.getProb( cr.mostLikely() ) < 0.5) fail++;

            if(i % 100000 == 0) System.out.println(cr.getVecView());

            MultiLabelOutput predictions = Evaluate.multiLabelOutput(cr,false); //above 0.5, todo crossvalidate
            GroundTruth groundTruth = Evaluate.groundTruth( dp.getCategoricalValues() );

            //measures
            examplePrecision.update(predictions,groundTruth);
            exampleAccuracy.update(predictions,groundTruth);
            exampleBasedRecall.update(predictions,groundTruth);
            exampleBasedFMeasure.update(predictions,groundTruth);

            macroF.update(predictions,groundTruth);
          //  microF.update(predictions,groundTruth);

            i++;
        }


        System.out.println("Example-based Accuracy: institution classifier: " + String.format("%.3f",exampleAccuracy.getValue()));
        System.out.println("Example-based Precision: institution classifier: " + String.format("%.3f",examplePrecision.getValue()));
        System.out.println("Example-based Recall: institution classifier: " + String.format("%.3f",exampleBasedRecall.getValue()));
        System.out.println("Example-based F-measure: institution classifier: " + String.format("%.3f",exampleBasedFMeasure.getValue()));
        System.out.println("MacroF: institution classifier: " + String.format("%.3f",macroF.getValue()));

      //  System.out.println("MicroF: " + String.format("%.3f",microF.getValue()));
        System.out.println("predictions where no class was above 0.5..: " + fail);


    }
}
