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

    private static final double overflowWarning = Double.MAX_VALUE*0.95;

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
        this.vector.normalize();


        this.S = 1.0D;
        this.l2norm = 1.0D;

    }


    public void add(SparseVector sparseVector, double learningRate) {


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

        //step 2


       if(this.l2norm != 0)   { this.vector.mutableAdd(this.l2norm * learningRate,sparseVector); } else { this.vector.mutableAdd(sparseVector); }

        //step 3

        double addToS = 0;

        for(int i=0; i<nonzeroIndices.length; i++) {

            addToS = addToS + Math.pow( this.vector.get( nonzeroIndices[i] ), 2.0D);

        }

        this.S = this.S + addToS;


        //step 4, uppdate the l2-norm

        this.l2norm = Math.sqrt( this.S );


        //normalize when things are close to overflow
        if(this.S >= this.overflowWarning) {


            this.vector.mutableDivide(this.l2norm);
            this.l2norm = 1.0D;
            this.S = 1.0D;

          //  System.out.println("Normalized to avoid overflow");
        }




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
        DenseVector denseVector = new DenseVector(dim);




        System.out.println("Deferred normalization (fast)");
        for(int i=10000; i<30000; i++ ) {

            sphericalCentroid.add(  (SparseVector)vecList.get(i), 1.0D );

        }




        System.out.println("Eager normalization (slow)");
        for(int i=10000; i<30000; i++ ) {

            denseVector.mutableAdd(  vecList.get(i) );
            denseVector.normalize();

        }

        System.out.println(Double.MAX_VALUE*0.95);

        System.out.println("Should be 1.0: " + denseVector.dot( sphericalCentroid.vector  ) / sphericalCentroid.l2norm );

        System.out.println(denseVector.dot(vecList.get(10) ));

        System.out.println(sphericalCentroid.similarity( (SparseVector)vecList.get(10)  ));



    }


}
