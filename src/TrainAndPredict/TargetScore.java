package TrainAndPredict;

/**
 * Created by Cristian on 2016-12-14.
 */
public class TargetScore implements Comparable<TargetScore> {

    double score;
    int target;

    public TargetScore(double score, int target) {
        this.score = score;
        this.target = target;
    }

    public TargetScore() {}


    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TargetScore that = (TargetScore) o;

        if (Double.compare(that.score, score) != 0) return false;
        return target == that.target;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(score);
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + target;
        return result;
    }


    @Override
    public String toString() {
        return "TargetScore{" +
                "score=" + score +
                ", target=" + target +
                '}';
    }

    public int compareTo(TargetScore other) {

        if(this.score > other.score) return 1;
        if(this.score < other.score) return -1;
        return 0;

    }



}
