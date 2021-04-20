package LibLinearMultiLabel;

import LibLinearMultiLabel.cc.fork.FeatureNode;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;




import java.io.*;
import java.util.*;

public class TrainingPair implements Serializable {

    private static final long serialVersionUID = -473732364433418818L;
    private String uri;
    private FeatureNode[] featureNodes; //vector
    private Set<Integer> classLabels; //multi-label


    public TrainingPair() {} //for cryo serializer

    public TrainingPair(String uri) {

        this.uri = uri;

    }

    public FeatureNode[] getFeatureNodes() {
        return featureNodes;
    }

    public void setFeatureNodes(FeatureNode[] featureNodes) {
        this.featureNodes = featureNodes;
    }

    public Set<Integer> getClassLabels() {
        return classLabels;
    }

    public void addClassLabels(Integer classLabel) {
        if(this.classLabels == null) { this.classLabels = new HashSet<>(5);
        }
        this.classLabels.add(classLabel);
    }

    public void setClassLabels(Set<Integer> classLabels) {
        this.classLabels = classLabels;
    }

    @Override
    public String toString() {
        return "TrainingPair{" +
                "uri='" + uri + '\'' +
                ", featureNodes=" + Arrays.toString(featureNodes) +
                ", classLabels=" + classLabels +
                '}';
    }



    public static void save(List<TrainingPair> trainingPairs, String file) throws IOException {


        Kryo kryo = new Kryo();

        Output output = new Output(new FileOutputStream(file));
        kryo.writeObject(output, trainingPairs);
        output.flush();
        output.close();

        System.out.println("Serialized " + trainingPairs.size() +" TrainingPairs (List) to " + file);


    }

    public static List<TrainingPair> load(String file) throws IOException, ClassNotFoundException {

        // ### Restore from disk...
        Kryo kryo = new Kryo();
        Input input = new Input(new FileInputStream(file));
        List<TrainingPair> someObject = kryo.readObject(input, ArrayList.class);
        input.close();

       return someObject;

    }


    public static void main(String[] arg) throws IOException, ClassNotFoundException {

        TrainingPair.load("E:\\Desktop\\JSON_SWEPUB\\multiLabelExperiment\\TrainingPairsEngLevel5.ser");


    }


}
