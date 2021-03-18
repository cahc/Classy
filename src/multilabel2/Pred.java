package multilabel2;

import jsat.classifiers.CategoricalResults;
import jsat.classifiers.DataPoint;
import jsat.linear.Vec;

import java.util.List;

public interface Pred {

    CategoricalResults predict(DataPoint dp);
    CategoricalResults predict(Vec vec);

    public List<DataPoint> getTraningData();

    int numberOfLabels();


    String getLabel(int i);


}
