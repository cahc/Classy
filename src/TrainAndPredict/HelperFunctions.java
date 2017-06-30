package TrainAndPredict;

import Database.IndexAndGlobalTermWeights;
import jsat.classifiers.CategoricalData;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.Classifier;
import jsat.io.JSATData;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Cristian on 2016-12-06.
 */
public class HelperFunctions {


    public static ClassificationDataSet readJSATdata(String name) throws IOException {

        System.out.println("Reading JSAT dataset");

        BufferedInputStream inputStream = new BufferedInputStream( new FileInputStream(name) );

        ClassificationDataSet cds = JSATData.loadClassification(inputStream);

        System.out.println("Read: Sample Size: " + cds.getSampleSize() + " classSize: " + cds.getClassSize() );



        return cds;

    }

    public static void writeJSATdata(ClassificationDataSet cds, String name) throws IOException {


        System.out.println("Writing JSAT dataset");

        BufferedOutputStream  outputStream = new BufferedOutputStream(( new FileOutputStream((name) )));


        JSATData.writeData(cds,outputStream);

        outputStream.flush();
        outputStream.close();



    }


    public static void writeSerializedClassifier(Classifier classifier, String name) throws IOException {


        FileOutputStream fos = new FileOutputStream(name);
        ObjectOutputStream oos = new ObjectOutputStream(fos);

        oos.writeObject(classifier);

        oos.flush();
        oos.close();

    }


    public static Classifier readSerializedClassifier(String name) throws IOException, ClassNotFoundException {

        FileInputStream fis = new FileInputStream(name);
        ObjectInputStream ois = new ObjectInputStream(fis);

        Classifier classifier = (Classifier)ois.readObject();

        return classifier;
    }



}