package multilabel2;

import com.koloboke.collect.map.IntIntCursor;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;
import jsat.classifiers.CategoricalResults;
import jsat.classifiers.DataPoint;
import jsat.utils.SystemInfo;
import mulan.classifier.MultiLabelOutput;
import mulan.evaluation.GroundTruth;
import mulan.evaluation.measure.*;

import java.util.List;

public class Tester {

    public static void main(String[] arg) {



        List<DataPoint> dataPointList = MultiLabelHSV.load("/Users/cristian/Desktop/JSON_SWEPUB/SwePubJsonDataPointsLDAProjectedLevel5LangEng.ser");


        List<DataPoint> dataPointList2 = MultiLabelHSV.load("/Users/cristian/Desktop/JSON_SWEPUB/SwePubJsonDataPointsLevel5LangEng.ser");




        ExampleBasedPrecision examplePrecision = new ExampleBasedPrecision();
        ExampleBasedAccuracy exampleAccuracy = new ExampleBasedAccuracy();
        ExampleBasedRecall exampleBasedRecall = new ExampleBasedRecall();
        ExampleBasedFMeasure exampleBasedFMeasure  = new ExampleBasedFMeasure();


        MacroFMeasure macroF = new MacroFMeasure( dataPointList.get(0).getCategoricalValues().length  ); //label-based

        for(int i=0; i<dataPointList.size(); i++) {

            int[] cr = dataPointList.get(i).getCategoricalValues();
            int[] cr2 = dataPointList2.get(i).getCategoricalValues();

            MultiLabelOutput predictions = Evaluate.multiLabelOutput(cr);
            GroundTruth groundTruth = Evaluate.groundTruth( cr2 );

            //measures
            examplePrecision.update(predictions,groundTruth);
            exampleAccuracy.update(predictions,groundTruth);
            exampleBasedRecall.update(predictions,groundTruth);
            exampleBasedFMeasure.update(predictions,groundTruth);

            macroF.update(predictions,groundTruth);
            //  microF.update(predictions,groundTruth);

        }


        System.out.println("Example-based Accuracy: institution classifier: " + String.format("%.3f",exampleAccuracy.getValue()));
        System.out.println("Example-based Precision: institution classifier: " + String.format("%.3f",examplePrecision.getValue()));
        System.out.println("Example-based Recall: institution classifier: " + String.format("%.3f",exampleBasedRecall.getValue()));
        System.out.println("Example-based F-measure: institution classifier: " + String.format("%.3f",exampleBasedFMeasure.getValue()));
        System.out.println("MacroF: institution classifier: " + String.format("%.3f",macroF.getValue()));




    }

}
