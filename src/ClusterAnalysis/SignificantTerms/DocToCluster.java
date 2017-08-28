package ClusterAnalysis.SignificantTerms;

import SwePub.Record;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by crco0001 on 8/28/2017.
 */
public class DocToCluster {

    //docID to cluster lable

    private HashSet<Integer> allDocIds = new HashSet<>();

    private HashMap<Integer,Integer> docIdToClusterLable = new HashMap<>();

    //lookup cluster Lable to get all docsIds
    private HashMap<Integer,Set<Integer>> clusterLableToDocs = new HashMap<>();

    //lookup a term to get all docIds containing this term
    private HashMap<String,Set<Integer>> termToDocs = new HashMap<>();

    //lable to set of terms
    private HashMap<Integer,Set<String>> lableToTermSet = new HashMap<>();


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

            docIdToClusterLable.put( Integer.valueOf(parts[1]), Integer.valueOf(parts[4]) );

        }


        reader.close();

        System.out.println("Nr docs read from file: " + docIdToClusterLable.size());
    }


    public Integer getClusterId(Integer docID) {

        return docIdToClusterLable.get(docID);


    }


    public Set<Map.Entry<Integer,Integer>> getEntrySetDocToClusterLable() {

        return docIdToClusterLable.entrySet();

    }


    public void addRecord(Record r, String lang,int id) {

        //1. lable to -> doc ids

      //  Integer id = r.getMapDBKey();
        this.allDocIds.add(id);

        Integer lable = docIdToClusterLable.get( id );

        if( this.clusterLableToDocs.containsKey(lable) ) {

            this.clusterLableToDocs.get(lable).add( id );

        } else {

            HashSet newSetOfDocIDs = new HashSet();
            newSetOfDocIDs.add( id );

            this.clusterLableToDocs.put(lable,newSetOfDocIDs);

        }

        // term to doc ids

        List<String> terms = r.getLanguageSpecificTerms(lang);
        List<String> keywords = r.getUnkontrolledKkeywords();
        String host = r.getHostName();

        if(keywords != null) terms.addAll(keywords);
        if(host != null) terms.add(host);

        HashSet<String> terms2 = new HashSet<>();
        terms2.addAll(terms);


        if(!this.lableToTermSet.containsKey(lable)) {

            this.lableToTermSet.put(lable, new HashSet<String>());

        }

        Set<String> termSetForLable = this.lableToTermSet.get(lable);

        for(String s : terms2 ) {

            termSetForLable.add(s);



            if(this.termToDocs.containsKey(s)) {

                this.termToDocs.get(s).add(id);
            } else {


                HashSet<Integer> newDocIdSet = new HashSet<>();
                newDocIdSet.add(id);
                this.termToDocs.put(s,newDocIdSet);

            }



        }


    }


    public int getNrTerms() {


        return this.termToDocs.size();
    }

    public int getNrTermsInCluster(Integer lable) {

        return this.lableToTermSet.get(lable).size();

    }

    public int getNrDocs() {

        //sannity check
        Set<Integer> totalDocs = new HashSet<>();
        for(Map.Entry<String,Set<Integer>> entry : this.termToDocs.entrySet())  {

            totalDocs.addAll( entry.getValue() );

        }

        return totalDocs.size();
    }


    public int getNrDocsInCluster(Integer lable) {

        return this.clusterLableToDocs.get(lable).size();
    }

    public Set<Integer> getCluserIDs() {


        return this.clusterLableToDocs.keySet();

    }

    public List<TermAndCorr> rankTermsForGivenCluster(Integer lable) {


        //potential
        Set<String> termsInCluster = this.lableToTermSet.get(lable);

        List<TermAndCorr> corr = new ArrayList<>();

        for(String term : termsInCluster) {


            Set<Integer> docIdsContainingTerm = this.termToDocs.get(term);
            Set<Integer> docIdsInCluster = this.clusterLableToDocs.get(lable);

            Set<Integer> intersection = new HashSet<Integer>(docIdsContainingTerm);
            intersection.retainAll(docIdsInCluster);

            int TP = intersection.size();

            Set<Integer> notInCButContainingT = new HashSet<>(docIdsContainingTerm);

            notInCButContainingT.removeAll(docIdsInCluster);

            int FN = notInCButContainingT.size();

            Set<Integer> inCbutNotContainingT = new HashSet<>(docIdsInCluster);

            inCbutNotContainingT.removeAll(docIdsContainingTerm);

            int FP = inCbutNotContainingT.size();

            Set<Integer> notInCAndNotContainingT = new HashSet<>(allDocIds);

            notInCAndNotContainingT.removeAll(docIdsContainingTerm);
            notInCAndNotContainingT.removeAll(docIdsInCluster);

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



}
