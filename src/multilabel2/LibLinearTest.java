package multilabel2;

import de.bwaldvogel.liblinear.*;
import jsat.classifiers.CategoricalData;
import jsat.classifiers.DataPoint;
import jsat.linear.IndexValue;
import mulan.classifier.MultiLabelOutput;
import mulan.evaluation.GroundTruth;
import mulan.evaluation.measure.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class LibLinearTest {


    public static Comparator<FeatureNode> sortFeatureNodes = new Comparator<FeatureNode>() {
        @Override
        public int compare(FeatureNode o1, FeatureNode o2) {

            if(o1.index < o2.index) return -1;
            if(o1.index > o2.index) return 1;
            return 0;

        }
    };


    public static FeatureNode[] convertDatapointToFeatureNode(DataPoint dp) {

        int n = dp.getNumericalValues().nnz();
        FeatureNode[] featureNodes = new FeatureNode[n];
        Iterator<IndexValue> iter = dp.getNumericalValues().getNonZeroIterator();

        int i=0;
        while(iter.hasNext()) {

            IndexValue indexValue = iter.next();

            featureNodes[i] = new FeatureNode( indexValue.getIndex()+1, indexValue.getValue()  ); //OBS index value must start at one (1)
            i++;
        }


        Arrays.sort(featureNodes,sortFeatureNodes);
        return featureNodes;
    }



    public static void main(String[] args) throws IOException {


        List<DataPoint> dataPointList = MultiLabelHSV.load("/Users/cristian/Desktop/JSON_SWEPUB/SwePubJsonDataPointsLevel5LangEng.ser");
        FeatureNode[][] matrix =  new FeatureNode[dataPointList.size()][];
        int[][] indicatorVariables = new int[dataPointList.size()][]; //0 - 1

        System.out.println("Converting..");
        int i=0;
        for(DataPoint dp : dataPointList) {

            matrix[i] = convertDatapointToFeatureNode(dp);
            indicatorVariables[i] = dp.getCategoricalValues();

            i++;
        }

        for(CategoricalData categoricalData : dataPointList.get(0).getCategoricalData() ) {

            System.out.println( categoricalData.getCategoryName() );


        }

        //one vs all

        Problem problem = new Problem();
        problem.l = matrix.length; //observations
        problem.n = dataPointList.get(0).getNumericalValues().length(); //features
        problem.x = matrix;

        //first group
        double[] GROUP_ARRAY = new double[matrix.length];

        for(int j=0; j<matrix.length; j++) {

            GROUP_ARRAY[j] = indicatorVariables[j][142]; // 1 or 0

        }

        problem.y = GROUP_ARRAY;

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


        ExampleBasedPrecision examplePrecision = new ExampleBasedPrecision();
        ExampleBasedRecall exampleBasedRecall = new ExampleBasedRecall();

        MacroFMeasure macroFMeasure = new MacroFMeasure(2);


        int[] labels = model.getLabels();

        int hit=0;
        for(int j= 0; j<matrix.length; j++) {

            double[] pred = new double[2];
            Linear.predictProbability(model,matrix[j], pred );

            MultiLabelOutput m_out = Evaluate.multiLabelOutputLibLinear(pred,labels);
            GroundTruth m_truth = Evaluate.groundTruthLibLinear( (int)GROUP_ARRAY[j] );

            //TODO bug m_out is mostly false, false
            //System.out.println( Arrays.toString(pred) + " ::" + m_out + " --" + m_truth);

            //average over examples
            examplePrecision.update(m_out,m_truth);
            exampleBasedRecall.update(m_out,m_truth);
            //average over classes
            macroFMeasure.update(m_out,m_truth);
        }


        System.out.println("Example-based Precision: " + examplePrecision.getValue());
        System.out.println("Example-based Recall: " + exampleBasedRecall.getValue());
        System.out.println("Label-based F-measure: " + macroFMeasure.getValue() );
        System.out.println("Label-based F-measure label index 0: " + macroFMeasure.getValue(0) );
        System.out.println("Label-based F-measure label index 1: " + macroFMeasure.getValue(1) );


    }
}