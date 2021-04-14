package multilabel2;

import Database.FileHashDB;
import Database.JsonSwePubParser;
import SwePub.HsvCodeToName;
import SwePub.Record;
import com.koloboke.collect.map.ObjIntCursor;
import jsat.classifiers.CategoricalData;
import jsat.classifiers.CategoricalResults;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.DataPoint;
import jsat.classifiers.linear.LogisticRegressionDCD;
import jsat.linear.IndexValue;
import jsat.linear.SparseVector;
import mulan.classifier.MultiLabelOutput;
import mulan.evaluation.GroundTruth;
import mulan.evaluation.measure.*;

import java.io.*;
import java.util.*;

public class MultiLabelHSV {


    public static List<String> getFeaturesFromRecords(Record record, String language) {

        List<String> terms = record.getLanguageSpecificTerms(language);
        terms.addAll( record.getTermsFromAffiliation() );
        terms.addAll( record.getTermsFromHost()  );
        terms.addAll( record.getUnkontrolledKkeywords() );
        String ISBN = record.getISBN();
        if(ISBN != null) terms.add( ISBN  );
        terms.addAll( record.getIssn());

        return terms;

    }


    public static void saveDataPointList(List<DataPoint> dataPointList, String file) {

        try
        {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);




            oos.writeObject(dataPointList);
            oos.close();
            fos.close();
            System.out.println("Serialized datapoint list  in " + file);
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
        }


    }

    public static List<DataPoint> load(String file) {

        List<DataPoint> dataPointList = null;
        try
        {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);

         dataPointList = (List<DataPoint>)ois.readObject();



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

        System.out.println("Deserialized datapointList, #: " + dataPointList.size() );

        return dataPointList;

    }




    public static void main(String[] arg) throws IOException, InterruptedException {

        /////////////////////////////////////////////////////
                    //PARSE RECORDS AND SAVE TO DB//
        /////////////////////////////////////////////////////

        /*
        FileHashDB fileHashDB = new FileHashDB();
        fileHashDB.setPathToFile("E:\\Desktop\\JSON_SWEPUB\\SWEPUB20210408.db");
        fileHashDB.create();

        JsonSwePubParser jsonSwePubParser = new JsonSwePubParser("E:\\Desktop\\JSON_SWEPUB\\swepub-deduplicated-2021-04-08.jsonl");
        jsonSwePubParser.parse(fileHashDB);
        */

        /////////////////////////////////////////////////////////////
        ////CREATE INDEX DEPENDENT ON CLASSIFIER LEVEL AND LANGUAGE//
        ////////////////////////////////////////////////////////////


        Index index = new Index();

        FileHashDB fileHashDB2 = new FileHashDB();
        fileHashDB2.setPathToFile("/Users/cristian/Desktop/JSON_SWEPUB/SwePubJson.db");
        fileHashDB2.createOrOpenDatabase();
        int total = 0;
        for (Map.Entry<Integer, Record> entry : fileHashDB2.database.entrySet()) {

            Record record = entry.getValue();

            if( index.addRecord(record,5,"eng") ) total++; // ony adds if it contains pair of language and level


        }


        System.out.println("total records used: " + total + " index size: " + index.size());
        index.removeRareTerms(3);
        System.out.println("IDF ind 0: " + index.getIDF(0) + " " + index.reverseLookupSlow(0));
        index.save("/Users/cristian/Desktop/JSON_SWEPUB/SwePubJsonIndexLevel5LangEng.ser");


        /*

        total records used: 465996 index size: 1455585
        Dimensionality before reduction of rare terms: 1455585
        Dimensionality after reduction of rare terms: 356944
        new index size: 356944
        IDF ind 0: 14067 TE@résidence
        Serialized Index (HashMap) is saved in /Users/cristian/Desktop/JSON_SWEPUB/SwePubJsonIndexLevel5LangEng.ser

         */




        fileHashDB2.closeDatabase();


        index = new Index();
        index.load("/Users/cristian/Desktop/JSON_SWEPUB/SwePubJsonIndexLevel5LangEng.ser");

        boolean balanced = false;
        boolean addSmothing = false;
        double bias = 0.05;

        TermWeight termWeight = new TermWeight(balanced,addSmothing,bias);



        fileHashDB2 = new FileHashDB();
        fileHashDB2.setPathToFile("/Users/cristian/Desktop/JSON_SWEPUB/SwePubJson.db");
        fileHashDB2.createOrOpenDatabase();

        List<SparseVector> sparseVectors = new ArrayList<>();
        List<Set<Integer>> classCodes = new ArrayList<>();

        for (Map.Entry<Integer, Record> entry : fileHashDB2.database.entrySet()) {

            Record record = entry.getValue();

            if( !(record.isFullEnglishText() && record.containsLevel5Classification()) ) continue;

            List<String> features = getFeaturesFromRecords(record,"eng");

            SparseVector sparseVector = index.getSparseVector(features , false );

            Set<Integer> codes = new HashSet<>(5);


            for(Integer i : record.getClassificationCodes() ) {

                Integer level5Code = HsvCodeToName.firstFiveDigitsOrNull(i);

                if(level5Code != null) codes.add(level5Code);

            }
            classCodes.add(codes);
            sparseVectors.add(sparseVector);

        }


        Set<Integer> integerSet = new HashSet<>();

        for(Set<Integer> s : classCodes) {

            integerSet.addAll(s);
        }

        List<Integer> targeClasses = new ArrayList<>(integerSet);
        Collections.sort(targeClasses);



        final CategoricalData[] targetCategories = new CategoricalData[targeClasses.size()];

        for(int i=0; i<targeClasses.size(); i++) {

            targetCategories[i] = new CategoricalData(2);
            targetCategories[i].setCategoryName( targeClasses.get(i).toString() );
        }

        System.out.println("Number of classes: "+ targeClasses.size());

        List<DataPoint> dataPointList = new ArrayList<>();

        for(int i=0; i< sparseVectors.size(); i++) {

            SparseVector v = sparseVectors.get(i);
            Set<Integer> codes = classCodes.get(i);


            int[] classIndicatorVector = new int[targeClasses.size()];

            for(int j=0; j<targeClasses.size(); j++ ) {

                if( codes.contains( targeClasses.get(j) ) ) classIndicatorVector[j] = 1;

            }


           dataPointList.add( new DataPoint(v,classIndicatorVector,targetCategories) );

        }

        saveDataPointList(dataPointList,"/Users/cristian/Desktop/JSON_SWEPUB/SwePubJsonDataPointsNOWEIGHTNONORMLevel5LangEng.ser"); //try LDA on this?


        System.out.println("Datapoint list size: "+ dataPointList.size());

        //TERMWEIGHTING AND NORMALIZING
        System.out.println("Term weighting and L2-normalizing");

        termWeight.parse(dataPointList);

        for (DataPoint i : dataPointList) {

            SparseVector vec = (SparseVector) i.getNumericalValues();

            Iterator<IndexValue> iter = vec.getNonZeroIterator();

            while (iter.hasNext()) {

                IndexValue indexValue = iter.next();
                double weight = termWeight.getWeight(indexValue.getIndex());
                vec.set(indexValue.getIndex(), weight *  indexValue.getValue() );

            }

            vec.normalize();

        }

        termWeight.saveToFile("/Users/cristian/Desktop/JSON_SWEPUB/SwePubJsonTermWeightLevel5LangEng.ser");
        saveDataPointList(dataPointList,"/Users/cristian/Desktop/JSON_SWEPUB/SwePubJsonDataPointsLevel5LangEng.ser");

        
        System.out.println("Started training..");
        long start = System.currentTimeMillis();


        LogisticRegressionDCD base = new LogisticRegressionDCD(3.0,500);
        base.setUseBias(true);
        MultiLabel multiLabelClassifier = new MultiLabel(base,true,dataPointList);
        System.out.println("Training multi-label classifier. # labels: " + multiLabelClassifier.numberOfLabels());
        multiLabelClassifier.train(0.15);
       long stop = System.currentTimeMillis();

        multiLabelClassifier.save("/Users/cristian/Desktop/JSON_SWEPUB/SwePubJsonClassifierLevel5LangEng.ser");

        System.out.println("Training took:  " + (stop - start) / 1000.0 + "seconds");



        ExampleBasedPrecision examplePrecision = new ExampleBasedPrecision();
        ExampleBasedAccuracy exampleAccuracy = new ExampleBasedAccuracy();
        ExampleBasedRecall exampleBasedRecall = new ExampleBasedRecall();
        ExampleBasedFMeasure exampleBasedFMeasure  = new ExampleBasedFMeasure();


        MacroFMeasure macroF = new MacroFMeasure( multiLabelClassifier.numberOfLabels() ); //label-based
  //      MicroFMeasure microF = new MicroFMeasure( multiLabelClassifier.numberOfLabels() );




        for(DataPoint dp : dataPointList) {

            CategoricalResults cr = multiLabelClassifier.predict(dp);
            MultiLabelOutput predictions = Evaluate.multiLabelOutput(cr,false); //above 0.5, todo crossvalidate
            GroundTruth groundTruth = Evaluate.groundTruth( dp.getCategoricalValues() );

            //measures
            examplePrecision.update(predictions,groundTruth);
            exampleAccuracy.update(predictions,groundTruth);
            exampleBasedRecall.update(predictions,groundTruth);
            exampleBasedFMeasure.update(predictions,groundTruth);

            macroF.update(predictions,groundTruth);
          //  microF.update(predictions,groundTruth);

        }


        System.out.println("Example-based Accuracy: institution classifier: " + String.format("%.3f",exampleAccuracy.getValue()));
        System.out.println("Example-based Precision: institution classifier: " + String.format("%.3f",examplePrecision.getValue()));
        System.out.println("Example-based Recall: institution classifier: " + String.format("%.3f",exampleBasedRecall.getValue()));
        System.out.println("Example-based F-measure: institution classifier: " + String.format("%.3f",exampleBasedFMeasure.getValue()));
        System.out.println("MacroF: institution classifier: " + String.format("%.3f",macroF.getValue()));
      //  System.out.println("MicroF: " + String.format("%.3f",microF.getValue()));



        fileHashDB2.closeDatabase();
    }



}
