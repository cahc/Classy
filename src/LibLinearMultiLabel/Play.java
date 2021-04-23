package LibLinearMultiLabel;


import com.koloboke.collect.map.ObjIntCursor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Play {


    public static void main(String[] arg) throws IOException {


      // List<TrainingPair> trainingPairList =  TrainingPair.load("E:\\Desktop\\JSON_SWEPUB\\multiLabelExperiment\\TrainingPairsEngLevel5.ser");


        //TermWeight termWeight = new TermWeight(false,true,0.05);

        BufferedWriter bufferedWriter = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( new File("E:\\Desktop\\JSON_SWEPUB\\multiLabelExperiment\\index.txt")), StandardCharsets.UTF_8) );

        TermWeight termWeight = TermWeight.load("E:\\Desktop\\JSON_SWEPUB\\multiLabelExperiment\\TermWeightingEntroypEngLevel5.ser");

        SimpleIndex simpleIndex = new SimpleIndex();

        simpleIndex.load("E:\\Desktop\\JSON_SWEPUB\\multiLabelExperiment\\simpleIndexEngLevel5.ser");

        for(ObjIntCursor<String> cur = simpleIndex.getMappingInIndex(); cur.moveNext(); ){

           bufferedWriter.write( cur.key() +"\t" +cur.value() +"\t" + "termweight:\t" +  termWeight.getWeight(cur.value(),0D) +"\t" + "TermWightWithBias\t" + termWeight.getWeight(cur.value()) );
           bufferedWriter.newLine();
        }


        bufferedWriter.flush();
        bufferedWriter.close();

    }
}
