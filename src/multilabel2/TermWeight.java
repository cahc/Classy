package multilabel2;

import jsat.classifiers.DataPoint;
import jsat.linear.IndexValue;
import jsat.linear.SparseVector;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TermWeight implements Serializable {

    private static final long serialVersionUID = -173537363433488988L;


    //(balanced) distributional concentration
    //https://ieeexplore.ieee.org/stamp/stamp.jsp?tp=&arnumber=7372153&tag=1


    //Balancing Between Over-Weighting and Under-Weighting in Supervised Term Weighting
    //https://arxiv.org/pdf/1604.04007.pdf
    //add-one smothing + bias term

    private boolean useBlancedConcentration;
    private double bias;
    private boolean addSmothing;

    private int nrLabels;

    private HashMap<Integer,Double> termToWeight;

    public Double getWeight(Integer index, Double bias) {


        Double weight = this.termToWeight.get(index);

        if(weight == null) return null;

        return regularizeEntropyWeight(weight,bias);
    }

    public Double getWeight(Integer index) {

        Double weight = this.termToWeight.get(index);

        if(weight == null) return null;

        return regularizeEntropyWeight(weight,this.bias);

    }


    public int getNrLabels() {

        return this.nrLabels;
    }
    public TermWeight(boolean useBlancedConcentration, boolean addSmothing, double bias) {

        this.useBlancedConcentration = useBlancedConcentration;
        this.bias = bias;
        this.addSmothing = addSmothing;

    }


    public static double regularizeEntropyWeight(double weight, double bias) {

        //weight [0,1]
        //bias [0,1] (0= no reg), try perhaps around 0.15

        return bias + (1-bias)*weight;
    }


    public void parse(List<DataPoint> dataPoints) {

        this.nrLabels = dataPoints.get(0).getCategoricalValues().length;

        //for(int i=0; i<this.nrLabels; i++) System.out.println( classifier.getLabel(  i   ) );

        //we need to calculate, for each term t
        //f(t,C_i)
        //f(C_i)
        //p(t|C_i) =  f(t,C_i) /f(C_i)

        // t -> sparse vector get(i) = freq of term t in label i

        HashMap<Integer, SparseVector> termToFreqDist = new HashMap<>();
        SparseVector lableSizes = new SparseVector(this.nrLabels,this.nrLabels);



        for(DataPoint point : dataPoints) {


            SparseVector vec = (SparseVector)point.getNumericalValues();

            for(int C_i=0; C_i <this.nrLabels; C_i++ ) {

                if(0 == point.getCategoricalValue(C_i)) continue;

                Iterator<IndexValue> iter = vec.getNonZeroIterator();

                while(iter.hasNext()) {


                    IndexValue indexValue = iter.next();
                    int t = indexValue.getIndex(); //todo assume boolean vector, so 1

                    SparseVector freq = termToFreqDist.get(t);

                    if(freq == null) {

                        freq = new SparseVector(this.nrLabels,this.nrLabels);
                        freq.set(C_i,1);
                        termToFreqDist.put(t,freq);

                    } else {

                        double old = freq.get(C_i);
                        freq.set(C_i,(old+1) );

                    }

                    //f(C_i)
                    lableSizes.set(C_i,lableSizes.get(C_i)+1);

                }


            } //for each label=1 (often just one, but can be multi-label



        } // for each data point



        //obs breaking sparsity if we do it like this, not efficient or nessesary, todo make it better
        if(this.addSmothing) {

            for(Map.Entry<Integer,SparseVector> entry : termToFreqDist.entrySet()) {

                SparseVector vec = entry.getValue();

                for(int i=0; i<vec.length(); i++) {

                    vec.set(i, (vec.get(i)+0.5)  );
                    lableSizes.set(i, lableSizes.get(i)+0.5 );

                }

            }

        } //add one smothing ends



        //this is the balanced distributional concentration
        //p(t|C_i) =  f(t,C_i) /f(C_i)

        //this is the distributional concentration
        //f(t,C_i) /f(t)


        for(Map.Entry<Integer,SparseVector> entry : termToFreqDist.entrySet()) {

            if(this.useBlancedConcentration) {

                SparseVector distoverLables = entry.getValue();
                SparseVector distoverLablesNormed = new SparseVector(this.nrLabels, this.nrLabels);

                Iterator<IndexValue> iter = distoverLables.getNonZeroIterator();

                while (iter.hasNext()) {


                    IndexValue indexValue = iter.next();

                    int C_i = indexValue.getIndex();
                    double C_i_size = lableSizes.get(C_i);
                    double freq = indexValue.getValue();

                    distoverLablesNormed.set(C_i, (freq / C_i_size));

                }

                entry.setValue(distoverLablesNormed);


            } else {

                SparseVector distoverLables = entry.getValue();

                distoverLables.mutableDivide( distoverLables.sum()  );

            }

        }



        HashMap<Integer,Double> termToSum = new HashMap<>();

        for(Map.Entry<Integer,SparseVector> entry : termToFreqDist.entrySet()) {

            termToSum.put(entry.getKey(), entry.getValue().sum() );
        }


        //calculate weights

        HashMap<Integer,Double> termToWeight = new HashMap<Integer, Double>();
        double normFactor = Math.log(this.nrLabels);

        for(Map.Entry<Integer,SparseVector> entry : termToFreqDist.entrySet()) {

            int t = entry.getKey();
            double t_sum = termToSum.get(t);

            double BH = 0;

            Iterator<IndexValue> iter = entry.getValue().getNonZeroIterator();
            while (iter.hasNext()) {

                IndexValue indexValue = iter.next();

                BH = BH + ( (indexValue.getValue()/t_sum) * Math.log(indexValue.getValue()/t_sum ) ); // Log(0) cannot happen

            }


            termToWeight.put(t,  1+(BH/normFactor) );

        }


        this.termToWeight = termToWeight;



    }


    public void saveToFile(String file) {


        try
        {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
            fos.close();
            System.out.println("TermWeights (HashMap) is saved in " + file);
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
        }


    }


    public static TermWeight load(String file) {

        TermWeight termWeight;
        try
        {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            termWeight = (TermWeight) ois.readObject();
            ois.close();
            fis.close();
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
            return null;
        }catch(ClassNotFoundException c)
        {
            System.out.println("Class not found");
            c.printStackTrace();
            return null;
        }

        System.out.println("Deserialized TermWeights (HashMap), mappings: " + termWeight.termToWeight.size() );
        return termWeight;
    }

    public static SparseVector weightVector(SparseVector vec, TermWeight termWeight) {


            SparseVector newVec = new SparseVector(vec.length(),vec.nnz());

            Iterator<IndexValue> iter = vec.getNonZeroIterator();

            while (iter.hasNext()) {

                IndexValue indexValue = iter.next();

                Double weight = termWeight.getWeight(indexValue.getIndex());
                if(weight == null) {

                    weight = 0D;
                }
                double origValue = indexValue.getValue();
                int origIndice = indexValue.getIndex();

                newVec.set(origIndice, (weight *  origValue) );

            }


            return newVec;
    }




    public static void main(String[] arg) {


      MultiLabel classifierFaculty = MultiLabel.load("/Users/cristian/Desktop/adr_scopus/multiLabelFaculties.ser");
      //SingleLabel classifierFaculty = SingleLabel.load("/Users/cristian/Desktop/adr_scopus/binaryLabelUniversity.ser");

      TermWeight termWeight = new TermWeight(false,true,0.05);
      termWeight.parse(classifierFaculty.getTraningData());


        Index index = new Index();
        index.load("/Users/cristian/Desktop/adr_scopus/index.ser");

        Integer ind = index.getIndice("sociology");

        System.out.println("Term weight: " + termWeight.getWeight(ind,0D) + " " + index.reverseLookupSlow(ind));
        System.out.println("term weight with bias: " + termWeight.getWeight(ind) );


        termWeight.saveToFile("/Users/cristian/Desktop/adr_scopus/termWeightsFaculty.ser");

       // TermWeight temp = TermWeight.load("/Users/cristian/Desktop/adr_scopus/termWeightsFaculty.ser");


    }
}
