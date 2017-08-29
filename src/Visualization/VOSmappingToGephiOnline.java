package Visualization;

import Database.ModsDivaFileParser;
import SwePub.Record;

import javax.xml.stream.XMLStreamException;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by crco0001 on 8/28/2017.
 */
public class VOSmappingToGephiOnline {

   public static String xmlEscapeText(String t) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < t.length(); i++){
            char c = t.charAt(i);
            switch(c){
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '\"': sb.append("&quot;"); break;
                case '&': sb.append("&amp;"); break;
                case '\'': sb.append("&apos;"); break;
                default:
                    if(c>0x7e) {
                        sb.append("&#"+((int)c)+";");
                    }else
                        sb.append(c);
            }
        }
        return sb.toString();
    }



    public static String writeNode(VOSNode vosNode, DistinctColours distinctColours,List<Record> listOfRecords) {

        StringBuilder stringBuilder = new StringBuilder();

        Color color = distinctColours.getColor(  vosNode.getCluster()  );
        int r =color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        String title = listOfRecords.get( (vosNode.id-1) ).getTitle().toString();
        String host = listOfRecords.get( (vosNode.id-1) ).getHostName();
        String URI = listOfRecords.get( (vosNode.id-1) ).getURI();
        if(host == null) host = "not available";


       title = title.replaceAll("\"","");
       host = host.replaceAll("\"","");

       title = xmlEscapeText(title);
       host = xmlEscapeText(host);

        stringBuilder.append("<node id=\"").append( vosNode.getId() ).append("\">").append("\n");
        stringBuilder.append("<attvalues>").append("\n");

            stringBuilder.append("<attvalue for=\"Cluster\" value=\"").append(vosNode.getCluster()).append("\"/>").append("\n");
            stringBuilder.append("<attvalue for=\"Title\" value=\"").append( title ).append("\"/>").append("\n");
            stringBuilder.append("<attvalue for=\"Host\" value=\"").append( host ).append("\"/>").append("\n");
            stringBuilder.append("<attvalue for=\"URI\" value=\"").append( URI  ).append("\"/>").append("\n");

        stringBuilder.append("</attvalues>").append("\n");

        stringBuilder.append("<viz:size value=\"0.01\"></viz:size>").append("\n");


       stringBuilder.append("<viz:color r=\""+ r +"\" g=\""+ g +"\" b=\"" + b + "\"></viz:color>").append("\n");


        stringBuilder.append("<viz:position x=\""+ vosNode.getX() +"\" y=\""+  vosNode.getY()+ "\"></viz:position>").append("\n");


         stringBuilder.append("</node>").append("\n");


        return stringBuilder.toString();
    }

    public static void main(String[] args) throws IOException, XMLStreamException {


        DistinctColours distinctColours = new DistinctColours();

        if (args.length != 2) {
            System.out.println("mods.xml vosMapp.txt");
            System.exit(0);
        }


        ModsDivaFileParser modsDivaFileParser = new ModsDivaFileParser();

        System.out.println("Parsing XML");
        List<Record> recordList = modsDivaFileParser.parse(args[0]);


        ArrayList<Record> reducedRecordList = new ArrayList<>();

        for (Record r : recordList) {

            if (r.isFullEnglishText()) reducedRecordList.add(r);

        }

        recordList = null;


        BufferedReader reader = new BufferedReader(new FileReader( new File(args[1])));
        List<VOSNode> vosNodeList = new ArrayList<>();


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

           vosNodeList.add(  new VOSNode(Integer.valueOf(parts[0]), parts[1], Double.valueOf(parts[2]) ,Double.valueOf(parts[3]), Integer.valueOf(parts[4]) ) );

        }


        reader.close();

        System.out.println("Read : " + vosNodeList.size() +" VOSnodes" );





        BufferedWriter writer = new BufferedWriter( new FileWriter( "testing.gexf" ));


        String head ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<gexf xmlns=\"http://www.gexf.net/1.3\" version=\"1.3\" xmlns:viz=\"http://www.gexf.net/1.3/viz\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.gexf.net/1.3 http://www.gexf.net/1.3/gexf.xsd\">\n" +
                "  <meta lastmodifieddate=\"2017-08-28\">\n" +
                "    <creator>Gephi 0.9</creator>\n" +
                "    <description></description>\n" +
                "  </meta>\n" +
                "  <graph defaultedgetype=\"directed\" mode=\"static\">\n";

        writer.write(head);

        writer.write("<attributes class=\"node\">");
        writer.newLine();
        writer.write("<attribute id=\"Cluster\" title=\"Cluster\" type=\"string\"/>");
        writer.newLine();
        writer.write("<attribute id=\"Title\" title=\"Title\" type=\"string\"/>");
        writer.newLine();
        writer.write("<attribute id=\"Host\" title=\"Host\" type=\"string\"/>");
        writer.newLine();
        writer.write("<attribute id=\"URI\" title=\"URI\" type=\"string\"/>");
        writer.newLine();
        writer.write(" </attributes>");
        writer.newLine();
        writer.write("<nodes>");
        writer.newLine();
        for(VOSNode vosNode : vosNodeList) {

            writer.write( writeNode(vosNode,distinctColours,reducedRecordList) );
            writer.newLine();
        }


        writer.write("</nodes>");
        writer.newLine();
        writer.write("</graph>");
        writer.newLine();
        writer.write(" </gexf>");

        writer.flush();
        writer.close();

        /*
   <?xml version="1.0" encoding="UTF-8"?>
   <gexf xmlns="http://www.gexf.net/1.1draft" version="1.1" xmlns:viz="http://www.gexf.net/1.1draft/viz" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.gexf.net/1.1draft http://www.gexf.net/1.1draft/gexf.xsd">
   <meta lastmodifieddate="2011-09-05">
   <creator>Gephi 0.8</creator>
   <description></description>
   </meta>

  <graph defaultedgetype="undirected">

  <attributes class=”node”>
        <attribute id=”0” title=”cluster” type=”string”/>
        <attribute id=”1” title=”title” type=”string”/>

  </attributes>

  <nodes>







  </nodes>
  </graph>

  </gexf>

  */




    }



}
