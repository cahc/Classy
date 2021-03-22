package DimensionReduction;

import TrainAndPredict.HelperFunctions;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.DataPoint;
import jsat.linear.DenseVector;
import jsat.linear.SparseVector;
import jsat.linear.Vec;
import meka.core.A;
import multilabel2.MultiLabelHSV;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PingPong {

    public static double similarity(Vec v, Vec v2) {

        double dot = v.dot(v2);

        double norm = v.pNorm(2.0D);
        double norm2 = v2.pNorm(2.0D);

        return(dot/(norm*norm2));

    }


    public static void main(String[] args) throws IOException {

        //List<SparseVector> vecList = readSparseFromCluto("re0.mat");


        List<DataPoint> dataPointList = MultiLabelHSV.load("/Users/cristian/Desktop/JSON_SWEPUB/SwePubJsonDataPointsLevel5LangEng.ser");

        List<SparseVector> vecList = new ArrayList<>();

        for(DataPoint dp : dataPointList) vecList.add(  (SparseVector)dp.getNumericalValues()  );

        //ClassificationDataSet classificationDataSet = HelperFunctions.readJSATdata("myDataSet.jsat");
        //List<SparseVector> vecList = (List<SparseVector>)(Object)classificationDataSet.getDataVectors();

        //two run of online k-means for initialization of centroids
        OnlineSphericalKmeans onlineSphericalKmeans = new OnlineSphericalKmeans(vecList,1000,2);
        onlineSphericalKmeans.fit();
        int[] partition = onlineSphericalKmeans.getPartition();


        BatchSphericalKmeans batchSphericalKmeans = new BatchSphericalKmeans(vecList,partition,1000,10,false);
        batchSphericalKmeans.fit();

        List<DenseVector> axis = batchSphericalKmeans.getCentroids();

        System.out.println("projecting vector");


        List<DataPoint> projectedDataPoints = new ArrayList<>();

        for(int i=0; i<vecList.size(); i++) {

            DenseVector projectedVector = Project.project(vecList.get(i), axis,0.001);

            projectedDataPoints.add(  new DataPoint( projectedVector, dataPointList.get(i).getCategoricalValues(), dataPointList.get(i).getCategoricalData()  )  );

        }


        MultiLabelHSV.saveDataPointList(projectedDataPoints, "/Users/cristian/Desktop/JSON_SWEPUB/SwePubJsonDataPointsProjected1000Level5LangEng.ser" );



        int first =100;
        int second = 9099;
        System.out.println("Best cluster: " + batchSphericalKmeans.getPartition()[first]);
        DenseVector projectedVector = Project.project(vecList.get(first), axis,0.001);

        System.out.println("Best cluster: " + batchSphericalKmeans.getPartition()[second]);
        DenseVector projectedVector2 = Project.project(vecList.get(second), axis,0.001);


        System.out.println(projectedVector);
        System.out.println(projectedVector.get(batchSphericalKmeans.getPartition()[first]));

        System.out.println(projectedVector2);
        System.out.println(projectedVector2.get(batchSphericalKmeans.getPartition()[second]));

        System.out.println("Similarity between projected vectors: " + similarity(projectedVector, projectedVector2));

        System.out.println("Similarity between original vectors: " + similarity(vecList.get(first), vecList.get(second)));

        System.out.println("centroid nnz%:");
        System.out.println(batchSphericalKmeans.getCentroids().get(0).nnz() / (double)batchSphericalKmeans.getCentroids().get(0).length()  );

        System.out.println("centroid nnz%:");
        System.out.println(batchSphericalKmeans.getCentroids().get(1).nnz() / (double)batchSphericalKmeans.getCentroids().get(1).length()  );



    }
}
