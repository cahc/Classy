package DimensionReduction;

import jsat.linear.DenseVector;
import jsat.linear.SparseVector;

import java.util.List;

public class Project {



    public static DenseVector project(SparseVector input, List<DenseVector> axis, double regularization) {


        DenseVector projected = new DenseVector( axis.size() );

        for(int i=0; i<axis.size(); i++) {


            double val = input.dot( axis.get(i) );


           if(val >= regularization) projected.set(i ,  val   );
        }


        return projected;

    }

}
