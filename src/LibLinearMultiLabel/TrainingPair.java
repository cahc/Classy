package LibLinearMultiLabel;
import LibLinearMultiLabel.cc.fork.FeatureNode;


import java.io.*;
import java.util.*;

public class TrainingPair implements Serializable {

    private static final long serialVersionUID = -473732364433418818L;
    private String uri;
    private FeatureNode[] featureNodes; //vector
    private Set<Integer> classLabels; //multi-label

    int dimensions; //feature space size


    public TrainingPair() {} //for cryo serializer

    public TrainingPair(String uri, int dimensions) {

        this.uri = uri;
        this.dimensions = dimensions;

    }


    public String getUri() {
        return uri;
    }


    public int getDimensions() {
        return dimensions;
    }



    public void L2normalize() {

        double norm = 0;

        for(FeatureNode featureNode : this.featureNodes) norm = norm +Math.pow(featureNode.value,2);

        norm = Math.sqrt(norm);

        for(FeatureNode featureNode : this.featureNodes) featureNode.setValue(   featureNode.value/norm  );

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



    public static void save(List<TrainingPair> trainingPairList, String file) {

        try
        {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);


            oos.writeObject(trainingPairList);
            oos.close();
            fos.close();
            System.out.println("Serialized TrainingPair (List) to " + file);
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
        }


    }

    public static List<TrainingPair> load(String file) {

        List<TrainingPair> trainingPairList = null;
        try
        {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);

            trainingPairList= (List<TrainingPair>)ois.readObject();


            ois.close();
            fis.close();
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
            return Collections.emptyList();
        }catch(ClassNotFoundException c)
        {
            System.out.println("Class not found");
            c.printStackTrace();
            return Collections.emptyList();
        }

        System.out.println("Deserialized TrainingPair (List), size: " + trainingPairList.size() );

        return trainingPairList;

    }


    public static void main(String[] arg) throws IOException, ClassNotFoundException {

       List<TrainingPair> trainingPairList =  TrainingPair.load("E:\\Desktop\\JSON_SWEPUB\\multiLabelExperiment\\TrainingPairsEngLevel5.ser");

       for(int i=0; i<10; i++) {



           System.out.println(trainingPairList.get(i));
       }


    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainingPair that = (TrainingPair) o;
        return uri.equals(that.uri) &&
                classLabels.equals(that.classLabels);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri, classLabels);
    }
}
