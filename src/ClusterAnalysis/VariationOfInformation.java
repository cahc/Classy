package ClusterAnalysis;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

/**
 * Created by crco0001 on 8/25/2017.
 */
public class VariationOfInformation {



    /**


    https://en.wikipedia.org/wiki/Variation_of_information

    this is a normalized variation [0,1] where normalization factor is:  1/log(n)


     **/




    public static double getVI(int[] partition1, int[] partition2) throws Exception {


        if(partition1.length != partition2.length) throw new Exception("membership vectors have different lengths");

        boolean existsZeroP1 = Arrays.stream(partition1).anyMatch( x -> x==0);

        boolean existsZeroP2 = Arrays.stream(partition2).anyMatch( x -> x==0);

        if(!existsZeroP1)  throw new Exception("Partition 1 dont contaion 0");
        if(!existsZeroP2)  throw new Exception("Partition 1 dont contaion 0");


        double n = partition1.length;

        int np1 = Arrays.stream(partition1).max().getAsInt() +1; //zero indexed partition
        int np2 = Arrays.stream(partition2).max().getAsInt() +1; //zero indexed partition

       int[][] confusionMatrix = new int[np1][np2];


       for(int i=0; i<partition1.length; i++) {


           int c_p1 = partition1[i];
           int c_p2 = partition2[i];

           confusionMatrix[  c_p1  ][  c_p2 ] += 1;



       }


        Map<Integer, Long> ck_p1 = Arrays.stream(partition1).boxed()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));


        Map<Integer, Long> ck_p2 = Arrays.stream(partition2).boxed()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));



       double vi = 0;
       for(int i=0; i<np1; i++) {


                    for(int j=0; j<np2; j++) {


                        double r_ij = (confusionMatrix[i][j])/n;
                        if(r_ij == 0) continue;

                        double p_i =  ( ck_p1.get(i)) /n;
                        double q_j =  ( ck_p2.get(j)) /n;

                        vi += (        r_ij * ( Math.log( r_ij/p_i ) + Math.log( r_ij/q_j ) ) );


                    }




       }


       //return the normalized variant by multiplying by 1/log(n)

       return    -vi  * ( 1/Math.log(n) ) ;

    }





    public static void main(String[] arg) throws Exception {


        int[] part1 = {0,0,0,0,3,3,3,0,1,1,3,0,0,0,1,1,3,0,1,0,1,0,1,2,2,2,1,2,2,1,1,2,1,1};
        int[] part2 = {0,2,2,2,0,0,0,2,1,1,0,0,2,2,1,1,0,2,1,2,1,2,1,3,3,3,1,3,3,1,1,3,1,1};



        System.out.println( getVI(part1,part2) );


    }


}
