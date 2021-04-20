package LibLinearMultiLabel;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import LibLinearMultiLabel.cc.fork.FeatureNode;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Play {


    public static void main(String[] arg) throws FileNotFoundException {

        List<TrainingPair> obj = new ArrayList<>();
        TrainingPair trainingPair = new TrainingPair("APA");

        trainingPair.setFeatureNodes( new FeatureNode[2] );
        obj.add( trainingPair );

        Kryo kryo = new Kryo();

        Output output = new Output(new FileOutputStream("file3.bin"));
        FeatureNode someObject = new FeatureNode(1,1);
        kryo.writeObject(output, obj);
        output.close();


    }
}
