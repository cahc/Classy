package SimpleTextAnalysis;

import jsat.linear.SparseVector;

/**
 * Created by Cristian on 2017-05-04.
 */
public class VectorWithSimValue implements Comparable<VectorWithSimValue> {

    public double sim;
    Integer vectorID;
    String uri;

    public SparseVector v;


    public VectorWithSimValue(SparseVector v, double sim, Integer vectorID) {

        this.sim = sim;
        this.v = v;
        this.vectorID = vectorID;
    }


    public VectorWithSimValue(SparseVector v, double sim, Integer vectorID, String uri) {

        this.sim = sim;
        this.v = v;
        this.vectorID = vectorID;
        this.uri = uri;
    }


    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public SparseVector getV() {
        return v;
    }

    public void setV(SparseVector v) {
        this.v = v;
    }

    public double getSim() {
        return sim;
    }

    public void setSim(double sim) {
        this.sim = sim;
    }

    public Integer getVectorID() {
        return vectorID;
    }

    public void setVectorID(Integer vectorID) {
        this.vectorID = vectorID;
    }

    @Override
    public int compareTo(VectorWithSimValue other) {

        if(this.sim > other.sim) return -1;
        if(this.sim < other.sim) return 1;

        return 0;


    }



}
