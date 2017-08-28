package Visualization;

/**
 * Created by crco0001 on 8/28/2017.
 */
public class VOSNode {

    int id;
    String lable;
    double x;
    double y;
    int cluster;



    //id and clusters are 1:based
    public VOSNode() {} ;


    public VOSNode(int id, String lable, double x, double y, int cluster) {

        this.id =id; this.lable =lable; this.x =x; this.y =y; this.cluster = cluster;

    }


    public int getId() {
        return id;
    }

    public String getLable() {
        return lable;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getCluster() {
        return cluster;
    }

    public void setId(int id) {

        this.id = id;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setCluster(int cluster) {
        this.cluster = cluster;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VOSNode node = (VOSNode) o;

        if (id != node.id) return false;
        if (Double.compare(node.x, x) != 0) return false;
        if (Double.compare(node.y, y) != 0) return false;
        if (cluster != node.cluster) return false;
        return lable.equals(node.lable);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + lable.hashCode();
        temp = Double.doubleToLongBits(x);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + cluster;
        return result;
    }
}
