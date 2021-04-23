package SimpleTextAnalysis;

import SwePub.Record;



import java.io.*;
import java.util.*;

/**
 * Created by Cristian on 2017-05-09.
 */
public class DocToCluster {

    //docID to cluster lable

    private Integer nrDocs;

    private HashSet<String> allDocIds = new HashSet<>();

    private HashMap<String,Integer> docLabelToClusterNr = new HashMap<>();

    //lookup cluster nr to get all docsLabels
    private HashMap<Integer,Set<String>> clusterLableToDocs = new HashMap<>();

    //lookup a term to get all doc Labels containing this term
    private HashMap<String,Set<String>> termToDocs = new HashMap<>();

    //lable to set of terms
    private HashMap<Integer,Set<String>> clusterToTermSet = new HashMap<>();


    public DocToCluster(String vosNetFile) throws IOException {


        File file = new File(vosNetFile);
        if(!file.exists()) {System.out.println("DocToLableFile dosent exist"); System.exit(0); }

        BufferedReader reader = new BufferedReader( new FileReader( file ));

        String line;
        int count = 0;
        while(  (line = reader.readLine() ) != null    ) {

            String[] parts = line.split("\t");

            if(count == 0) {

                if(!"cluster".equals(parts[4])) {System.out.println("wrong file.."); System.exit(0); }

                count++;
                continue;
            }

           // System.out.println(parts[1] +" " +  parts[4]);

            Integer lable = null;

            try {
            lable = Integer.valueOf(parts[4]);

            } catch (NumberFormatException e) {

                System.out.println("Warning! " + parts[1] + " not in any cluster!");
            }

            if(lable != null) docLabelToClusterNr.put( parts[1], Integer.valueOf(parts[4]) );

        }


        reader.close();

        System.out.println("Nr docs read from file: " + docLabelToClusterNr.size());
    }


    public Integer getClusterId(String docLabel) {

        return docLabelToClusterNr.get(docLabel);


    }


    public Set<Map.Entry<String,Integer>> getEntrySetDocToClusterLable() {

       return docLabelToClusterNr.entrySet();

    }


    public void addRecord(Record r, String lang) {

        //1. lable to -> doc ids

        String docLabel = r.getURI();
        this.allDocIds.add(docLabel);

        Integer clusterNR = docLabelToClusterNr.get( docLabel );

        if( this.clusterLableToDocs.containsKey(clusterNR) ) {

            this.clusterLableToDocs.get(clusterNR).add( r.getURI() );

        } else {

            HashSet newSetOfDocIDs = new HashSet();
            newSetOfDocIDs.add( docLabel );

            this.clusterLableToDocs.put(clusterNR,newSetOfDocIDs);

        }

        // term to doc ids

        List<String> terms = r.getLanguageSpecificTerms(lang);
        List<String> keywords = r.getUnkontrolledKkeywords();
        String host = r.getHostName();
        List<String> hostNameTerms =  r.getTermsFromHost();
        terms.addAll(hostNameTerms);

        if(keywords != null) terms.addAll(keywords);
        if(host != null) terms.add(host);

        HashSet<String> terms2 = new HashSet<>();
        terms2.addAll(terms);


        if(!this.clusterToTermSet.containsKey(clusterNR)) {

            this.clusterToTermSet.put(clusterNR, new HashSet<String>());

        }

        Set<String> termSetForLable = this.clusterToTermSet.get(clusterNR);

        for(String s : terms2 ) {

            termSetForLable.add(s);



            if(this.termToDocs.containsKey(s)) {

                this.termToDocs.get(s).add(docLabel);
            } else {


                HashSet<String> newDocIdSet = new HashSet<>();
                newDocIdSet.add(docLabel);
                this.termToDocs.put(s,newDocIdSet);

            }



        }


    }



    public int getNrTerms() {


       return this.termToDocs.size();
    }

    public int getNrTermsInCluster(Integer lable) {

       return this.clusterToTermSet.get(lable).size();

    }

    public int getNrDocs() {

        //cache nr docs

        if(nrDocs == null) {
            //sannity check
            Set<String> totalDocs = new HashSet<>();


            for (Map.Entry<String, Set<String>> entry : this.termToDocs.entrySet()) {


                totalDocs.addAll(entry.getValue());

            }

            this.nrDocs = totalDocs.size();

            return this.nrDocs;

        } else {

            return this.nrDocs;
        }


    }


    public int getNrDocsInCluster(Integer lable) {

        return this.clusterLableToDocs.get(lable).size();
    }

    public Set<Integer> getCluserIDs() {


        return this.clusterLableToDocs.keySet();

    }


    public List<TermAndCorr> rankTermsForGovenClusterTFS(Integer clusterNr, Double a) {
        List<TermAndCorr> corr = new ArrayList<>();

        // |Ci|
        int numberOfDocsInCluster = getNrDocsInCluster(clusterNr);

        // |Cp|
        int numberOfDocsTotal = getNrDocs();


        //terms occurring in clusterNr
        Set<String> termsInCluster = this.clusterToTermSet.get(clusterNr);

        for(String term : termsInCluster) {

            //total docs in database ("parent") that contains the term , tf(term,Cp)
            Set<String> docLabelsContainingTerm = this.termToDocs.get(term);

           double expected = docLabelsContainingTerm.size() * (numberOfDocsInCluster/(double)numberOfDocsTotal); //tf(ti,Cp) * |cj|/|cp|


            Set<String> docLabelsInCluster = this.clusterLableToDocs.get(clusterNr);
            Set<String> intersection = new HashSet<>(docLabelsContainingTerm);
            intersection.retainAll(docLabelsInCluster);

            //docs in cluster Cj containing term tf(term,Cj)
            int nrDocsInClusterContainingTerm = intersection.size();

            double scaledOccurrences = nrDocsInClusterContainingTerm/(double)numberOfDocsInCluster;

           double score = Math.pow(scaledOccurrences,a) * Math.pow( nrDocsInClusterContainingTerm/expected,(1-a));

           corr.add( new TermAndCorr(term,score,0,0,0,0));

        }

        Collections.sort(corr,Collections.reverseOrder());
        return corr;


    }

    public List<TermAndCorr> rankTermsForGivenCluster(Integer lable) {


        //potential
        Set<String> termsInCluster = this.clusterToTermSet.get(lable);

        List<TermAndCorr> corr = new ArrayList<>();

        for(String term : termsInCluster) {


            Set<String> docLabelsContainingTerm = this.termToDocs.get(term);
            Set<String> docLabelsInCluster = this.clusterLableToDocs.get(lable);

            Set<String> intersection = new HashSet<String>(docLabelsContainingTerm);
            intersection.retainAll(docLabelsInCluster);

            int TP = intersection.size();

            Set<String> notInCButContainingT = new HashSet<>(docLabelsContainingTerm);

            notInCButContainingT.removeAll(docLabelsInCluster);

            int FN = notInCButContainingT.size();

            Set<String> inCbutNotContainingT = new HashSet<>(docLabelsInCluster);

            inCbutNotContainingT.removeAll(docLabelsContainingTerm);

            int FP = inCbutNotContainingT.size();

            Set<String> notInCAndNotContainingT = new HashSet<>(allDocIds);

            notInCAndNotContainingT.removeAll(docLabelsContainingTerm);
            notInCAndNotContainingT.removeAll(docLabelsInCluster);

            int TN = notInCAndNotContainingT.size();

            double numerator = ( (TP*TN) - (FN*FP) ) ;

            // might overflow!
            //int denominator = (TP+FN)*(FP+TN)*(TP+FP)*(FN+TN);

            double a = Math.sqrt(TP +  FN);
            double b = Math.sqrt(FP  +  TN);
            double c = Math.sqrt(TP  +  FP);
            double d = Math.sqrt(FN  +  TN);


            double squarerootDenominator = a*b*c*d;

            if( Double.isNaN(squarerootDenominator) ) System.out.println( term + "  WARNING IS NA!!!!!");
            if( Double.isNaN(squarerootDenominator) ) System.out.println( squarerootDenominator + " WARNING IS NA!!!!!: " + TP +" " +TN + " " +FN + " " +FP );
            if( Double.isNaN(squarerootDenominator) ) System.out.println("WARNING IS NA!!!!!");
            if( Double.isNaN(squarerootDenominator) ) System.out.println("WARNING IS NA!!!!!");


            corr.add(  new TermAndCorr(term,(numerator/squarerootDenominator),TP,TN,FN,FP)  );

        }


        Collections.sort(corr,Collections.reverseOrder());
        return corr;
    }




    public static void main(String[] arg) throws IOException {


        DocToCluster docToCluster = new DocToCluster("E:\\Desktop\\JSON_SWEPUB\\SimilarityEngSportSci.map");





    }


}

