package multilabel2;

import jsat.classifiers.*;
import jsat.linear.Vec;
import jsat.utils.concurrent.ParallelUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MultiLabel implements Serializable, Pred {

    private static final long serialVersionUID = -973737364433488888L;

    private final CategoricalData[] predicting; // length  number of labels (1/0).
    private final Classifier[] OneVsRestMultiLabel;
    private final Classifier baseClassifier;
    private boolean concurrentTraining;

    private final List<DataPoint> dataPointList; //we store the datapoints so we can get them from a serialized model
    private final int dimensionality;
    /*

    public DataPoint(Vec numericalValues, int[] categoricalValues, CategoricalData[] categoricalData)

     */


    //public MultiLabel() {}

    public MultiLabel(Classifier baseClassifier, boolean concurrentTraining, List<DataPoint> dataPoints) {

        this.baseClassifier = baseClassifier;
        this.concurrentTraining = concurrentTraining;
        this.predicting = dataPoints.get(0).getCategoricalData();
        this.OneVsRestMultiLabel = new Classifier[this.predicting.length];
        this.dimensionality = dataPoints.get(0).getNumericalValues().length();
        this.dataPointList = dataPoints;
        if(this.predicting.length != dataPoints.get(0).getCategoricalValues().length) {

            System.out.println("inconsistent datapoints!"); System.exit(0);
        }

        if(dataPoints.get(dataPoints.size()-1).getCategoricalValues().length != dataPoints.get(0).getCategoricalValues().length ) {

            System.out.println("inconsistent datapoints!"); System.exit(0);
        }


    }

    public String getLabel(int i) {

        return this.predicting[i].getCategoryName();
    }

    public void train(final double minRatioForRareLable) {


        ParallelUtils.range(0, this.predicting.length,this.concurrentTraining).forEach(i ->

        {

            final ClassificationDataSet cds = new ClassificationDataSet(this.dimensionality, new CategoricalData[]{}, new CategoricalData(2));


            for(DataPoint dp : this.dataPointList ) {

                cds.addDataPoint(dp.getNumericalValues(), dp.getCategoricalValue(i) ); // 1 or zero

            }

            int label_one = cds.getSamples(1).size();
            int label_zero =cds.getSamples(0).size();
            int total = label_one+label_zero;



            //
            //UPSAMPLING, category 1 is assumed to be much smaller than category 0, in this binary relevance multi-label contex..
            //
            final Random random = new Random();
            final int originalSampleSize = cds.getSamples(1).size();

            final int z =  (int)(total*minRatioForRareLable);
            final int resamplingSize = (z-originalSampleSize);


            if(resamplingSize > 0) {
                final List<DataPoint> resampledPoints = new ArrayList<>();
                for (int j = 0; j < resamplingSize; j++) {

                    DataPoint dp = cds.getSamples(1).get(random.nextInt(originalSampleSize)); //todo clone?
                    resampledPoints.add(dp);

                }


                for (DataPoint dp : resampledPoints) cds.addDataPoint(dp.getNumericalValues(), 1);
            }


            System.out.println(getLabel(i) + " --> # label one: " +label_one + " # label zero: " + label_zero + " (total=" + (label_one+label_zero) +")" + " new size of label one: " + cds.getSamples(1).size());

            this.OneVsRestMultiLabel[i] = this.baseClassifier.clone();

            //if parallel loop, train in serial mode
            if(this.concurrentTraining) { this.OneVsRestMultiLabel[i].train(cds,false); } else {

                this.OneVsRestMultiLabel[i].train(cds,true);
            }


        });


    }


    public CategoricalResults predict(DataPoint dp) {

        CategoricalResults cr = new CategoricalResults(this.predicting.length);

        for(int i=0; i<this.predicting.length; i++) {
            CategoricalResults result = this.OneVsRestMultiLabel[i].classify(dp);
            double prob = result.getProb(1);
            cr.setProb(i, prob);


        }

        return cr;
    }

    public CategoricalResults predict(Vec vec) {

        DataPoint dp = new DataPoint(vec);
        CategoricalResults cr = new CategoricalResults(this.predicting.length);

        for(int i=0; i<this.predicting.length; i++) {
            CategoricalResults result = this.OneVsRestMultiLabel[i].classify(dp);
            double prob = result.getProb(1);
            cr.setProb(i, prob);


        }

        return cr;
    }


    public void save(String file) {

        try
        {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
            fos.close();
            System.out.println("Serialized MultiLabel model is saved in " + file);
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
        }


    }


    public List<DataPoint> getTraningData() {

        return this.dataPointList;
    }

    public static MultiLabel load(String file) {

        MultiLabel multiLabel; // = null;

           try {

                    FileInputStream fis = new FileInputStream(file);

                    ObjectInputStream ois = new ObjectInputStream(fis);

                    multiLabel = (MultiLabel) ois.readObject();

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


           System.out.println("Successfully loaded serialized multiLabel model");
           return multiLabel;
    }

    public int numberOfLabels() {

        return this.predicting.length;

    }


    public static void main(String[] arg) {





    }

}
