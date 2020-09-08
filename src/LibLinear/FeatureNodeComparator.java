package LibLinear;

import de.bwaldvogel.liblinear.FeatureNode;

import java.util.Comparator;

public class FeatureNodeComparator implements Comparator<FeatureNode> {


    @Override
    public int compare(FeatureNode a, FeatureNode b) {

        if(a.index < b.index) return -1;
        if(a.index > b.index) return 1;
        return 0;
    }

}