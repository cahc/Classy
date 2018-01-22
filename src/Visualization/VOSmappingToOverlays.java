package Visualization;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by crco0001 on 1/22/2018.
 */
public class VOSmappingToOverlays {


    /*

    Read a map file, and create N new files, where N is the number of clusters:

    1) colour custer X red, all others grey
    2) set weight 2 on cluster X, all others 1

    Then run like so:

    java -jar VOSviewer.jar -map MappingFileRes2.27_overlay.txt -overlay_visualization -max_label_length 0 -scale 0.5 -save_screenshot_emf cluster6.emf





    id	label	x	y	cluster	weight<Links>	weight<Total link strength>	red	green   blue	weight<highlight>
    1	0	0.7	0.0451	8	20	4.5717	192	192	192	1
    2	1	-0.3165	0.3875	6	22	3.4261	255	0	0	2


     */




    public static void main(String[] arg) throws IOException {


        if(arg.length != 1) {System.out.println("Supply a map file (arg should = 1)"); System.exit(0); }

        File file = new File(arg[0]);

        if(!file.exists()) {System.out.println("Supply a map file (supplyed non existing"); System.exit(0); }

        BufferedReader reader = new BufferedReader( new FileReader( file ));
        //check headers
        String header = reader.readLine();

        if( header == null ) {System.out.println("Supply a map file (null when checking header)"); System.exit(0); }

         //   id	label	x	y	cluster	weight<Links>	weight<Total link strength>	red	green	blue	weight<highlight>

        String[] pars = header.split("\t");

        int idInx = -1;
        int labelInx = -1;
        int xInx = -1;
        int yInx = -1;
        int clusteringInx = -1;

        for(int i=0; i<pars.length; i++) {

            if ("id".equals( pars[i])) idInx = i;
            if ("label".equals( pars[i])) labelInx = i;
            if ("x".equals( pars[i])) xInx = i;
            if ("y".equals( pars[i])) yInx = i;
            if ("cluster".equals( pars[i])) clusteringInx = i;

            }


        if(idInx == -1 || labelInx == -1 || xInx == -1 ||  yInx == -1 || clusteringInx == -1)   {System.out.println("Supply a map file, unexpected header"); System.exit(0); }


        List<String> id = new ArrayList<>();
        List<String> label = new ArrayList<>();
        List<String> x = new ArrayList<>();
        List<String> y = new ArrayList<>();
        List<Integer> cluster = new ArrayList<>();

        String line;
        while( (line = reader.readLine()) != null ) {

          String[] parts =  line.split("\t");

          id.add( parts[idInx]  );
          label.add( parts[labelInx]  );
          x.add( parts[xInx]  );
          y.add( parts[yInx]  );
          cluster.add( Integer.valueOf(parts[clusteringInx])  );

        }

        System.out.println("# items: " + id.size());

        List<Integer> clusterIds = cluster.stream().distinct().collect(Collectors.toList());
        System.out.println("# clusters  " + clusterIds.size() );
        Collections.sort(clusterIds);

        reader.close();

        for(Integer s : clusterIds) {

            System.out.println(s);
        }



        }





    }


