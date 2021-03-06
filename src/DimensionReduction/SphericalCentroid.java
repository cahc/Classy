package DimensionReduction;

import TrainAndPredict.HelperFunctions;
import jsat.classifiers.ClassificationDataSet;
import jsat.linear.DenseVector;
import jsat.linear.IndexValue;
import jsat.linear.SparseVector;
import jsat.linear.Vec;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class SphericalCentroid {

    DenseVector vector;

    double S; //sum of squares , the L2 norm of vector is the sqrt of S

    double l2norm; //sqrt(S)


    public SphericalCentroid(int dim) {

        this.S=0;
        this.l2norm =0;

        this.vector = new DenseVector(dim);

    }

    public SphericalCentroid(DenseVector vec) {

        this.vector = vec;

        double S = 0;

        for(int i=0; i< vec.length(); i++) {

            S = S + Math.pow(vector.get(i),2.0D);
        }

        this.S = S;
        this.l2norm = Math.sqrt(S);

    }


    public void add(SparseVector sparseVector) {


        //step one
        int[] nonzeroIndices = new int[ sparseVector.nnz() ];
        Iterator<IndexValue> nzItert = sparseVector.getNonZeroIterator();
        int j=0;
        while(nzItert.hasNext()) {

            nonzeroIndices[j] = nzItert.next().getIndex();
            j++;
        }


        double removeFromS = 0;
        for(int i=0; i<nonzeroIndices.length; i++) {

            double value = this.vector.get(   nonzeroIndices[i]   );

            if(value != 0.0D) {

                removeFromS = removeFromS + Math.pow(value,2.0D);

            }

        }


        this.S = this.S - removeFromS;

        //step 2 todo with a learning step, here implicit 1.0

        this.vector.mutableAdd(sparseVector);

        //step 3

        double addToS = 0;

        for(int i=0; i<nonzeroIndices.length; i++) {

            addToS = addToS + Math.pow( this.vector.get( nonzeroIndices[i] ), 2.0D);

        }

        this.S = this.S + addToS;


        //step 4, uppdate the l2-norm

        this.l2norm = Math.sqrt( this.S );
    }


    public double similarity(SparseVector s) {

        //assuming s is l2-normalized
        return ( s.dot(this.vector) / this.l2norm );

    }




    public static void main(String[] arg) throws IOException {



        ClassificationDataSet classificationDataSet = HelperFunctions.readJSATdata("myDataSet.jsat");
        List<Vec> vecList = classificationDataSet.getDataVectors();

        int N = vecList.size();
        int dim = vecList.get(0).length();

        SphericalCentroid sphericalCentroid = new SphericalCentroid(dim);


        for(int i=0; i< 100; i++) {


            sphericalCentroid.add( (SparseVector)vecList.get(i) );


            if(i % 10 == 0) System.out.println(i + " S: " + sphericalCentroid.S + " l2: " + sphericalCentroid.l2norm);

        }


        System.out.println();

        System.out.println(sphericalCentroid.S);
        System.out.println(sphericalCentroid.l2norm);


        System.out.println();


        DenseVector denseVector = new DenseVector(dim);
        for(int i=0; i< 100; i++) {


            denseVector.mutableAdd( (SparseVector)vecList.get(i) );

            denseVector.normalize(); //we avoid this costly step by using SphericalCentroid class instead

        }

        System.out.println("Should be 1.0: " + denseVector.dot( sphericalCentroid.vector ) / sphericalCentroid.l2norm );


   /*
        SparseVector sparseVector = new SparseVector(10,10);
        sparseVector.set(0,1);
        sparseVector.set(1,2);
        sparseVector.set(4,3);
        sparseVector.set(6,1);
        sparseVector.set(9,1);
        sphericalCentroid.add(sparseVector);
        System.out.println(sphericalCentroid.l2norm);
        sparseVector.normalize();
        System.out.println(sphericalCentroid.similarity(sparseVector));
        SparseVector sparseVector1 = new SparseVector(10,10);
        sparseVector1.set(2,1);
        sparseVector1.set(3,1);
        sparseVector1.set(5,1);
        sparseVector1.set(6,2);
        sparseVector1.set(9,1);
        sphericalCentroid.add(sparseVector1);
        System.out.println(sphericalCentroid.l2norm);
        sphericalCentroid.add(sparseVector1);
        System.out.println(sphericalCentroid.l2norm);


    */

    }


}
