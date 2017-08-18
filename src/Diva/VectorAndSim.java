package Diva;

import jsat.linear.SparseVector;

/**
 * Created by crco0001 on 8/17/2017.
 */
public class VectorAndSim implements Comparable<VectorAndSim> {

    public double sim;
    public VectorWithID v;


    public VectorAndSim(VectorWithID v, double sim) {

        this.sim = sim;
        this.v = v;

    }

    public VectorWithID getV() {
        return v;
    }

    public void setV(VectorWithID v) {
        this.v = v;
    }

    public double getSim() {
        return sim;
    }

    public void setSim(double sim) {
        this.sim = sim;
    }

    public int getVectorID() {
        return this.v.getId();
    }


    @Override
    public int compareTo(VectorAndSim other) {

        if (this.sim > other.sim) return -1;
        if (this.sim < other.sim) return 1;

        return 0;


    }


}