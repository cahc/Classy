package Diva;

/**
 * Created by crco0001 on 2/13/2018.
 */
public class MatrixEntry implements Comparable<MatrixEntry>{

    int i;
    int j;
    double val;


    public MatrixEntry(int i, int j, double val) {

        this.i = i;
        this.j = j;
        this.val = val;

    }


    @Override
    public int compareTo(MatrixEntry other) {

        if (this.val > other.val) return -1;
        if (this.val < other.val) return 1;

        return 0;

    }


    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getJ() {
        return j;
    }

    public void setJ(int j) {
        this.j = j;
    }

    public double getVal() {
        return val;
    }

    public void setVal(double val) {
        this.val = val;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MatrixEntry that = (MatrixEntry) o;

        if (i != that.i) return false;
        if (j != that.j) return false;
        return Double.compare(that.val, val) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = i;
        result = 31 * result + j;
        temp = Double.doubleToLongBits(val);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
