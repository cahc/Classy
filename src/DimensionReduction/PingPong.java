package DimensionReduction;

import TrainAndPredict.HelperFunctions;
import jsat.classifiers.ClassificationDataSet;
import jsat.linear.SparseVector;

import java.io.IOException;
import java.util.List;

public class PingPong {


    public static void main(String[] args) throws IOException {

        //List<SparseVector> vecList = readSparseFromCluto("re0.mat");


        ClassificationDataSet classificationDataSet = HelperFunctions.readJSATdata("myDataSet.jsat");

        List<SparseVector> vecList = (List<SparseVector>)(Object)classificationDataSet.getDataVectors();

        OnlineSphericalKmeans onlineSphericalKmeans = new OnlineSphericalKmeans(vecList,100,2);
        onlineSphericalKmeans.fit();
        int[] partition = onlineSphericalKmeans.getPartition();


        BatchSphericalKmeans BatchSphericalKmeans = new BatchSphericalKmeans(vecList,partition,100,5,false);
        BatchSphericalKmeans.fit();




    }
}
