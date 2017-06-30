package TrainAndPredict;

import jsat.classifiers.CategoricalData;
import jsat.classifiers.ClassificationDataSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Cristian on 2017-04-10.
 */
public class Splitter {

    double prop;
    ClassificationDataSet cds;
    List<Integer>[] vectorCategoryIndices;
    List<Integer>[] holdOutIndices;

    HashSet<Integer> holdOutIndicesSet = new HashSet<>();
    HashSet<Integer> trainingIndicesSet = new HashSet<>();

    public Splitter(ClassificationDataSet cds, double prop) { this.cds = cds; this.prop = prop;}



    public List<ClassificationDataSet> splitInTrainingAndHoldOut() {


        int nrclasses = this.cds.getClassSize();

        this.vectorCategoryIndices = new ArrayList[nrclasses];

        this.holdOutIndices = new ArrayList[nrclasses];

        Integer[] categorySize = new Integer[nrclasses];

        for(int i=0; i<nrclasses; i++) { categorySize[i] = cds.classSampleCount(i); System.out.println(categorySize[i]); }



        for(int i=0; i<nrclasses; i++) { vectorCategoryIndices[i] = new ArrayList<Integer>();   holdOutIndices[i] = new ArrayList<Integer>(); }



        for(int i=0; i< cds.getSampleSize(); i++) {


            vectorCategoryIndices[ cds.getDataPointCategory(i) ].add(i);

        }


        for(int j=0; j< vectorCategoryIndices.length; j++) {

            int houldOutSample = (int)Math.ceil(categorySize[j] * prop);
            if(houldOutSample <= 0) continue; //todo fix categories containing zero records!
            jsat.utils.ListUtils.randomSample(vectorCategoryIndices[j], holdOutIndices[j], houldOutSample);

        }



        for(int i=0; i<holdOutIndices.length; i++) {


            holdOutIndicesSet.addAll( holdOutIndices[i] );
        }

        for(int i=0; i<cds.getSampleSize(); i++) {

           if(!holdOutIndicesSet.contains(i)) trainingIndicesSet.add(i);

        }


        ClassificationDataSet training = new ClassificationDataSet(cds.getDataPoint(0).getNumericalValues().length(),new CategoricalData[]{}, cds.getPredicting());
        ClassificationDataSet houldOut = new ClassificationDataSet(cds.getDataPoint(0).getNumericalValues().length(),new CategoricalData[]{}, cds.getPredicting());

        for(int i=0; i< cds.getSampleSize(); i++) {


            if(holdOutIndicesSet.contains(i)) {  houldOut.addDataPoint( cds.getDataPoint(i).getNumericalValues(), cds.getDataPointCategory(i)  );  } else {

                training.addDataPoint(cds.getDataPoint(i).getNumericalValues(), cds.getDataPointCategory(i) );

            }

        }


        List<ClassificationDataSet> pair = new ArrayList<ClassificationDataSet>(2);

        pair.add(training);
        pair.add(houldOut);

        return pair;

    }


    public HashSet<Integer> getHoldOutIndices() {

        return holdOutIndicesSet;
    }


    public HashSet<Integer> getTrainingIndicesSet() {

        return trainingIndicesSet;
    }




}
