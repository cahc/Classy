package Diva;

import jsat.classifiers.DataPoint;
import jsat.linear.SparseVector;
import jsat.linear.Vec;

/**
 * Created by crco0001 on 8/17/2017.
 */
public class VectorWithID {

    int id;
    SparseVector vec;


    public VectorWithID(SparseVector vec, int ID) {

        this.id = ID;
        this.vec = vec;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SparseVector getVec() {
        return vec;
    }

    public void setVec(SparseVector vec) {
        this.vec = vec;
    }
}
