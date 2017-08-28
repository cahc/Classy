package ClusterAnalysis.SignificantTerms;

/**
 * Created by crco0001 on 8/28/2017.
 */
public class TermAndCorr implements Comparable<TermAndCorr> {

    String term;
    double corr_with_lable;
    int TP;
    int TN;
    int FN;
    int FP;


    public TermAndCorr(String s, double corr, int TP, int TN,int FN,int FP) {

        this.term = s;
        this.corr_with_lable = corr;

        this.TP = TP;
        this.TN = TN;
        this.FN = FN;
        this.FP = FP;


    }



    @Override
    public int compareTo(TermAndCorr other) {

        if(this.corr_with_lable > other.corr_with_lable) return 1;
        if(this.corr_with_lable < other.corr_with_lable) return -1;

        return 0;

    }

    public double getCorr_with_lable() {
        return corr_with_lable;
    }

    public void setCorr_with_lable(double corr_with_lable) {
        this.corr_with_lable = corr_with_lable;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    @Override
    public String toString() {

        return this.term + " " + this.corr_with_lable +" TP=" + this.TP + " TN=" + this.TN + " FN=" + this.FN + " FP=" + this.FP;

    }
}
