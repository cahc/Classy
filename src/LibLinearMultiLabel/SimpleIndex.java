package LibLinearMultiLabel;

import Database.FileHashDB;
import SwePub.HsvCodeToName;
import SwePub.Record;

import com.koloboke.collect.map.IntDoubleCursor;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.ObjIntCursor;
import com.koloboke.collect.map.hash.*;
import LibLinearMultiLabel.cc.fork.FeatureNode;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SimpleIndex implements Serializable {

    private static final long serialVersionUID = -473737364433488888L;

    private static int counter = 0;
    private HashObjIntMap<String> index = HashObjIntMaps.getDefaultFactory().withDefaultValue(-1).newMutableMap(10000);
    private IntIntMap IDF = HashIntIntMaps.getDefaultFactory().withDefaultValue(0).newMutableMap(10000);

    private HashIntDoubleMap indexToWeight; //optional

    public static Comparator<FeatureNode> sortFeatureNodes = new Comparator<FeatureNode>() {
        @Override
        public int compare(FeatureNode o1, FeatureNode o2) {

            if (o1.index < o2.index) return -1;
            if (o1.index > o2.index) return 1;
            return 0;

        }
    };


    public SimpleIndex() {
    }



    public TrainingPair getVectorForUnSeenRecord(Record record,String language) {


        List<String> terms = null;

        if("swe".equals(language)) {

            terms = record.getLanguageSpecificTerms("swe");
            terms.addAll(record.getTermsFromAffiliation());
            terms.addAll(record.getTermsFromHost());
            terms.addAll(record.getUnkontrolledKkeywords());
            String ISBN = record.getISBN();
            if (ISBN != null) terms.add(ISBN);
            terms.addAll(record.getIssn());


        } else {


            terms = record.getLanguageSpecificTerms("eng");
            terms.addAll(record.getTermsFromAffiliation());
            terms.addAll(record.getTermsFromHost());
            terms.addAll(record.getUnkontrolledKkeywords());
            String ISBN = record.getISBN();
            if (ISBN != null) terms.add(ISBN);
            terms.addAll(record.getIssn());


        }


        TrainingPair trainingPair = new TrainingPair(record.getMasterURI(), this.size());


        HashMap<Integer, Integer> indexToOccurance = new HashMap<>();

        for (String s : terms) {

            int inx = this.getIndice(s);

            if (inx != -1) {
                indexToOccurance.putIfAbsent(inx, 0);
                indexToOccurance.put(inx, indexToOccurance.get(inx) + 1); //obs indice start from 1 in FeatureNode but from 0 in simple Index
            }

        }

        //in other senarios the size could be zero
        FeatureNode[] featureNodes = new FeatureNode[indexToOccurance.size()];

        int incr = 0;
        for (Map.Entry<Integer, Integer> entry : indexToOccurance.entrySet()) {

            int index = entry.getKey() + 1; //index start at 1 not zero!

            int value = entry.getValue();

            featureNodes[incr] = new FeatureNode(index, value);
            incr++;

        }

        Arrays.sort(featureNodes, sortFeatureNodes);
        trainingPair.setFeatureNodes(featureNodes);


        return trainingPair; //no class info here

    }


    public boolean addRecord(Record record, int level, String language) {


        if (level == 5 && language.equals("eng")) {

            if (!(record.containsLevel5Classification() && record.isFullEnglishText())) return false;


            List<String> terms = record.getLanguageSpecificTerms("eng");
            terms.addAll(record.getTermsFromAffiliation());
            terms.addAll(record.getTermsFromHost());
            terms.addAll(record.getUnkontrolledKkeywords());
            String ISBN = record.getISBN();
            if (ISBN != null) terms.add(ISBN);
            terms.addAll(record.getIssn());
            addTokens(terms);

            return true;


        } else if (level == 3 && language.equals("eng")) {

            if (!(record.containsLevel3Classification() && record.isFullEnglishText())) return false;

            List<String> terms = record.getLanguageSpecificTerms("eng");
            terms.addAll(record.getTermsFromAffiliation());
            terms.addAll(record.getTermsFromHost());
            terms.addAll(record.getUnkontrolledKkeywords());
            String ISBN = record.getISBN();
            if (ISBN != null) terms.add(ISBN);
            terms.addAll(record.getIssn());
            addTokens(terms);

            return true;


        } else if (level == 5 && language.equals("swe")) {

            if (!(record.containsLevel5Classification() && record.isFullSwedishText())) return false;

            List<String> terms = record.getLanguageSpecificTerms("swe");
            terms.addAll(record.getTermsFromAffiliation());
            terms.addAll(record.getTermsFromHost());
            terms.addAll(record.getUnkontrolledKkeywords());
            String ISBN = record.getISBN();
            if (ISBN != null) terms.add(ISBN);
            terms.addAll(record.getIssn());
            addTokens(terms);

            return true;


        } else if (level == 3 && language.equals("swe")) {

            if (!(record.containsLevel3Classification() && record.isFullSwedishText())) return false;


            List<String> terms = record.getLanguageSpecificTerms("swe");
            terms.addAll(record.getTermsFromAffiliation());
            terms.addAll(record.getTermsFromHost());
            terms.addAll(record.getUnkontrolledKkeywords());
            String ISBN = record.getISBN();
            if (ISBN != null) terms.add(ISBN);
            terms.addAll(record.getIssn());
            addTokens(terms);

            return true;

        }


        return false;

    }

    public void addTokens(List<String> tokens) {

        Set<String> seenBeforeForCurrentRecord = new HashSet<>(50);


        for (String t : tokens) {

            int indice = this.index.getInt(t);

            if (indice == -1) {

                this.index.put(t, counter);
                this.IDF.addValue(counter, 1);
                counter++;
            } else {

                if (!seenBeforeForCurrentRecord.contains(t)) this.IDF.addValue(indice, 1);

            }

            seenBeforeForCurrentRecord.add(t);

        }


    }

    public void removeRareTerms(int minFreq) {


        System.out.println("Dimensionality before reduction of rare terms: " + this.index.size());


        for (ObjIntCursor<String> cur = index.cursor(); cur.moveNext(); ) {


            int indice = cur.value();

            int IDF = this.IDF.get(indice);

            if (IDF < minFreq) cur.remove();

        }


        System.out.println("Dimensionality after reduction of rare terms: " + this.index.size());


        HashObjIntMap<String> index2 = HashObjIntMaps.getDefaultFactory().withDefaultValue(-1).newMutableMap(10000);


        int dim = 0;

        for (ObjIntCursor<String> cur = index.cursor(); cur.moveNext(); ) {

            String term = cur.key();
            index2.put(term, dim);
            dim++;

        }


        //remapped from 0.. to maxDim
        this.index = index2;


        System.out.println("new index size: " + this.index.size());


    }

    public int getIDF(int indice) {

        return this.IDF.get(indice);
    }

    public int getIndice(String t) {

        return this.index.getInt(t);

    }

    public double getWeight(int indice) {

        return this.indexToWeight.get(indice);

    }

    public void saveWeightingSchemeOptional(String file) {


        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);


            HashMap<Integer, Double> serializeMap = new HashMap<>(this.indexToWeight.size());

            for (IntDoubleCursor cur = this.indexToWeight.cursor(); cur.moveNext(); ) {


                serializeMap.put(cur.key(), cur.value()); //index to weight

            }


            oos.writeObject(serializeMap);
            oos.close();
            fos.close();
            System.out.println("Serialized termWeights (HashMap) is saved in " + file);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }


    }

    public void save(String file) {

        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);


            HashMap<String, Integer> serializeMap = new HashMap<>(this.index.size());

            for (ObjIntCursor<String> cur = index.cursor(); cur.moveNext(); ) {


                serializeMap.put(cur.key(), cur.value());

            }


            oos.writeObject(serializeMap);
            oos.close();
            fos.close();
            System.out.println("Serialized Index (HashMap) is saved in " + file);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }


    }

    public void load(String file) {

        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);

            HashMap<String, Integer> serializeMap = (HashMap<String, Integer>) ois.readObject();

            this.index.clear();

            for (Map.Entry<String, Integer> entry : serializeMap.entrySet()) {


                this.index.put(entry.getKey(), entry.getValue().intValue());

            }


            ois.close();
            fis.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        } catch (ClassNotFoundException c) {
            System.out.println("Class not found");
            c.printStackTrace();
            return;
        }

        System.out.println("Deserialized Index (HashMap), mappings: " + this.index.size());
        this.counter = this.index.size() - 1;

    }


    public void loadTermWeightsOptional(String file) {

        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);

            HashMap<Integer, Double> serializeMap = (HashMap<Integer, Double>) ois.readObject();

            this.indexToWeight = HashIntDoubleMaps.getDefaultFactory().withDefaultValue(0).newMutableMap(this.index.size());


            for (Map.Entry<Integer, Double> entry : serializeMap.entrySet()) {


                this.indexToWeight.put(entry.getKey().intValue(), entry.getValue().doubleValue());

            }


            ois.close();
            fis.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        } catch (ClassNotFoundException c) {
            System.out.println("Class not found");
            c.printStackTrace();
            return;
        }

        System.out.println("Deserialized TermWeights (HashMap), mappings: " + this.indexToWeight.size());


    }

    public int size() {

        return this.index.size();
    }


    public String reverseLookupSlow(int indice) {


        for (ObjIntCursor<String> cur = index.cursor(); cur.moveNext(); ) {


            if (cur.value() == indice) return cur.key();
        }


        return null;

    }


    public ObjIntCursor<String> getMappingInIndex() {

        return this.index.cursor();


    }


    public TrainingPair getTrainingPair(List<String> tokens, Set<Integer> classLabels, String uri, int dim) {


        TrainingPair trainingPair = new TrainingPair(uri, dim);
        trainingPair.setClassLabels(classLabels);


        HashMap<Integer, Integer> indexToOccurance = new HashMap<>();

        for (String s : tokens) {

            int inx = this.getIndice(s);

            if (inx != -1) {
                indexToOccurance.putIfAbsent(inx, 0);
                indexToOccurance.put(inx, indexToOccurance.get(inx) + 1); //obs indice start from 1 in FeatureNode but from 0 in simple Index
            }

        }

        //in other senarios the size could be zero
        FeatureNode[] featureNodes = new FeatureNode[indexToOccurance.size()];

        int incr = 0;
        for (Map.Entry<Integer, Integer> entry : indexToOccurance.entrySet()) {

            int index = entry.getKey() + 1; //index start at 1 not zero!

            int value = entry.getValue();

            featureNodes[incr] = new FeatureNode(index, value);
            incr++;

        }

        Arrays.sort(featureNodes, sortFeatureNodes);
        trainingPair.setFeatureNodes(featureNodes);


        return trainingPair;


    }


    public void applyTermWeights(TrainingPair trainingPair) {

        if (trainingPair.dimensions != this.indexToWeight.size())
            System.out.println("Weighting scheme dimensions missmatch with trainingPair dimension!");

        FeatureNode[] nodes = trainingPair.getFeatureNodes();

        for (FeatureNode node : nodes) {

            int index = node.getIndex() - 1; //convert to zero-based
            double val = node.getValue();

            double weight = this.indexToWeight.get(index);

            double newVal = val * weight;

            node.setValue(newVal);

        }

    }

    public void addEntropyWeighting(List<TrainingPair> trainingPairList) throws IOException {

        if (trainingPairList.get(0).getDimensions() != this.index.size()) {

            System.out.println("Mismatch in vector space dimensions. Index is of size " + this.index.size() + " but exemplar report " + trainingPairList.get(0).getDimensions());
            return;

        }


        //freq distribution of a term over target classes
        ArrayList<HashIntDoubleMap> termToHashMapFreqInClass = new ArrayList<>(this.index.size());
        for (int i = 0; i < this.index.size(); i++)
            termToHashMapFreqInClass.add(HashIntDoubleMaps.getDefaultFactory().withDefaultValue(0).newMutableMap(10)); //initialize


        //size sum freq for a target class
        HashIntDoubleMap targetClassSize = HashIntDoubleMaps.getDefaultFactory().withDefaultValue(0).newMutableMap(300);


        //mapping index to final weight
        this.indexToWeight = HashIntDoubleMaps.getDefaultFactory().withDefaultValue(0).newMutableMap(this.index.size());

        for (TrainingPair trainingPair : trainingPairList) {

            Set<Integer> targetClasses = trainingPair.getClassLabels();

            //warning index starts at 0, vector starts at 1
            FeatureNode[] features = trainingPair.getFeatureNodes();

            for (FeatureNode node : features) {

                int index = node.index - 1; //convert 1 based to 0 based
                double value = node.value;

                for (Integer target : targetClasses) {

                    termToHashMapFreqInClass.get(index).addValue(target, value); //update freq for term (index) in class (target)

                    targetClassSize.addValue(target, value); //update class size
                }


            } //for each feature


        } //for each training pair


        // calculate f(t,c_i) / f(c_i) for t_i=0 and then balanced entropy


        double normFactor = Math.log(targetClassSize.size());

        for (int t = 0; t < this.index.size(); t++) { //for each term

            int n_classes = termToHashMapFreqInClass.get(t).size(); //number of classes that term t occurs in
            double[] prob = new double[n_classes];
            int idx = 0;

            for (IntDoubleCursor cur = termToHashMapFreqInClass.get(t).cursor(); cur.moveNext(); ) {

                prob[idx] = cur.value() / targetClassSize.get(cur.key()); //freq of term t in class / class size

                idx++;
            }

            //debug print
            //System.out.println("Prob for t" + Arrays.toString(prob) );


            double sum = 0;
            for (int i = 0; i < prob.length; i++) {

                sum = sum + prob[i];
            }

            //debug print
            //System.out.println("sum of prob for t" + sum );

            double weight = 0;

            for (int i = 0; i < prob.length; i++) {

                double ratio = prob[i] / sum;

                weight = weight + (ratio * Math.log(ratio));

            }

            //debug print
            //System.out.println("entropy balanced: " + weight );

            double final_w = 1 + (weight / normFactor);

            //debug print
            //System.out.println("Final weight: " + final_w);

            indexToWeight.put(t, final_w);

        }

        // System.out.println("term to class freq distribution_ " + termToHashMapFreqInClass.size());
        // System.out.println("KeySet:" +termToHashMapFreqInClass.get(364460).keySet());
        // System.out.println("value" + termToHashMapFreqInClass.get(364460).values());
        // System.out.println();
        // System.out.println("KeySet:" +termToHashMapFreqInClass.get(364479).keySet());
        // System.out.println("value" + termToHashMapFreqInClass.get(364479).values());
        // System.out.println();
        // System.out.println("KeySet:" +termToHashMapFreqInClass.get(this.index.size()-1).keySet());
        // System.out.println("value" + termToHashMapFreqInClass.get(this.index.size()-1).values());


        /*
                    for(IntDoubleCursor cur = targetClassSize.cursor(); cur.moveNext(); ) {

                System.out.println("target: " + cur.key() + " size: " + cur.value());

            }


         */


        /*
        BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( new File("weight_analysis1.txt")), StandardCharsets.UTF_8) );


            for(int t=0; t<this.index.size(); t++) {

                String term = reverseLookupSlow(t);
                double weight = indexToWeight.get(t);

                writer.write(term +"\t" + t +"\t" + weight + "\t" + termToHashMapFreqInClass.get(t).values().toString());
                writer.newLine();
            }


            writer.flush();
            writer.close();

*/

    }


    public static void main(String[] arg) throws IOException {


    }

}