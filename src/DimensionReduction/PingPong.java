package DimensionReduction;

import TrainAndPredict.HelperFunctions;
import jsat.classifiers.ClassificationDataSet;
import jsat.linear.DenseVector;
import jsat.linear.SparseVector;
import jsat.linear.Vec;

import java.io.IOException;
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


        ClassificationDataSet classificationDataSet = HelperFunctions.readJSATdata("myDataSet.jsat");

        List<SparseVector> vecList = (List<SparseVector>)(Object)classificationDataSet.getDataVectors();

        //two run of online k-means for initialization of centroids
        OnlineSphericalKmeans onlineSphericalKmeans = new OnlineSphericalKmeans(vecList,300,2);
        onlineSphericalKmeans.fit();
        int[] partition = onlineSphericalKmeans.getPartition();


        BatchSphericalKmeans batchSphericalKmeans = new BatchSphericalKmeans(vecList,partition,300,10,false);
        batchSphericalKmeans.fit();

        List<DenseVector> axis = batchSphericalKmeans.getCentroids();

        System.out.println("projecting vector");

        int first =100;
        int second = 9099;
        System.out.println("Best cluster: " + batchSphericalKmeans.getPartition()[first]);
        DenseVector projectedVector = Project.project(vecList.get(first), axis,0.05);

        System.out.println("Best cluster: " + batchSphericalKmeans.getPartition()[second]);
        DenseVector projectedVector2 = Project.project(vecList.get(second), axis,0.05);


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
