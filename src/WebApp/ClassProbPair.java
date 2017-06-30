package WebApp;

/**
 * Created by Cristian on 2017-04-18.
 */
public class ClassProbPair implements Comparable<ClassProbPair> {

    public int classCode = -1;
    public double probability = 0;


    public ClassProbPair(int code, double probability) {

        this.classCode = code;
        this.probability = probability;

    }

    public ClassProbPair() {}


    public int getClassCode() {
        return classCode;
    }

    public void setClassCode(int classCode) {
        this.classCode = classCode;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    @Override
    public int compareTo(ClassProbPair other) {


        if(other.probability > this.probability) return -1;
        if(this.probability > other.probability) return 1;
        return 0;

    }


    @Override
    public String toString() {

        return this.classCode + "--> " + this.probability;
    }


}
