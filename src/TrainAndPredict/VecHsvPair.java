package TrainAndPredict;

import jsat.linear.SparseVector;
import jsat.linear.Vec;

/**
 * Created by Cristian on 2017-04-07.
 */
public class VecHsvPair {

    SparseVector vector;
    int hsvCode;

    public VecHsvPair(SparseVector v, int hsvCode) {

        this.vector = v;
        this.hsvCode = hsvCode;

    }

    public VecHsvPair() {


    }

    public SparseVector getVector() {
        return vector;
    }

    public void setVector(SparseVector vector) {
        this.vector = vector;
    }

    public int getHsvCode() {
        return hsvCode;
    }

    public void setHsvCode(int hsvCode) {
        this.hsvCode = hsvCode;
    }
}
