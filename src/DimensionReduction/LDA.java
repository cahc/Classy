package DimensionReduction;

import jsat.SimpleDataSet;
import jsat.classifiers.DataPoint;
import jsat.linear.Vec;
import jsat.text.topicmodel.OnlineLDAsvi;
import jsat.utils.SystemInfo;
import multilabel2.MultiLabelHSV;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LDA {



    public static void main(String[] arg) {

        List<DataPoint> dataPointList = MultiLabelHSV.load("/Users/cristian/Desktop/JSON_SWEPUB/SwePubJsonDataPointsNOWEIGHTNONORMLevel5LangEng.ser");

        OnlineLDAsvi lda = new OnlineLDAsvi();

        //we will use multiple-cores to process this data
        ExecutorService ex = Executors.newFixedThreadPool(4);


        //the parameters set here are generaly decent defaults. The OnlineLDAsvi
        //documentation includes a table of values to test that are recomended
        //by the original paper
        int K = 500;

        lda.setAlpha(1.0/K);
        lda.setEta(1.0/K);
        lda.setKappa(0.5);
        lda.setMiniBatchSize(4096);
        //Because this is a small dataset, and this algorithm intended for
        //larger corpora , we will do more than one epoch and set tau0 to a
        //small value
        lda.setEpochs(1);
        lda.setTau0(64);

        double start = System.currentTimeMillis();

        lda.model(new SimpleDataSet(dataPointList), K, ex);

        double    stop = System.currentTimeMillis();
        System.out.println("Training took:  " + (stop - start) / 1000.0 + "seconds");

        //projected versions

        start = System.currentTimeMillis();

        List<DataPoint> projectedDatapoints = new ArrayList<>();

        for(DataPoint dataPoint : dataPointList) {

            Vec projected = lda.getTopics(dataPoint.getNumericalValues());
            DataPoint d = new DataPoint(projected, dataPoint.getCategoricalValues(),dataPoint.getCategoricalData());
            projectedDatapoints.add(d);

        }

        stop = System.currentTimeMillis();
        System.out.println("Projecting took:  " + (stop - start) / 1000.0 + "seconds");

        MultiLabelHSV.saveDataPointList(projectedDatapoints,"/Users/cristian/Desktop/JSON_SWEPUB/SwePubJsonDataPointsLDAProjectedLevel5LangEng.ser");


        ex.shutdown();


    }
}
