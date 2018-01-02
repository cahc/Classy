package WebApp;

import Database.IndexAndGlobalTermWeights;
import Database.ModsOnlineParser;
import SwePub.ClassificationCategory;
import SwePub.HsvCodeToName;
import SwePub.Record;
import jsat.classifiers.CategoricalResults;
import jsat.classifiers.Classifier;
import jsat.classifiers.DataPoint;
import jsat.linear.SparseVector;
import jsat.linear.Vec;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Cristian on 2016-12-08.
 */
public class FetchFromDiVA extends HttpServlet  {

    private final String regex = "diva2:\\d{1,20}";

    private final Pattern r = Pattern.compile(regex);

    private  IndexAndGlobalTermWeights englishLevel5 = null;

    private Classifier classifierlevel5eng = null;

    private Classifier classifierLevel3swe = null;

    private  IndexAndGlobalTermWeights swedishLevel3 = null;

    private DecimalFormat df = new DecimalFormat("#0.00");


    String whereAmI = null;


    @Override
    public void init(ServletConfig config) throws ServletException {

        this.whereAmI = new File(".").getAbsolutePath();

        try {
            englishLevel5 = new IndexAndGlobalTermWeights("eng", 5);

            swedishLevel3 = new IndexAndGlobalTermWeights("swe", 3);

            englishLevel5.readFromMapDB();

            swedishLevel3.readFromMapDB();





        } catch (Throwable t) {

            t.printStackTrace();
            System.out.println("readFromMapDB throwed some shit!");

        }


        try {
            this.classifierlevel5eng = TrainAndPredict.HelperFunctions.readSerializedClassifier("classifier.eng.5.ser");
            this.classifierLevel3swe = TrainAndPredict.HelperFunctions.readSerializedClassifier("classifier.swe.3.ser");

        } catch (Throwable t) {
            t.printStackTrace();
            System.out.println("Could not read serialized model!");
        }



    }




    public String SendGetRequest(String divaNumber) throws IOException, XMLStreamException {


        String url2 = "http://www.diva-portal.org/smash/export.jsf?format=mods&aq=[[{\"publicationId\":\""+ divaNumber + "\"}]]&aqe=[]&aq2=[[]]&onlyFullText=false&noOfRows=2&sortOrder=title_sort_asc";


        URL obj = new URL(url2);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url2);
        System.out.println("Response Code : " + responseCode);

        if(responseCode == 500) return "DiVA svarade med \"Internal Server Error 500\". Har du angivit ett diva2-id som ej existerar?";

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        con.disconnect();

        //print result
        Record record = ModsOnlineParser.parse(response.toString());

        if(record == null) {

            return "DiVA returnerade ingen data. Kontrollera angivet diva2-id (feltyp #1)";
        }

        if( record.getTitle().size() == 0) {

            return "DiVA returnerade ingen data. Kontrollera angivet diva2-id (feltyp #2)";
        }

        //return "Title: " + record.getTitle() + " Supported language: " + record.containsSupportedLanguageText() +"\n\n" + record.toString();

        StringBuilder builder = new StringBuilder();

        builder.append("<p>Titel: ").append(record.getTitle()).append("<br>");
        //builder.append("innehåller svensk text: ").append(record.isContainsSwedish()).append("<br>");
        //builder.append("innehåller engelsk text: ").append(record.isContainsEnglish()).append("<br>");
        builder.append("<br>");
        //builder.append("Full record: ").append("<br>");
        //builder.append(record.toString());
        builder.append("Förslag:").append("<br>");
        builder.append("</p>");


        SparseVector vec = null;

        if(record.isContainsEnglish()) {

            //use english level 5
          vec = this.englishLevel5.getVecForUnSeenRecord(record);

            if(vec != null) {

                vec.normalize();
                int nnz = vec.nnz();
                //builder.append( this.englishLevel5.printSparseVector(vec)  );
               // builder.append("nnz:" + nnz);

                CategoricalResults result = this.classifierlevel5eng.classify( new DataPoint(vec) );

                int hsv = result.mostLikely();
                double prob = result.getProb(hsv);

                ClassificationCategory true_hsv =  HsvCodeToName.getCategoryInfo( IndexAndGlobalTermWeights.level5ToCategoryCodes.inverse().get(hsv)    );

                builder.append("<p>");
                builder.append("UKÄ/SCB: <b>" + true_hsv.getCode() + "</b> : " + true_hsv.getEng_description().replaceAll("-->","&rarr;") +  " (probability: " + df.format(prob) +")");
                builder.append("</p>");


                Vec probabilities = result.getVecView();
                List<ClassProbPair> classProbPairs = new ArrayList<>(5);

                for(int i=0; i < probabilities.length(); i++) {

                    if(i == hsv) continue;

                    if(probabilities.get(i) > 0.2) classProbPairs.add( new ClassProbPair(i,probabilities.get(i)));


                }

                Collections.sort(classProbPairs, Comparator.reverseOrder());


                for(int i=0; i<classProbPairs.size(); i++) {

                    ClassificationCategory true_hsv2 =  HsvCodeToName.getCategoryInfo( IndexAndGlobalTermWeights.level5ToCategoryCodes.inverse().get( classProbPairs.get(i).classCode   )    );
                    double probability = classProbPairs.get(i).probability;
                    builder.append("<p>");
                    builder.append("UKÄ/SCB: <b>" + true_hsv2.getCode() + "</b> : " + true_hsv2.getEng_description().replaceAll("-->","&rarr;") +  " (probability: " + df.format(probability) +")");
                    builder.append("</p>");



                }

                //builder.append(result.getVecView());



            }


        } else

        if(record.isContainsSwedish()) {

            vec = this.swedishLevel3.getVecForUnSeenRecord(record);

            if(vec != null) {

                vec.normalize();
                int nnz = vec.nnz();
               // builder.append("nnz:" + nnz);
                //builder.append( this.swedishLevel3.printSparseVector(vec)  );

                CategoricalResults result = this.classifierLevel3swe.classify( new DataPoint(vec) );

                int hsv = result.mostLikely();
                double prob = result.getProb(hsv);
                ClassificationCategory true_hsv =  HsvCodeToName.getCategoryInfo( IndexAndGlobalTermWeights.level3ToCategoryCodes.inverse().get(hsv)    );

                builder.append("<p>");
                builder.append("UKÄ/SCB: <b>" + true_hsv.getCode() + "</b> : " + true_hsv.getEng_description().replaceAll("-->","&rarr;") +  " (probability: " + df.format(prob) +")");
                builder.append("</p>");


                Vec probabilities = result.getVecView();
                List<ClassProbPair> classProbPairs = new ArrayList<>(5);

                for(int i=0; i < probabilities.length(); i++) {

                    if(i == hsv) continue;

                    if(probabilities.get(i) > 0.25) classProbPairs.add( new ClassProbPair(i,probabilities.get(i)));


                }

                Collections.sort(classProbPairs, Comparator.reverseOrder());


                for(int i=0; i<classProbPairs.size(); i++) {

                    ClassificationCategory true_hsv2 =  HsvCodeToName.getCategoryInfo( IndexAndGlobalTermWeights.level3ToCategoryCodes.inverse().get( classProbPairs.get(i).classCode   )    );
                    double probability = classProbPairs.get(i).probability;
                    builder.append("<p>");
                    builder.append("UKÄ/SCB: <b>" + true_hsv2.getCode() + "</b> : " + true_hsv2.getEng_description().replaceAll("-->","&rarr;") +  " (probability: " + df.format(probability) +")");
                    builder.append("</p>");



                }



                //builder.append(result.getVecView());
            }
        }


        return builder.toString();
    }







    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String status = request.getParameter("serverstatus");

        if(status != null) {
            response.setCharacterEncoding("UTF-8");
            PrintWriter out=response.getWriter();

            int index = 0;

            if(this.swedishLevel3 != null) index++;
            if(this.englishLevel5 != null) index++;

            int modeller = 0;
            if(this.classifierlevel5eng != null) modeller++;
            if(this.classifierlevel5eng != null) modeller++;


            String resp =  index + " index inlästa från " + whereAmI +". och: " + modeller;
            out.print(resp);
            out.flush();
            out.close();
            return;
        }



        response.setContentType("text/plain");  // Set content type of the response so that jQuery knows what it can expect.
        response.setCharacterEncoding("UTF-8"); // You want world domination, huh?

        PrintWriter out = response.getWriter();

        // Now create matcher object.
        Matcher m = r.matcher(request.getParameterValues("divaid")[0]);
        if (m.find( )) {
           String divaNumber = m.group(0);

            //Connect to DiVA Portal
            String xmlRecived = "null";
            try {
                xmlRecived = SendGetRequest(divaNumber);
            } catch (Exception e) {
                e.printStackTrace();
            }

            out.write(xmlRecived);



        } else {
            out.write("Förefaller inte vara ett giltigt diva2-id.");

        }


    }








    public static void main(String[] arg) throws IOException, XMLStreamException {

        //System.out.println( SendGetRequest("diva2:452290") );
    }


}
